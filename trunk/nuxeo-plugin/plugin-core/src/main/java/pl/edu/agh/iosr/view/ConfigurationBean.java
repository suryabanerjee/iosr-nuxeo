package pl.edu.agh.iosr.view;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.controller.ConfigurationStorage;
import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.TranslationServiceDescription;

/**
 * backing bean dla widoku osoby administruj�cej WSami. wy�wietla tabelke z
 * zarejestrowanymi WSami oraz pozwala dodawac i usuwac owe.
 * */
@Scope(ScopeType.CONVERSATION)
@Name("configurationBean")
public class ConfigurationBean {

	@In("#{configurationStorage}")
	private ConfigurationStorage configurationStorage;

	private String description = "", endpoint = "", name = "";

	public List<TranslationServiceDescription> getRemoteWSs() {
		return configurationStorage.getRemoteWSs();
	}

	/**
	 * rejestruje nowy WS, validacja w xhtlmu
	 * */
	public void addNewWS(ActionEvent ae) {

		// troche walidacji nie zaszkodzi
		for (TranslationServiceDescription r : configurationStorage
				.getRemoteWSs()) {
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
		configurationStorage.getRemoteWSs().add(r);
	}

	/**
	 * usuwa wybrany WS. Przed tym wydarzeniem setPropertyActionListenery z
	 * xhtmla ustawiaja wartosc name i endpoint
	 * */
	public void deleteWS(ActionEvent ae) {
		
		String name = (String)ae.getComponent().getAttributes().get("czopson"); 
		
		TranslationServiceDescription rwds = null;
		for (TranslationServiceDescription r : configurationStorage
				.getRemoteWSs()) {
			if (r.getName().equals(name)) {
				rwds = r;
				break;
			}
		}
		if (rwds != null) {
			log(this.getClass(), "deleted ws: "
					+ configurationStorage.getRemoteWSs().remove(rwds)
					+ " - chosen: " + name);
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

	public ConfigurationStorage getConfigurationStorage() {
		return configurationStorage;
	}

	public void setConfigurationStorage(
			ConfigurationStorage configurationStorage) {
		this.configurationStorage = configurationStorage;
	}

	public TranslationServiceDescription getSelectedWS() {
		for (TranslationServiceDescription r : configurationStorage
				.getRemoteWSs()) {
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
	 * action do usuwanie jednej pary z obslugiwanych jezyków
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

	public SelectItem[] getIso3Codes() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (Locale l : Locale.getAvailableLocales()) {
			if (l.getISO3Language() != null && l.getISO3Language() != "") {
				list.add(new SelectItem(l.getISO3Language().toUpperCase()));
			}
		}
		SelectItem[] items = new SelectItem[list.size()];
		int i = 0;
		for (SelectItem si : list) {
			items[i++] = si;
		}
		return items;
	}

	/**
	 * action, dodaje parke obslugiwanych jezykow
	 * */
	public String addPair() {
		if (newLangFrom.equals(newLangTo)) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Languages must differ."));
			return "";
		}

		if (getSelectedWS() == null) {
			return "";
		}

		Collection<LangPair> ll = getSelectedWS().getSupportedLangPairs();
		for (LangPair lp : ll) {
			if (lp.getFromLang().equals(newLangFrom)
					&& lp.getToLang().equals(newLangTo)) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Selected languages set already supported."));
				return "";
			}
		}

		LangPair lp = new LangPair();
		lp.setFromLang(newLangFrom);
		lp.setToLang(newLangTo);

		getSelectedWS().getSupportedLangPairs().add(lp);
		
		return "";
	}
}
