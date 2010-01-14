package pl.edu.agh.iosr.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.activation.DataHandler;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import pl.edu.agh.iosr.exceptions.WorkflowException;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.TranslationStatus;
import pl.edu.agh.iosr.util.IosrLogger;
import pl.edu.agh.iosr.util.IosrRandomGenerator;
import pl.edu.agh.iosr.util.IosrLogger.Level;

/**
 * Obiekt używany do formułowania zamówień na przykład przez klientów
 * 
 * Przechowuje informacje o stanie tłumaczenia dokładnie jednego dokumentu
 * 
 * Przez cały czas powinien być utrwalany.
 * */
@Entity
@Table(name = "TRANSLATION_ORDER")
public class TranslationOrder implements java.io.Serializable {

	private static final long serialVersionUID = -6226478864052959723L;

	public enum RequestState {
		/**
		 * Tuż po utworzeniu, prawdopodobnie nikt nigdy nie zobaczy zamówienia w
		 * takim stanie
		 * */
		BEFORE_PROCESSING,

		/**
		 * Konwertowane
		 * */
		UNDER_CONVERSION,

		/**
		 * Nietrudno się domyśleć
		 * */
		UNDER_PROCESSING,

		/**
		 * Jeszcze łatwiej się domyśleć
		 * */
		UNDER_RECONVERSION,

		SUCCEEDED,

		/**
		 * cóż, kicha
		 * */
		FAILED
	}

	private RequestState state = RequestState.BEFORE_PROCESSING;

	/**
	 * Referencja do dokumentu źródłowego
	 * */
	private DocumentRefWrapper sourceDocument;

	/**
	 * Referencja do dokumentu wynikowego
	 * */
	private DocumentRefWrapper destinationDocument;

	/**
	 * Wiadomo co
	 * */
	private LangPair langPair;

	/**
	 * Nazwa dokumentu wynikowego
	 * 
	 * sam dokument może jeszcze nie istnieć, ale trzeba znać jego nazwe
	 * */
	private String destinationDocumentName;

	/**
	 * unikalny numer zamówienie
	 * */
	private Long requestId = IosrRandomGenerator.nextLong();

	/**
	 * Jakość zleconego tłumaczenia
	 * */
	private String quality;

	/**
	 * identyfikator web servisu
	 * */
	private Long wsId;

	/**
	 * Lokalizacja pliku, bądź zawartość skeletona
	 * */
	private Serializable skeleton;

	/**
	 * Lokalizacja pliku, bądź zawartość xliffa
	 * */
	private String xliff;

	/**
	 * Wiadomość, jakby zaszła jakaś konieczność, np. błąd
	 * */
	private String message;

	/**
	 * Detekcja języka
	 * */
	private boolean languageDetection;

	/**
	 * daty, kiedy co się stało,
	 * 
	 * można odczytać tylko poprzez toString
	 * */
	private Date[] timeStamps = new Date[RequestState.values().length];
	
	/**
	 * zawiera status okreslajacy postep translacji
	 * */
	private TranslationStatus status = TranslationStatus.NOT_STARTED;

	/**
	 * dla ulatwienia przeciazamy hehe
	 * */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("\tRequest no: " + requestId + "\n");
		if (langPair != null) {
			result
					.append("\tSource language: " + langPair.getFromLang()
							+ "\n");
			result.append("\tTarget language: " + langPair.getToLang() + "\n");
		}
		else {
			result.append("\tlanguages unknown\n");
		}
		result.append("\tstate: " + state);
		result.append("\tdocument to translate: " + sourceDocument);
		result.append("\tdestination document: " + destinationDocument);
		result.append("\tactions happened in lifecycle:");
		for (int i = 0; i < timeStamps.length; ++i) {
			result.append("\t\t" + RequestState.values()[i] + ": "
					+ timeStamps[i] + "\n");
		}

