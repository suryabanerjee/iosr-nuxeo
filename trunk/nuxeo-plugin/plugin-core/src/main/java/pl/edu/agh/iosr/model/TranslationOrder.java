package pl.edu.agh.iosr.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.nuxeo.ecm.core.api.DocumentRef;

import pl.edu.agh.iosr.exceptions.WorkflowException;
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
@Table(name="TranslationOrder")
public class TranslationOrder implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static  long serialVersionUID = -4937919332325578161L;


	public enum RequestState {
		/**
		 * Tuż po utworzeniu, prawdopodobnie nikt nigdy nie zobaczy zamówienia
		 * w takim stanie
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
	
	@Enumerated(EnumType.STRING)
	@Column(name="state")
	private RequestState state = RequestState.BEFORE_PROCESSING;
	
	/**
	 * Referencja do dokumentu źródłowego
	 * */
	@Lob
	@Column(name="sourceDocument")
	private  DocumentRef sourceDocument;
	
	/**
	 * Referencja do doumentu wynikowego
	 * */
	@Lob
	@Column(name="destinationDocument")
	private DocumentRef destinationDocument;
	
	/**
	 * Wiadomo co
	 * */
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="from", column=@Column(name="langFrom")),
		@AttributeOverride(name="to", column=@Column(name="langTo"))
	})
	private  LangPair langPair;
	
	/**
	 * Nazwa dokumentu wynikowego
	 * 
	 * sam dokument może jeszcze nie istnieć, ale trzeba znać jego nazwe
	 * */
	@Column(name="destinationDocumentName")
	private  String destinationDocumentName;
	
	/**
	 * unikalny numer zamówienie
	 * */
	@Id
	@Column(name="id")
	private  Long requestId = IosrRandomGenerator.nextLong();
	
	/**
	 * Jakość zleconego tłumaczenia
	 * */
	@Column(name="quality")
	private  String quality;
	
	/**
	 * identyfikator web servisu
	 * */
	@Column(name="wsId")
	private  Long wsId;
	
	/**
	 * Lokalizacja pliku, bądź zawartość skeletona
	 * */
	@Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name="skeleton")
	private Serializable skeleton;
	
	/**
	 * Lokalizacja pliku, bądź zawartość xliffa
	 * */
	@Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name="xliff")
	private Serializable xliff;
	
	/**
	 * Wiadomość, jakby zaszła jakaś konieczność, np. błąd
	 * */
	@Column(name="message")
	private String message;
	
	/**
	 * Detekcja języka
	 * */
	@Column(name="languageDetection")
	private  boolean languageDetection;
	
	/**
	 * daty, kiedy co się stało,
	 * 
	 * można odczytać tylko poprzez toString
	 * */
	@Lob
	@Column(name="timeStamps")
	private  Date[] timeStamps = new Date[RequestState.values().length];
	
	/**
	 * dla ulatwienia przeciazamy hehe
	 * */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("\tRequest no: " + requestId + "\n");
		result.append("\tSource language: " + langPair.getFrom() + "\n");
		result.append("\tTarget language: " + langPair.getTo() + "\n");
		
		result.append("\tdocument to translate: " + sourceDocument.reference().toString());
		result.append("\tactions happened in lifecycle:");
		for (int i = 0; i < timeStamps.length; ++i) {
			result.append("\t\t" + RequestState.values()[i] + ": " + timeStamps[i]);
		}
		
		return result.toString();
	}

	/**
	 * Konstruktor tylko na potrzeby na mechanizmów persystencji.
	 * */
	public TranslationOrder() {}
	
	// konstruktor
	public TranslationOrder(DocumentRef sourceDocument, LangPair langPair,
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
	 * Pozwala ustawiać daty tych legendarnych wydarzeń
	 * <br>
	 * Pozwala na walidacje, np, aby dokument usiągnął
	 * status SUCCEDDED musi zawierac referencje do
	 * plików wynikowych (naturalnie nie jest to zaimplementowane)
	 * */
	public void nextState() throws WorkflowException {
		int o = state.ordinal();
		if (o >= RequestState.values().length) {
			throw new WorkflowException("Last stage of worklow has been already obtained.");
		}
		if (o == RequestState.SUCCEEDED.ordinal()) {
			throw new WorkflowException("Document already marked as successfully translated.");
		}
		else {
			state = RequestState.values()[o++];
			timeStamps[o] = new Date();
		}
	}
	
	/**
	 * Coś się zepsuło - najlepiej odznaczyć to w ten sposób, bo
	 * możliwe że zostaną podjęte jakieś specjalne działa, np logowanie!
	 * */
	public void markAsFailed(String failureReason) {
		this.state = RequestState.FAILED;
		message = failureReason;
		IosrLogger.log(this.getClass(), "Failed to translate document: " +
				sourceDocument.reference(), Level.FATAL);
	}
	
	// gettery i setter
	
	public RequestState getState() {
		return state;
	}


	public DocumentRef getDestinationDocument() {
		return destinationDocument;
	}


	public void setDestinationDocument(DocumentRef destinationDocument) {
		this.destinationDocument = destinationDocument;
	}


	public Serializable getSkeleton() {
		return skeleton;
	}


	public void setSkeleton(Serializable skeleton) {
		this.skeleton = skeleton;
	}


	public Serializable getXliff() {
		return xliff;
	}


	public void setXliff(Serializable xliff) {
		this.xliff = xliff;
	}


	public DocumentRef getSourceDocument() {
		return sourceDocument;
	}


	public LangPair getLangPair() {
		return langPair;
	}


	public String getDestinationDocumentName() {
		return destinationDocumentName;
	}


	public Long getRequestId() {
		return requestId;
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
	


	public boolean isLanguageDetection() {
		return languageDetection;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
