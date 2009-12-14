package pl.edu.agh.iosr.services;

import java.util.Collection;

import pl.edu.agh.iosr.model.TranslationServiceDescription;

/**
 * Podobna historia jak z TranslationOrderService, koniecznie patrz komentarz
 * do niego!!!
 * */
public interface TranslationServicesConfigService {

	/**
	 * Lista wszystkich
	 * */
	public Collection<TranslationServiceDescription> getTranslationServices();
	
	
	public TranslationServiceDescription getTranslationService(Long wsId);
	
	
	/**
	 * aktualizacja informacji o serwisie
	 * */
	public void saveOrUpdateTranslationService(TranslationServiceDescription translationService);
	
	/**
	 * wiadomo
	 * */
	public void delete(Long id);
	
	
	
	
}
