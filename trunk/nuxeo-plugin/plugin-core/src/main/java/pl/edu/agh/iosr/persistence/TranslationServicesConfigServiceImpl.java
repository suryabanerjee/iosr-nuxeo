package pl.edu.agh.iosr.persistence;

import java.util.Collection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.services.TranslationServicesConfigService;

@Name("translationServicesConfigService")
@Scope(ScopeType.APPLICATION)
public class TranslationServicesConfigServiceImpl implements TranslationServicesConfigService{

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TranslationServiceDescription getTranslationService(Long wsId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<TranslationServiceDescription> getTranslationServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateTranslationService(
			TranslationServiceDescription translationService) {
		// TODO Auto-generated method stub
		
	}

}
