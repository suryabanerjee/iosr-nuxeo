package pl.edu.agh.iosr.services;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.exceptions.WorkflowException;
import pl.edu.agh.iosr.model.DocumentType;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.model.TranslationOrder.RequestState;

@Name("validationService")
@Scope(ScopeType.APPLICATION)
public class ValidationServiceImpl implements ValidationService{

	public boolean isConversionNeeded(String fileExtension, TranslationServiceDescription tsDescription) {
		boolean result = true;
		
		/*for(DocumentType type: tsDescription.getSupportedDocumentTypes()){
			if(fileExtension.toLowerCase().equals(type.getValue().toLowerCase()))
				result = true;
		}*/
		
		return result;
	}

	public boolean isReconversionNeeded(TranslationOrder order) {
		return order.getXliff() != null;
	}

	/**
	 * Pomaga sprawdzać poprawność przepływu zamówień
	 * 
	 * @throws WorkflowException
	 *             , IllegalArgumentException
	 * */
	public void validateOrder(TranslationOrder order, RequestState expectedState) throws WorkflowException, IllegalArgumentException {

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
	

}
