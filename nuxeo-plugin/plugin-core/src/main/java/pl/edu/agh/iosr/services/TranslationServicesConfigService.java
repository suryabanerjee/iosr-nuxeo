package pl.edu.agh.iosr.services;

import java.util.List;

import pl.edu.agh.iosr.model.TranslationServiceDescription;

/**
 * Podobna historia jak z TranslationOrderService, koniecznie patrz komentarz
 * do niego!!!
 * */
public interface TranslationServicesConfigService {

	/**
	 * Lista wszystkich
	 * */
	public List<TranslationServiceDescription> getTranslationServices();
	
	
	public TranslationServiceDescription getTranslationService(Long wsId);
	
	
	/**
	 * aktualizacja informacji o serwisie
	 * */
	public TranslationServiceDescription saveOrUpdateTranslationService(TranslationServiceDescription translationService);
	
	/**
	 * wiadomo
	 * */
	public void delete(Long id);
	
	
	/**
	 * Synchronicznie odświeża informacje o wybranym web servicie.
	 * 
	 * @Throws {@link InterruptedException} kiedy stracimy cierpliwość.
	 * */
	public void refreshWs(Long id) throws InterruptedException; 
	
}
