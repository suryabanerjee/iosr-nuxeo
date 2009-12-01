package pl.edu.agh.iosr.model;

import java.util.Collection;

import pl.edu.agh.iosr.util.IosrRandomGenerator;

/**
 * Opisuje konfigurację pojedynczego serwisu tłumaczącego
 * */
public class TranslationService {

	/**
	 * id web serwisu
	 * */
	private final Long wsId = IosrRandomGenerator.nextLong();
	
	/**
	 * nietrudno się domyśleć
	 * */
	private String endpoint;
	
	private Collection<LangPair> supportedLangPairs;
	
	private Collection<String> supportedQualities;
	
	private String name, description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getWsId() {
		return wsId;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Collection<LangPair> getSupportedLangPairs() {
		return supportedLangPairs;
	}

	public void setSupportedLangPairs(Collection<LangPair> supportedLangPairs) {
		this.supportedLangPairs = supportedLangPairs;
	}

	public Collection<String> getSupportedQualities() {
		return supportedQualities;
	}

	public void setSupportedQualities(Collection<String> supportedQualities) {
		this.supportedQualities = supportedQualities;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}