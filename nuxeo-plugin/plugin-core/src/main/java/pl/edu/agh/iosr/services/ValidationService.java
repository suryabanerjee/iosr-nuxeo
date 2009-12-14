package pl.edu.agh.iosr.services;

import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;

public interface ValidationService {

	boolean isConversionNeeded(String fileExtension,
			TranslationServiceDescription tsDescription);

	void validate(TranslationOrder order,
			TranslationServiceDescription tsDescription);

	boolean isReconversionNeeded(TranslationOrder order);

}