		return result.toString();
	}

	/**
	 * Konstruktor tylko na potrzeby na mechanizmów persystencji.
	 * */
	public TranslationOrder() {
		timeStamps[0] = new Date();
	}

	// konstruktor
	public TranslationOrder(DocumentRefWrapper sourceDocument, LangPair langPair,
			String destinationDocumentName, String quality, Long wsId,
			boolean languageDetection) {
		super();
		this.sourceDocument = sourceDocument;
		this.langPair = langPair;
		this.destinationDocumentName = destinationDocumentName;
		this.quality = quality;
		this.wsId = wsId;
		this.languageDetection = languageDetection;
		timeStamps[0] = new Date();
	}

	/**
	 * Ustawia następny stan w przepływie dokumentów
	 * 
	 * <br>
	 * Pozwala ustawiać daty tych legendarnych wydarzeń <br>
	 * Pozwala na walidacje, np, aby dokument usiągnął status SUCCEDDED musi
	 * zawierac referencje do plików wynikowych (naturalnie nie jest to
	 * zaimplementowane)
	 * */
	public void nextState() throws WorkflowException {
		int o = state.ordinal();
		if (o >= RequestState.values().length) {
			throw new WorkflowException(
					"Last stage of worklow has been already obtained.");
		}
		if (o == RequestState.SUCCEEDED.ordinal()) {
			throw new WorkflowException(
					"Document already marked as successfully translated.");
		}
		else {
			timeStamps[o++] = new Date();
			state = RequestState.values()[o];
		}
	}

	/**
	 * Coś się zepsuło - najlepiej odznaczyć to w ten sposób, bo możliwe że
	 * zostaną podjęte jakieś specjalne działa, np logowanie!
	 * */
	public void markAsFailed(String failureReason) {
		this.state = RequestState.FAILED;
		message = failureReason;
		IosrLogger.log(this.getClass(), "Failed to translate document: "
				+ sourceDocument.getName(), Level.FATAL);
	}
	
	/**
	 * przy zwrocie wyniku translacji, zapisuje xliffa do pliku i zwraca go jako wynik
	 * */
	
	public File saveResultFile(DataHandler dh) throws IOException{
		/*File xliff = new File(getXliff());
		String name = xliff.getName();
		name = name.replaceFirst(".old", "");*/
		
		File xliffResult = new File(getXliff() + ".transl");
	    if(!xliffResult.exists())
	    	xliffResult.createNewFile();
	    
		FileOutputStream out = new FileOutputStream(xliffResult);
		
		dh.writeTo(out);
    	out.flush();
    	out.close();
    	
    	return xliffResult;
	}
	
	/**
	 * uaktualnia status okreslajacy postep translacji
	 * */
	public void updateStatus(TranslationStatus status){
		
		this.status = status;
	}

	// gettery i setter

	@Id
	public Long getRequestId() {
		return requestId;
	}
	
	@Enumerated(EnumType.STRING)
	public RequestState getState() {
		return state;
	}

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="name", column=@Column(name="distName")),
		@AttributeOverride(name="path", column=@Column(name="distPath")),
		@AttributeOverride(name="type", column=@Column(name="distType"))
	})
	public DocumentRefWrapper getDestinationDocument() {
		return destinationDocument;
	}

	public void setDestinationDocument(DocumentRefWrapper destinationDocument) {
		this.destinationDocument = destinationDocument;
	}

	@Transient
	public Serializable getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(Serializable skeleton) {
		this.skeleton = skeleton;
	}

	public String getXliff() {
		return xliff;
	}

	public void setXliff(String xliff) {
		this.xliff = xliff;
	}

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="name", column=@Column(name="srcName")),
		@AttributeOverride(name="path", column=@Column(name="srcPath")),
		@AttributeOverride(name="type", column=@Column(name="srcType"))
	})
	public DocumentRefWrapper getSourceDocument() {
		return sourceDocument;
	}

	@OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, optional=true)
	public LangPair getLangPair() {
		return langPair;
	}

	public String getDestinationDocumentName() {
		return destinationDocumentName;
	}

	
	public String getQuality() {
		return quality;
	}

	public Long getWsId() {
		return wsId;
	}

	public String getMessage() {
		return message;
	}

	public boolean getLanguageDetection() {
		return languageDetection;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Lob
	public Date[] getTimeStamps() {
		return timeStamps;
	}

	public void setTimeStamps(Date[] timeStamps) {
		this.timeStamps = timeStamps;
	}

	public void setState(RequestState state) {
		this.state = state;
	}

	public void setSourceDocument(DocumentRefWrapper sourceDocument) {
		this.sourceDocument = sourceDocument;
	}

	public void setLangPair(LangPair langPair) {
		this.langPair = langPair;
	}

	public void setDestinationDocumentName(String destinationDocumentName) {
		this.destinationDocumentName = destinationDocumentName;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public void setWsId(Long wsId) {
		this.wsId = wsId;
	}

	public void setLanguageDetection(boolean languageDetection) {
		this.languageDetection = languageDetection;
	}
	
	/* na potrzeby wyswietlania */
	@Transient public Date getBefore() {return timeStamps[0];}
	@Transient public Date getConversion() {return timeStamps[1];}
	@Transient public Date getProcessing() {return timeStamps[2];}
	@Transient public Date getReconversion() {return timeStamps[3];}
	@Transient public Date getSucceeded() {return timeStamps[4];}
	@Transient public Date getFailed() {return timeStamps[5];}

}
