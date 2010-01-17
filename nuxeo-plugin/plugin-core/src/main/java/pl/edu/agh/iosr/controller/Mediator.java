package pl.edu.agh.iosr.controller;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.io.File;
import java.util.List;

import javax.activation.DataHandler;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.conversion.XliffConverter;
import pl.edu.agh.iosr.exceptions.DataInconsistencyException;
import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.Quality;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.model.TranslationOrder.RequestState;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.TranslationStatus;
import pl.edu.agh.iosr.persistence.PersistenceTest;
import pl.edu.agh.iosr.services.RepositoryProxyService;
import pl.edu.agh.iosr.services.TranslationOrderService;
import pl.edu.agh.iosr.services.TranslationServicesConfigService;
import pl.edu.agh.iosr.services.ValidationService;
import pl.edu.agh.iosr.ws.RemoteWSInvoker;

/**
 * Implementacja wzorca projektowego odpowiadającego za centralizację logiki
 * sterowania pracą i koordynacją komponentów. Redukuje stopień sprzężenia.
 * Zawiera głównie logikę przepływu zamówień przekładu.
 * */

@Name("mediator")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class Mediator {

	@In(create = true)
	private XliffConverter xliffConverter;

	@In(create = true)
	private ValidationService validationService;

	@In(create = true, value = "#{repositoryProxyService}")
	private RepositoryProxyService documentAccessService;

	@In(create = true, value = "#{translationOrderService}")
	private TranslationOrderService translationOrderService;

	@In(create = true, value = "#{translationServicesConfigService}")
	private TranslationServicesConfigService translationServicesConfigService;

	@In(create = true, value = "#{remoteWSInvoker}")
	private RemoteWSInvoker remoteWSInvoker;

	@In(create = true)
	private PersistenceTest persistenceTest;

	/**
	 * Inicjalizacja mediatora. Zawiera testy intergracyjne DAO oraz inicjuje
	 * bazę przykładową konfiguracją web serwisu tłumaczącego.
	 * */
	@Create
	public void init() {
		persistenceTest.testTranslationOrder();
		persistenceTest.testServicesDescriptions();

		/* konfiguracja jednego początkowego web servisu */
		TranslationServiceDescription wsd = new TranslationServiceDescription();
		wsd.getSupportedLangPairs().add(new LangPair("pl", "en"));
		wsd.getSupportedLangPairs().add(new LangPair("en", "pl"));
		wsd.setDescription("google translator");
		wsd
				.setEndpoint("http://localhost:8090/cxf-googletranslator-ws/services/translator");
		wsd.setName("Google");
		wsd.getSupportedQualities().add(
				new Quality("google translator quality"));

		List<TranslationServiceDescription> list = translationServicesConfigService
				.getTranslationServices();
		for (TranslationServiceDescription t : list) {
			if (t.getName().equals("Google")) {
				return;
			}
		}
		translationServicesConfigService.saveOrUpdateTranslationService(wsd);

	}

	/**
	 * Uogólniona obsługa błędów workflowa.
	 * 
	 * W razie niepowodzenia umieszczamy stosowną informacje w obiekcie order
	 * ,logujemy błąd i zapisujemy order.
	 * */
	private void cancelOrder(TranslationOrder order, Exception e) {
		e.printStackTrace();
		log(this.getClass(), "Failed to submit translation order. "
				+ e.getMessage());
		order.markAsFailed(e.getLocalizedMessage());
		try {
			if (order != null) {
				translationOrderService.saveOrUpdateTranslationOrder(order);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Pierwszy element w przepływie zamówień na translacje. Obiekt
	 * <code>order</code> jest persystowany oraz poddawany konwersji. Wołane
	 * przez komponenty warstwy prezentacji, po złożeniu zamówienia przez
	 * użytkownika.
	 * 
	 * @param order
	 *            Zamówienie, które będzie przetwarzane.
	 * */
	public void beginTranslation(TranslationOrder order) {
		try {
			validationService.validateOrder(order,
					RequestState.BEFORE_PROCESSING);

			// ustawiamy status w tym miejscu na konwersje
			// konwerter nie przeprowadza zadnej kontroli
			order.nextState();
			translationOrderService.saveOrUpdateTranslationOrder(order);

			TranslationServiceDescription tsDescription = translationServicesConfigService
					.getTranslationService(order.getWsId());
			// testowe wywoalnie invokera google translatora
			// log(this.getClass(), "before calling translate");

			// remoteWSInvoker.testInvoke(tsDescription, order, null);

			// log(this.getClass(), "after calling translate");

			String fileExtension = "";// documentAccessService.getFileExtension(order.getSourceDocument().getDocumentModel().getRef());
			// - to powodowalo blad kompilacji
			if (validationService.isConversionNeeded(fileExtension,
					tsDescription)) {
				xliffConverter.convert(order);
				// xliffConverter.reConvert(order);
			}
			else {
				performExactTranslation(order);
			}

		}
		catch (Exception e) {
			cancelOrder(order, e);
		}

	}

	/**
	 * Zamówienie opisywane przez obiekt <code>order</code> jest persystowany
	 * oraz zlecane jest jego właściwie tłumaczenie poprzez delegację do
	 * <code>RemoteWsInvoker</code>.
	 * 
	 * Wołane przez mechanizmy konwersji, po zakończeniu tego procesu.
	 * 
	 * @param order
	 *            Zamówienie, które będzie przetwarzane.
	 * */
	public void performExactTranslation(TranslationOrder order) {

		log(this.getClass(), "PERFORM EXACT TRANSLATION CALLED");
		try {

			validationService.validateOrder(order,
					RequestState.UNDER_CONVERSION);

			// ustawiamy status w tym miejscu na wyslany do tlumaczenia
			// remoteWSinvoker nie przeprowadza zadnej kontroli workflowa
			order.nextState();
			translationOrderService.saveOrUpdateTranslationOrder(order);

			remoteWSInvoker.traslateAsync(translationServicesConfigService
					.getTranslationService(order.getWsId()), order, null); // ???
			// documentAccessService.getFile(order.getSourceDocument())
			// po co sourcedocument?

		}
		catch (Exception e) {
			cancelOrder(order, e);
		}

	}

	/**
	 * Zamówienie opisywane przez obiekt <code>order</code> zostało przetworzone
	 * przez zewnętrzne serwisu tłumaczące. Jest persystowany oraz zlecana jest
	 * rekonwersja treści z niego uzyskanej.
	 * 
	 * Wołane przez obsługę web service'u przyjmującego przetłumaczone treści.
	 * 
	 * @param id
	 *            - identyfikator zamówienia
	 * @param resultFileDh
	 *            - przetłumaczona treść
	 * */
	public void deliverTranslationResult(Long id, DataHandler resultFileDh) {
		TranslationOrder order = translationOrderService
				.getTranslationOrder(id);

		if (order != null) {
			try {

				validationService.validateOrder(order,
						RequestState.UNDER_PROCESSING);

				// w trakcie rekonwersji
				order.nextState();

				// Utworzenie pliku z wynikiem
				File resultFile = order.saveResultFile(resultFileDh);

				if (validationService.isReconversionNeeded(order)) {

					translationOrderService.saveOrUpdateTranslationOrder(order);

					xliffConverter.reConvert(order);
				}
				else {

					// successful
					order.nextState();

					translationOrderService.saveOrUpdateTranslationOrder(order);

					documentAccessService.saveFile(order, resultFile);

				}

			}
			catch (Exception e) {
				cancelOrder(order, e);
			}
		}
	}

	/**
	 * Finalizacja zamówienia opisywanego przez obiekt <code>order</code>.
	 * Walidacja oraz ostateczne utrwalenie w bazie danych.
	 * 
	 * @param order
	 *            Zamówienie, które będzie przetwarzane.
	 * */
	public void completeTranslation(TranslationOrder order) {

		try {

			validationService.validateOrder(order,
					RequestState.UNDER_RECONVERSION);

			// przeklad dokonany
			order.nextState();

			translationOrderService.saveOrUpdateTranslationOrder(order);

		}
		catch (Exception e) {
			cancelOrder(order, e);
		}

	}

	public void updateTranslationStatus(Long orderId, TranslationStatus status) {
		TranslationOrder order = translationOrderService
				.getTranslationOrder(orderId);
		order.setStatus(status);
		try {
			translationOrderService.saveOrUpdateTranslationOrder(order);
		}
		catch (DataInconsistencyException e) {
			e.printStackTrace();
		}
	}

}
