package pl.edu.agh.iosr.view;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.controller.ConfigurationStorage;
import pl.edu.agh.iosr.controller.EnrichedFile;
import pl.edu.agh.iosr.controller.FilesSelectionBean;
import pl.edu.agh.iosr.controller.Mediator;
import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;

/**
 * Backing bean dla widoku wyboru tłumaczenia
 * 
 * 'wyświetla' tabelke z plikami oraz opcje tłumaczenie
 * 
 * tworzy rządzanie tłumaczenia TranslationRequest i wysyła je do mediatora.
 * 
 * */
@Scope(ScopeType.CONVERSATION)
@Name("editionBean")
public class EditionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5218857682596144169L;

	@In("#{mediator}")
	private Mediator mediator;

	@In(create = true)
	private FilesSelectionBean filesSelectionBean;

	@In(create = true)
	private ConfigurationStorage configurationStorage;

	private TranslationServiceDescription translationServiceDescription;
	private String langTo, langFrom, quality;
	private boolean languageDetection = false;

	@Create
	public void init() {
		try {
			if (configurationStorage != null
					&& configurationStorage.getRemoteWSs() != null) {
				translationServiceDescription = configurationStorage
						.getRemoteWSs().get(0);

				Collection<LangPair> list = translationServiceDescription
						.getSupportedLangPairs();

				if (list != null && list.size() > 0) {
					langFrom = list.iterator().next().getFromLang();
					langTo = list.iterator().next().getToLang();
				}
			}
		}
		catch (Exception e) {
			log(this.getClass(), e.getMessage());
		}
	}

	/**
	 * Pobiera liste zarejestrowanych serwisow tlumaczacych
	 * */
	public SelectItem[] getAvailableServices() {
		List<TranslationServiceDescription> wss = configurationStorage
				.getRemoteWSs();
		SelectItem[] items = new SelectItem[wss.size()];
		int i = 0;
		for (TranslationServiceDescription rd : wss) {
			items[i++] = new SelectItem(rd.getName(), rd.getName());
		}
		return items;
	}

	/**
	 * Po wybraniu serwisu, pobiera dla niego liste jezykow z ktorych mozna
	 * tlumaczyc
	 * */
	public SelectItem[] getLangsFrom() {
		if (translationServiceDescription == null
				|| translationServiceDescription.getSupportedLangPairs() == null) {
			return new SelectItem[0];
		}

		Collection<LangPair> langPairs = translationServiceDescription
				.getSupportedLangPairs();

		Set<String> froms = new HashSet<String>();

		for (LangPair lp : langPairs) {
			froms.add(lp.getFromLang());
		}

		SelectItem[] items = new SelectItem[froms.size()];
		int i = 0;
		for (String s : froms) {
			items[i++] = new SelectItem(s, s);
		}
		return items;
	}

	/**
	 * Po wybraniu jezyka z ktorego ma byc tlumaczone, zwraca liste jezyka na
	 * ktore moze nastapic przeklad
	 * */
	public SelectItem[] getLangsTo() {
		if (translationServiceDescription == null
				|| langFrom == null
				|| translationServiceDescription.getSupportedLangPairs() == null) {
			return new SelectItem[0];
		}

		Collection<LangPair> langPairs = translationServiceDescription.getSupportedLangPairs();

		Set<String> froms = new HashSet<String>();

		for (LangPair lp : langPairs) {
			if (lp.getFromLang().equals(langFrom)) {
				froms.add(lp.getToLang());
			}
		}

		SelectItem[] items = new SelectItem[froms.size()];
		int i = 0;
		for (String s : froms) {
			items[i++] = new SelectItem(s, s);
		}
		return items;
	}

	/**
	 * Po wybraniu serwisu, pobiera dla niego liste wspieranych jakosci
	 * przekladu
	 * */
	public SelectItem[] getQualities() {
		if (translationServiceDescription == null
				|| translationServiceDescription.getSupportedQualities() == null) {
			return new SelectItem[0];
		}

		SelectItem[] items = new SelectItem[translationServiceDescription
				.getSupportedQualities().length];
		Iterator<String> iterator = Arrays.asList(
				translationServiceDescription.getSupportedQualities())
				.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			String s = iterator.next();
			items[i++] = new SelectItem(s, s);
		}
		return items;
	}

	public void setWsName(String wsName) {
		for (TranslationServiceDescription rd : configurationStorage
				.getRemoteWSs()) {
			log(this.getClass(), "trying to compare: " + rd.getName() + " vs "
					+ wsName);
			if (rd.getName().equals(wsName)) {
				translationServiceDescription = rd;
				log(this.getClass(), "translationSeviceDescription set to: "
						+ rd);
				return;
			}
		}
		log(this.getClass(),
				"failed to find suitable translationServiceDescription!");
	}

	public String getWsName() {
		return "";
	}

	public String getLangTo() {
		return langTo;
	}

	public String getLangFrom() {
		return langFrom;
	}

	public boolean isLanguageDetection() {
		return languageDetection;
	}

	public void setLanguageDetection(boolean languageDetection) {
		this.languageDetection = languageDetection;
	}

	public void setLangFrom(String langFrom) {
		this.langFrom = langFrom;
	}

	public void setLangTo(String langTo) {
		this.langTo = langTo;
	}

	public FilesSelectionBean getFilesSelectionBean() {
		return filesSelectionBean;
	}

	public void setFilesSelectionBean(FilesSelectionBean filesSelectionBean) {
		this.filesSelectionBean = filesSelectionBean;
	}

	/**
	 * zawiera validacje przed wykonaniem wlasciwiej akcji
	 * */
	public void validationListener(ActionEvent ae) {
		filesSelectionBean.validate();
	}

	/**
	 * buduje i wysyła zlecenie do kontrolera
	 * 
	 * nie zawiera validacji - validacja w listenerze
	 * 
	 * odpalane po nacisnieciu guziora
	 * */
	public String buildTranslationRequest() {

		TranslationOrder translationOrder;
		log(this.getClass(), "buildTranslationRequest called!");
		for (EnrichedFile ef : filesSelectionBean.getFiles()) {
			log(this.getClass(), "file found!");
			if (ef.getSelected()) {
				log(this.getClass(), "file added!");

				LangPair lp = new LangPair();
				lp.setFromLang(langFrom);
				lp.setToLang(langTo);

				translationOrder = new TranslationOrder(ef.getDocumentModel()
						.getRef(), lp, ef.getTargetName(), "",
						translationServiceDescription.getWsId(),
						languageDetection);
				mediator.beginTranslation(translationOrder);
			}
		}
		log(this.getClass(), "end of buildTranslationRequest!");

		return "#";
	}

	public ConfigurationStorage getConfigurationStorage() {
		return configurationStorage;
	}

	public void setConfigurationStorage(
			ConfigurationStorage configurationStorage) {
		this.configurationStorage = configurationStorage;
	}

	public TranslationServiceDescription getTranslationServiceDescription() {
		return translationServiceDescription;
	}

	// raporty ze zleconych tłumaczeń
	private String report;
	private boolean hasReport = false;

	public String getReport() {
		return report;
	}

	public boolean getHasReport() {
		return hasReport;
	}

	public Mediator getMediator() {
		return mediator;
	}

	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

}
