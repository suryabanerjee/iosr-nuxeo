package pl.edu.agh.iosr.view;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.controller.ConfigurationStorage;
import pl.edu.agh.iosr.controller.RemoteWSDescription;

/**
 * backing bean dla widoku osoby administruj¹cej
 * WSami. wyœwietla tabelke z zarejestrowanymi WSami oraz
 * pozwala dodawac i usuwac owe.
 * */
@Scope(ScopeType.CONVERSATION)
@Name("configurationBean")
public class ConfigurationBean {

	@In("#{configurationStorage}")
	private ConfigurationStorage configurationStorage;

	private String description, endpoint, name;
	
	public List<RemoteWSDescription> getRemoteWSs() {
		return configurationStorage.getRemoteWSs();
	}
	
	/**
	 * rejestruje nowy WS, validacja w xhtlmu
	 * */
	public void addNewWS() {
		
		// troche walidacji nie zaszkodzi
		for (RemoteWSDescription r : configurationStorage.getRemoteWSs()) {
			if (endpoint.equals(r.getEndpoint()) &&
				name.equals(r.getName())) {
				FacesContext.getCurrentInstance().
					addMessage(null, new FacesMessage("Cannot duplicate WS."));
				//TODO odnosnik do bundla
			}
		}
		
		RemoteWSDescription r = new RemoteWSDescription();
		r.setDescription(description);
		r.setEndpoint(endpoint);
		r.setName(name);
		configurationStorage.getRemoteWSs().add(r);
	}
	
	/**
	 * usuwa wybrany WS. Przed tym wydarzeniem
	 * setPropertyActionListenery z xhtmla
	 * ustawiaja wartosc name i endpoint
	 * */
	public void deleteWS(ActionEvent ae) {
		for (RemoteWSDescription r : configurationStorage.getRemoteWSs()) {
			if (r.getName().equals(name) && r.getEndpoint().equals(endpoint)) {
				configurationStorage.getRemoteWSs().remove(r);
				break;
			}
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
