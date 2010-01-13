package pl.edu.agh.iosr.view;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.controller.EnrichedFile;
import pl.edu.agh.iosr.controller.FilesSelectionBean;
import pl.edu.agh.iosr.controller.Mediator;
import pl.edu.agh.iosr.model.DocumentRefWrapper;
import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.Quality;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.services.TranslationOrderService;
import pl.edu.agh.iosr.services.TranslationServicesConfigService;
import pl.edu.agh.iosr.util.MessagesLocalizer;

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

	@In("#{translationServicesConfigService}")
	private TranslationServicesConfigService configService;

	@In(value = "#{translationOrderService}")
	private TranslationOrderService translationOrderService;

	private List<TranslationOrder> orders;

	private List<TranslationServiceDescription> translationServices;

	private TranslationServiceDescription translationServiceDescription;
	private String langTo, langFrom, quality;
	private boolean languageDetection = false;

	@Create
	public void init() {
		try {
			translationServices = configService.getTranslationServices();

			if (translationServices.size() == 0) {
				return;
			}

			List<String> names = new LinkedList<String>();
			for (EnrichedFile ef : filesSelectionBean.getFiles()) {
				names.add(ef.getName());
			}
			orders = translationOrderService.getTranslationOrders(names
					.toArray(new String[0]));

			translationServiceDescription = translationServices.get(0);

			Collection<LangPair> list = translationServiceDescription
					.getSupportedLangPairs();

			if (list != null && list.size() > 0) {
				langFrom = list.iterator().next().getFromLang();
				langTo = list.iterator().next().getToLang();
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
		SelectItem[] items = new SelectItem[translationServices.size()];
		int i = 0;
		for (TranslationServiceDescription rd : translationServices) {
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

		Collection<LangPair> langPairs = translationServiceDescription
				.getSupportedLangPairs();

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
				.getSupportedQualities().size()];
		Iterator<Quality> iterator = translationServiceDescription
				.getSupportedQualities().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			String s = iterator.next().getValue();
			items[i++] = new SelectItem(s, s);
		}
		return items;
	}

	public void setWsName(String wsName) {
		for (TranslationServiceDescription rd : translationServices) {
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
			if (ef.getSelected()) {
				log(this.getClass(), "file: " + ef.getName() + " added!");

				LangPair lp = new LangPair();
				lp.setFromLang(langFrom);
				lp.setToLang(langTo);

				String path = ef.getDocumentModel().getPathAsString();

				DocumentRefWrapper drw = new DocumentRefWrapper();
				drw.setName(ef.getDocumentModel().getName());
				drw.setPath(path.substring(0, path.lastIndexOf("/")));
				drw.setType(ef.getDocumentModel().getType());

				translationOrder = new TranslationOrder(drw, lp, ef
						.getTargetName(), "", translationServiceDescription
						.getWsId(), languageDetection);
				mediator.beginTranslation(translationOrder);
			}
		}
		log(this.getClass(), "end of buildTranslationRequest!");

		return "#";
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
	
	/* zabawa z wyświetlaniem raportów START */
	
	private List<TranslationOrder> toHistory;
	
	public void showHistory(ActionEvent ae) {
		EnrichedFile ef = (EnrichedFile) ae
				.getComponent().getAttributes().get("file");
		String name = ef.getName();
		toHistory = new LinkedList<TranslationOrder>();
		for(TranslationOrder to : orders) {
			if (to.getSourceDocument().getName().equals(name)) {
				toHistory.add(to);
			}
		}
		if (toHistory.size() == 0) {
			String message = MessagesLocalizer.getMessage("no.orders.yet");
			if (message == null) {
				message = "No translations pertaining this file has been ordered yet.";
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(message));
		}
	}

	public List<TranslationOrder> getToHistory() {
		return toHistory;
	}

	/* zabawa z wyświetlaniem raportów END */

}
