package pl.edu.agh.iosr.services;

import java.util.Collection;

import pl.edu.agh.iosr.model.TranslationService;

/**
 * Podobna historia jak z TranslationOrderService, koniecznie patrz komentarz
 * do niego!!!
 * */
public interface TranslationServicesConfigService {

	/**
	 * Lista wszystkich
	 * */
	public Collection<TranslationService> getTranslationServices();
	
	/**
	 * aktualizacja informacji o serwisie
	 * */
	public void saveOrUpdateTranslationService(TranslationService translationService);
	
	/**
	 * wiadomo
	 * */
	public void delete(Long id);
}
