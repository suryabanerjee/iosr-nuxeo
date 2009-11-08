package pl.edu.agh.iosr.controller;

/**
 * Mediator, koordynuje dzia³ania innych komponentów,
 * nale¿y unikaæ dodawania tu logiki za wyj¹tkiem sterowania
 * */
public class TranslationMediator {
	
	private ConfigurationStorage configurationStorage;

	public ConfigurationStorage getConfigurationStorage() {
		return configurationStorage;
	}

	public void setConfigurationStorage(ConfigurationStorage configurationStorage) {
		this.configurationStorage = configurationStorage;
	}
	
}
