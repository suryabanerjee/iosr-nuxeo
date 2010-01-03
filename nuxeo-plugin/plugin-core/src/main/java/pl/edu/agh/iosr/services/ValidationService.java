package pl.edu.agh.iosr.services;

import pl.edu.agh.iosr.exceptions.WorkflowException;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.model.TranslationOrder.RequestState;

public interface ValidationService {

	boolean isConversionNeeded(String fileExtension,
			TranslationServiceDescription tsDescription);

	void validateOrder(TranslationOrder order, RequestState expectedState) throws WorkflowException, IllegalArgumentException;

	boolean isReconversionNeeded(TranslationOrder order);

}
