package pl.edu.agh.iosr.services;

import java.util.List;

import pl.edu.agh.iosr.exceptions.DataInconsistencyException;
import pl.edu.agh.iosr.model.TranslationOrder;

/**
 * Jeśli ta klasa będzie miała charakter dao to nie będzie zwracała referencji
 * do obiektów a ich klony - inaczej mówiąc encje TranslationOrder będą odłączne
 * z kontekstu persystencji i wszelkie przeprowadzane w nich zmiany nie będą
 * utrwalanie aż do momentu wywołania update, czyli mw merga.
 * 
 * Naturalnie "trudno" cos takiego zaimplementować na mechanizmie utrwalania w
 * pamięć (chyba najszybciej poprzez Cloneable i clone), ale gdy będzie to
 * działać na poważnym mechazmie persystencji trzeba o tym pamiętać.
 * */
public interface TranslationOrderService {

	/**
	 * Zapewne ma zwrócić wszystkie TranslationOrdery o zadanym stanie
	 * */
	public List<TranslationOrder> getTranslationOrders(
			TranslationOrder.RequestState state);

	/**
	 * Zwraca wszystkie TranslationOrdery które dotyczą plików o
	 * nazwach ze zbioru <code>names</code>.
	 * */
	public List<TranslationOrder> getTranslationOrders(String[] names);

	/**
	 * Nietrudno zgadnąć
	 * */
	public TranslationOrder getTranslationOrder(Long id);

	/**
	 * wiadomo - patrz komentarz do interfejsu!!!
	 * */
	public TranslationOrder saveOrUpdateTranslationOrder(
			TranslationOrder translationOrder)
			throws DataInconsistencyException;

	/**
	 * prawdopodobnie nie będzie wykorzystywane
	 * */
	public void delete(Long id);
}
