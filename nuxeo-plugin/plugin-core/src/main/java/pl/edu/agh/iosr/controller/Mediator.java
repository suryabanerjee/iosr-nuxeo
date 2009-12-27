package pl.edu.agh.iosr.controller;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.io.File;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.conversion.XliffConverter;
import pl.edu.agh.iosr.exceptions.WorkflowException;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.model.TranslationOrder.RequestState;
import pl.edu.agh.iosr.services.RepositoryProxyService;
import pl.edu.agh.iosr.services.TranslationOrderService;
import pl.edu.agh.iosr.services.TranslationServicesConfigService;
import pl.edu.agh.iosr.services.ValidationService;
import pl.edu.agh.iosr.ws.RemoteWSInvoker;

/**
 * Mediator, koordynuje dzia�ania innych komponent�w, nale�y unika� dodawania tu
 * logiki za wyj�tkiem sterowania
 * 
 * Poniewaz se springiem jest kupa problemow prawdopodobnie wszystko co mozliwe
 * napiszemy w Seamie (to potem tez), narazie to jest Spring, patrz
 * ApplicationContext.xml
 * 
 * <br>
 * 1.12.2009 Trzeba będzie przemyśleć konieczność implementowania wszystkich
 * interfejsów, narazie daję wolną rękę
 * 
 * @author czopson
 * */

@Name("mediator")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class Mediator {

	@In(create = true)
	private XliffConverter xliffConverter;

	@In(create = true)
	private ValidationService validationService;

	@In(create = true)
	private RepositoryProxyService documentAccessService;

	@In(create = true)
	private TranslationOrderService translationOrderService;

	@In(create = true)
	private TranslationServicesConfigService translationServicesConfigService;

	@In(create = true)
	private RemoteWSInvoker remoteWSInvoker;

	/**
	 * Kolejkuje zamówienie
	 * */
	public void enqueuRequest(TranslationOrder request) {
		log(this.getClass(), "Złożono zamówienie na przekład:\n"
				+ request.toString());
	}

	/**
	 * Pomaga sprawdzać poprawność przepływu zamówień
	 * 
	 * @throws WorkflowException
	 *             , IllegalArgumentException
	 * */
	private void validateOrder(TranslationOrder order,
			RequestState expectedState) throws WorkflowException,
			IllegalArgumentException {

		// nie chcemy nulla
		if (order == null) {
			throw new IllegalArgumentException("Order cannot be null.");
		}

		// stan workflowa musi sie zgadzac
		if (!order.getState().equals(expectedState)) {
			throw new WorkflowException("Order with id: "
					+ order.getRequestId()
					+ " has improper workflow state. Has: " + order.getState()
					+ ", should have: " + expectedState);
		}
	}

	/**
	 * zglasza ��danie translacji i przprowadza operacje konieczne by wyslac je
	 * do tlumaczenia
	 * */
	public void submitTranslationOrder(TranslationOrder order) {
		try {

			validateOrder(order, RequestState.BEFORE_PROCESSING);
			
			// ustawiamy status w tym miejscu na konwersje
			// konwerter nie przeprowadza zadnej kontroli
			order.nextState();
			translationOrderService.saveOrUpdateTranslationOrder(order);

			TranslationServiceDescription tsDescription = translationServicesConfigService
					.getTranslationService(order.getWsId());
			validationService.validate(order, tsDescription);
			
			// TODO przesunąć poniższą logikę do konwertera
			String fileExtension = documentAccessService.getFileExtension(order
					.getSourceDocument());
			if (validationService.isConversionNeeded(fileExtension,
					tsDescription)) {

				// tylko to powinno sie tutaj znalezc
				xliffConverter.convert(order);
			}
			else {
				File fileToTranslate = documentAccessService.getFile(order
						.getSourceDocument());
				remoteWSInvoker.traslateAsync(tsDescription, order,
						fileToTranslate);
			}

		}
		catch (Exception e) {
			// w razie niepowodzenia umieszczamy stosowną informacje w obiekcie
			// order
			// logujemy błąd i zapisujemy order
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

	}

	/**
	 * zleca wyslanie do tłumaczenia remoteWSinvokerowi
	 * */
	public void submitExactTranslation(TranslationOrder order) {

		try {

			validateOrder(order, RequestState.UNDER_CONVERSION);

			// ustawiamy status w tym miejscu na wyslany do tlumaczenia
			// remoteWSinvoker nie przeprowadza zadnej kontroli
			order.nextState();
			translationOrderService.saveOrUpdateTranslationOrder(order);

			remoteWSInvoker.traslateAsync(translationServicesConfigService
					.getTranslationService(order.getWsId()), order,
					documentAccessService.getFile(order.getSourceDocument()));

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * zglasza rezultaty translacji i przeprowadza operacje konieczne by
	 * przetlumaczenie zostalo zapisane
	 * */
	public void submitTranslationResult(Long id, File resultFile) {

		try {

			TranslationOrder order = translationOrderService
					.getTranslationOrder(id);

			validateOrder(order, RequestState.UNDER_PROCESSING);


			if (validationService.isReconversionNeeded(order)) {

				order.setXliff(resultFile);

				// TODO przesunac logike do konwertera
				xliffConverter.reConvert(order); // TODO pobranie pliku i
				// zapisanie go w Nuxeo

			}
			else {

				documentAccessService.saveFile(order, resultFile);

			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
