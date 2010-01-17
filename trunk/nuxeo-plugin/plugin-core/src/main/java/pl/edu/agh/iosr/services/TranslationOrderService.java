package pl.edu.agh.iosr.services;

import java.util.List;

import pl.edu.agh.iosr.exceptions.DataInconsistencyException;
import pl.edu.agh.iosr.model.TranslationOrder;

/**
 * DAO obsługujące obiekty klasy {@link TranslationOrder}.
 * */
public interface TranslationOrderService {

	/**
	 * Zwraca zamówienia dotyczące źródłowego pliku o nazwie z tablicy
	 * <code>names</code>.
	 * 
	 * @param names
	 *            - tablica nazw plików
	 * 
	 * @return lista zamówień
	 * */
	public List<TranslationOrder> getTranslationOrders(String[] names);

	/**
	 * @param id
	 *            identyfikator zamówienia
	 * @return Zamówienie o wybranym identyfikatorze
	 * */
	public TranslationOrder getTranslationOrder(Long id);

	/**
	 * @param translationOrder
	 *            - zamówienie do utrwalenia bądź zaktualizowania.
	 * @return zaktualizowane zamównienie
	 * */
	public TranslationOrder saveOrUpdateTranslationOrder(
			TranslationOrder translationOrder)
			throws DataInconsistencyException;

	/**
	 * Usuwany zamówienie o wyspecyfikowanym identyfikatorze.
	 * 
	 * @param id
	 *            identyfikator zamówienia.
	 * */
	public void delete(Long id);
}
