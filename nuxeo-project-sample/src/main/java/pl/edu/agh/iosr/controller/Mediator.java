package pl.edu.agh.iosr.controller;

import pl.edu.agh.iosr.model.TranslationRequest;

import static pl.edu.agh.iosr.util.IosrLogger.log;

/**
 * Mediator, koordynuje dzia�ania innych komponent�w,
 * nale�y unika� dodawania tu logiki za wyj�tkiem sterowania
 * 
 * Poniewaz se springiem jest kupa problemow prawdopodobnie
 * wszystko co mozliwe napiszemy w Seamie (to potem tez),
 * narazie to jest Spring, patrz ApplicationContext.xml
 * */
public class Mediator {
	
	private ConfigurationStorage configurationStorage;

	public ConfigurationStorage getConfigurationStorage() {
		return configurationStorage;
	}

	public void setConfigurationStorage(ConfigurationStorage configurationStorage) {
		this.configurationStorage = configurationStorage;
	}
	
	
	/**
	 * Kolejkuje zamówienie
	 * jeszcze nie wiadomo gdzie ani co z nimi robi, ale jest tu!
	 * */
	public void enqueuRequest(TranslationRequest request) {
		log(this.getClass(), "Złożono zamówienie na przekład:\n" + request.toString());
	}
	
}
