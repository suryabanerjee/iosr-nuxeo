package pl.edu.agh.iosr.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import pl.edu.agh.iosr.controller.EnrichedFile;

/**
 * Obiekt używany do formułowania zamówień na przykład przez klientów
 * 
 * Jego cykl życia wygląda tak:
 * - tworzony w backing beanach edycji na poziomie gui
 * - wysyłany do mediatora w celu realizacji
 * - utrwalenie (możliwe pobranie stanu realizacji zleceń) i przetwarzanie
 * */
public class TranslationRequest {

	public enum RequestState {
		/**
		 * Tuż po utworzeniu, prawdopodobnie nikt nigdy nie zobaczy zamówienia
		 * w takim stanie
		 * */
		UNDER_CONTRUCTION,
		
		/**
		 * Zamówienie sformułowane, oczekuje na rozpoczęcie przetwarzania
		 * Gdyby na przykład miały miejsce jakieś przykre synchroniczne wywołania
		 * persystencja, pliki, to może się okazać, że zamówienie czeka
		 * */
		SENT,
		
		/**
		 * Nietrudno się domyśleć
		 * Zamówienie w tym stanie powinno być już utrwalone
		 * */
		UNDER_PROCESSING,
		
		/**
		 * Jeszcze łatwiej się domyśleć
		 * Powinno być utrwalone
		 * */
		SUCCEEDED,
		
		/**
		 * cóż, kicha
		 * Powinno być utrwalone
		 * */
		FAILED
	}
	
	RequestState state = RequestState.UNDER_CONTRUCTION;
	
	private String author;
	private LangPair langPair;
	
	/**
	 * unikalny numer zamówienie, fajna byłaby fajniejsza metoda :]
	 * */
	private final String requestId = String.valueOf(new Date().getTime());
	
	/**
	 * Prawdopodobnie lista referencji do dokumentów
	 * */
	private List<EnrichedFile> documents = new LinkedList<EnrichedFile>();
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LangPair getLangPair() {
		return langPair;
	}

	public void setLangPair(LangPair langPair) {
		this.langPair = langPair;
	}

	public RequestState getState() {
		return state;
	}

	public void setState(RequestState state) {
		this.state = state;
	}

	public String getRequestId() {
		return requestId;
	}

	public List<EnrichedFile> getDocuments() {
		return documents;
	}

	public void setDocuments(List<EnrichedFile> documents) {
		this.documents = documents;
	}

	/**
	 * dla ulatwienia przeciazamy hehe
	 * */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("\tRequest no: " + requestId + "\n");
		result.append("\tauthor: " + author + "\n");
		result.append("\tSource language: " + langPair.getFrom() + "\n");
		result.append("\tTarget language: " + langPair.getTo() + "\n");
		
		result.append("\t" + documents.size() + " document to translate:\n");
		for (EnrichedFile ef : documents) {
			result.append("\t\t" + ef.getDocumentModel().getName() + " (-> "
					+ ef.getTargetName() + ")\n");
		}
		
		return result.toString();
	}
}
