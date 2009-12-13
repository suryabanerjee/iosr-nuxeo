package pl.edu.agh.iosr.controller;

import pl.edu.agh.iosr.conversion.XliffConverter;
import pl.edu.agh.iosr.model.TranslationOrder;

import static pl.edu.agh.iosr.util.IosrLogger.log;

/**
 * Mediator, koordynuje dzia�ania innych komponent�w,
 * nale�y unika� dodawania tu logiki za wyj�tkiem sterowania
 * 
 * Poniewaz se springiem jest kupa problemow prawdopodobnie
 * wszystko co mozliwe napiszemy w Seamie (to potem tez),
 * narazie to jest Spring, patrz ApplicationContext.xml
 * 
 * <br>
 * 1.12.2009
 * Trzeba będzie przemyśleć konieczność implementowania
 * wszystkich interfejsów, narazie daję wolną rękę
 * 
 * @author czopson
 * */
public class Mediator {
	
	private XliffConverter xliffConverter;
	
	/**
	 * Kolejkuje zamówienie
	 * */
	public void enqueuRequest(TranslationOrder request) {
		log(this.getClass(), "Złożono zamówienie na przekład:\n" + request.toString());
	}
	
}
