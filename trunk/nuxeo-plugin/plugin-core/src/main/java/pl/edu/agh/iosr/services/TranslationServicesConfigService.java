package pl.edu.agh.iosr.services;

import java.util.List;

import pl.edu.agh.iosr.model.TranslationServiceDescription;

/**
 * DAO dla obiektów {@link TranslationServiceDescription}.
 * */
public interface TranslationServicesConfigService {

	/**
	 * @return Lista wszytkich zarejestrowanych serwisów.
	 * */
	public List<TranslationServiceDescription> getTranslationServices();

	/**
	 * @param identyfikator
	 *            serwisu
	 * @return obiekt reprezentujący serwis o zadanym identyfikatorze.
	 * */
	public TranslationServiceDescription getTranslationService(Long wsId);

	/**
	 * @param translationService
	 *            - opis serwisu do utrwalenia bądź zaktualizowania.
	 * @return zaktualizowany opis serwisu
	 * */
	public TranslationServiceDescription saveOrUpdateTranslationService(
			TranslationServiceDescription translationService);

	/**
	 * Usuwa opis serwisu o zadanym identyfikatorze
	 * 
	 * @param id
	 *            identyfikator serwisu do usunięcia.
	 * */
	public void delete(Long id);

	/**
	 * Synchronicznie odświeża informacje o wybranym web servicie.
	 * 
	 * @Throws {@link InterruptedException} kiedy stracimy cierpliwość.
	 * */
	public void refreshWs(Long id) throws InterruptedException;

}
