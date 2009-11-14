package pl.edu.agh.iosr.ws;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.iosr.model.LangPair;


/**
 * Zawiera informacje niezb�dne do wywo�ania zdalnego WSa
 * jak kto� b�dzie wiedzia� co dok�adnie, to jazda
 * */
public class RemoteWSDescription {

	private String name, endpoint, description;
	private List<LangPair> supportedTranslation = new ArrayList<LangPair>(); 
	
	public RemoteWSDescription() {
		super();
	}
	
	public RemoteWSDescription(String name, String endpoint) {
		super();
		this.name = name;
		this.endpoint = endpoint;
	}

	public RemoteWSDescription(String name, String endpoint, String description) {
		super();
		this.name = name;
		this.endpoint = endpoint;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<LangPair> getSupportedTranslation() {
		return supportedTranslation;
	}

}
