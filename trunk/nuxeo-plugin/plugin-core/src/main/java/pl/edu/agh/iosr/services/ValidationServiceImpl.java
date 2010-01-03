package pl.edu.agh.iosr.services;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;

@Name("validationService")
@Scope(ScopeType.APPLICATION)
public class ValidationServiceImpl implements ValidationService{

	public boolean isConversionNeeded(String fileExtension,
			TranslationServiceDescription tsDescription) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isReconversionNeeded(TranslationOrder order) {
		// TODO Auto-generated method stub
		return true;
	}

	public void validate(TranslationOrder order,
			TranslationServiceDescription tsDescription) {
		// TODO Auto-generated method stub
		
	}
	

}
