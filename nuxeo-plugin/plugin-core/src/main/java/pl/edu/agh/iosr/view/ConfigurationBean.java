package pl.edu.agh.iosr.view;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.services.TranslationServicesConfigService;
import pl.edu.agh.iosr.util.IosrLogger;
import pl.edu.agh.iosr.util.MessagesLocalizer;

/**
 * Backing bean dla widoku osoby administruj�cej WSami. wy�wietla tabelke z
 * zarejestrowanymi WSami oraz pozwala dodawac i usuwac owe oraz odświeżać ich
 * konfiguracje.
 * */
@Scope(ScopeType.CONVERSATION)
@Name("configurationBean")
public class ConfigurationBean {

	@In("#{translationServicesConfigService}")
	private TranslationServicesConfigService configService;

	private String description = "", endpoint = "", name = "";

	private List<TranslationServiceDescription> translationServices;

	@Create
	public void init() {
		translationServices = configService.getTranslationServices();
	}

	public List<TranslationServiceDescription> getTranslationServices() {
		return translationServices;
	}

	/**
	 * Rejestruje nowy WS, walidacja w xhtlmu
	 * 
	 * Wołane jako ActionListener.
	 * */
	public void addNewWS(ActionEvent ae) {

		IosrLogger.log(this.getClass(), "addNewWs invoked");

		// troche walidacji nie zaszkodzi
		for (TranslationServiceDescription r : translationServices) {
			if (name.equals(r.getName())) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Cannot duplicate WS."));
				return;
			}
		}

		TranslationServiceDescription r = new TranslationServiceDescription();
		r.setDescription(description);
		r.setEndpoint(endpoint);
		r.setName(name);
		configService.saveOrUpdateTranslationService(r);
		IosrLogger.log(this.getClass(),
				"Translation Service Desciption persisted: " + r);
		init();
	}

	/**
	 * Usuwa wybrany WS. Przed tym wydarzeniem setPropertyActionListenery z
	 * xhtmla ustawiaja wartosc name i endpoint.
	 * 
	 * Wołane jako ActionListener.
	 * */
	public void deleteWS(ActionEvent ae) {

		String name = (String) ae.getComponent().getAttributes().get("czopson");

		TranslationServiceDescription rwds = null;
		for (TranslationServiceDescription r : translationServices) {
			if (r.getName().equals(name)) {
				rwds = r;
				break;
			}
		}
		if (rwds != null) {
			configService.delete(rwds.getWsId());
			init();
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

	public TranslationServiceDescription getSelectedWS() {
		for (TranslationServiceDescription r : translationServices) {
			if (r.getName().equals(name)) {
				return r;
			}
		}
		return null;
	}

	// usuwanie pojedynczych par języków

	private String toDeletePairLangFrom, toDeletePairLangTo;

	public void setToDeletePairLangFrom(String toDeletePairLangFrom) {
		this.toDeletePairLangFrom = toDeletePairLangFrom;
		System.out.println("toDeletePairLAng From set to "
				+ toDeletePairLangFrom);
	}

	public void setToDeletePairLangTo(String toDeletePairLangTo) {
		this.toDeletePairLangTo = toDeletePairLangTo;
		System.out.println("toDeletePairLAngTo set to " + toDeletePairLangTo);
	}

	/**
	 * Action do usuwania jednej pary z obslugiwanych jezyków
	 * */
	public String deletePair() {
		int i = -1;
		LangPair lp = null;
		for (LangPair l : getSelectedWS().getSupportedLangPairs()) {
			++i;
			if (l.getFromLang().equals(toDeletePairLangFrom)
					&& l.getToLang().equals(toDeletePairLangTo)) {
				lp = l;
				break;
			}
		}
		System.out.println("removing: " + toDeletePairLangFrom + "-"
				+ toDeletePairLangTo + " which is " + i + "th");
		if (lp != null) {
			getSelectedWS().getSupportedLangPairs().remove(lp);
		}
		return "";
	}

	// dodawanie pojedynczych par języków

	private String newLangFrom, newLangTo;

	public String getNewLangFrom() {
		return newLangFrom;
	}

	public void setNewLangFrom(String newLangFrom) {
		this.newLangFrom = newLangFrom;
	}

	public String getNewLangTo() {
		return newLangTo;
	}

	public void setNewLangTo(String newLangTo) {
		this.newLangTo = newLangTo;
	}

	/* odświeżanie wsa START */

	/**
	 * 
	 * Odświeża wybrany opis serwisu.
	 * Wybór poprzez aatrybut "ws".
	 * Wołane jako ActionListener.
	 * */
	public void refreshWs(ActionEvent ae) {
		TranslationServiceDescription t = (TranslationServiceDescription) ae
				.getComponent().getAttributes().get("ws");
		try {
			configService.refreshWs(t.getWsId());
		}
		catch (InterruptedException e) {
			String message = MessagesLocalizer.getMessage("failed.refresh");
			if (message == null) {
				message = "Failed to refresh translation service configuration.";
			}
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(message));
		}
		init();
	}

	/* odświeżanie wsa END */

}
