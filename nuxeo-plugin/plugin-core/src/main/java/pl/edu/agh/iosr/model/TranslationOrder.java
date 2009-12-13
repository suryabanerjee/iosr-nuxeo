package pl.edu.agh.iosr.model;

import java.util.Date;
import java.util.logging.Level;

import org.nuxeo.ecm.core.api.DocumentRef;

import pl.edu.agh.iosr.exceptions.WorkflowException;
import pl.edu.agh.iosr.util.IosrLogger;
import pl.edu.agh.iosr.util.IosrRandomGenerator;

/**
 * Obiekt używany do formułowania zamówień na przykład przez klientów
 *
 * Przechowuje informacje o stanie tłumaczenia dokładnie jednego dokumentu
 *
 * Przez cały czas powinien być utrwalany.
 * */
public class TranslationOrder {

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
	
	RequestState state = RequestState.BEFORE_PROCESSING;
	
	/**
	 * Referencja do dokumentu źródłowego
	 * */
	private final DocumentRef sourceDocument;
	
	/**
	 * Referencja do doumentu wynikowego
	 * */
	private DocumentRef destinationDocument;
	
	/**
	 * Wiadomo co
	 * */
	private final LangPair langPair;
	
	/**
	 * Nazwa dokumentu wynikowego
	 * 
	 * sam dokument może jeszcze nie istnieć, ale trzeba znać jego nazwe
	 * */
	private final String destinationDocumentName;
	
	/**
	 * unikalny numer zamówienie
	 * */
	private final Long requestId = IosrRandomGenerator.nextLong();
	
	/**
	 * Jakość zleconego tłumaczenia
	 * */
	private final String quality;
	
	/**
	 * identyfikator web servisu
	 * */
	private final Long wsId;
	
	/**
	 * Lokalizacja pliku, bądź zawartość skeletona
	 * */
	private Object skeleton;
	
	/**
	 * Lokalizacja pliku, bądź zawartość xliffa
	 * */
	private Object xliff;
	
	/**
	 * Wiadomość, jakby zaszła jakaś konieczność, np. błąd
	 * */
	private String message;
	
	/**
	 * Detekcja języka
	 * */
	private final boolean languageDetection;
	
	/**
	 * daty, kiedy co się stało,
	 * 
	 * można odczytać tylko poprzez toString
	 * */
	private final Date[] timeStamps = new Date[RequestState.values().length];
	
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
				sourceDocument.reference(), Level.SEVERE);
	}
	
	// gettery i setter, naturalnie nie wszystkie, finale w konstruktorze
	
	public RequestState getState() {
		return state;
	}


	public DocumentRef getDestinationDocument() {
		return destinationDocument;
	}


	public void setDestinationDocument(DocumentRef destinationDocument) {
		this.destinationDocument = destinationDocument;
	}


	public Object getSkeleton() {
		return skeleton;
	}


	public void setSkeleton(Object skeleton) {
		this.skeleton = skeleton;
	}


	public Object getXliff() {
		return xliff;
	}


	public void setXliff(Object xliff) {
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
