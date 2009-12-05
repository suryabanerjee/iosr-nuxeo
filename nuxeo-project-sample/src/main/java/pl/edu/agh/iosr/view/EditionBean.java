package pl.edu.agh.iosr.view;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
import static pl.edu.agh.iosr.util.IosrLogger.log;

/**
 * Backing bean dla widoku wyboru tłumaczenia
 * 
 * 'wyświetla' tabelke z plikami oraz opcje tłumaczenie
 * 
 * tworzy rządzanie tłumaczenia TranslationRequest i wysyła je
 * do mediatora.
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

	@In(create=true)
    private FilesSelectionBean filesSelectionBean;
	
	@In(create=true)
	private ConfigurationStorage configurationStorage;
	
	private TranslationServiceDescription translationServiceDescription;
	private String langTo, langFrom;
	
	@Create
	public void init() {
		if (configurationStorage != null && configurationStorage.getRemoteWSs() != null) {
			translationServiceDescription = configurationStorage.getRemoteWSs().get(0);
			
			Collection<LangPair> list = translationServiceDescription.getSupportedLangPairs();
			
			if (list != null && list.size() > 0) {
				langFrom = list.iterator().next().getFrom();
				langTo = list.iterator().next().getTo();
			}
		}		
	}
	
	/**
	 * Pobiera liste zarejestrowanych serwisow tlumaczacych
	 * */
	public SelectItem[] getAvailableServices() {
		List<TranslationServiceDescription> wss = configurationStorage.getRemoteWSs();
		SelectItem[] items = new SelectItem[wss.size()];
		int i = 0;
		for (TranslationServiceDescription rd : wss) {
			items[i++] = new SelectItem(rd.getName(), rd.getName());
		}
		return items;
	}
	
	
	/**
	 * Po wybraniu serwisu, pobiera dla niego liste jezykow z ktorych
	 * mozna tlumaczyc
	 * */
	public SelectItem[] getLangsFrom() {
		if (translationServiceDescription == null) {
			return new SelectItem[0];
		}
		
		Collection<LangPair> langPairs = translationServiceDescription.getSupportedLangPairs();
		
		Set<String> froms = new HashSet<String>();
		
		for (LangPair lp : langPairs) {
			froms.add(lp.getFrom());
		}
		
		SelectItem[] items = new SelectItem[froms.size()];
		int i = 0;
		for (String s : froms) {
			items[i++] = new SelectItem(s, s);
		}
		return items;				
	}
	
	
	/**
	 * Po wybraniu jezyka z ktorego ma byc tlumaczone,
	 * zwraca liste jezyka na ktore moze nastapic przeklad
	 * */
	public SelectItem[] getLangsTo() {
		if (translationServiceDescription == null || langFrom == null) {
			return new SelectItem[0];
		}
		
		Collection<LangPair> langPairs = translationServiceDescription.getSupportedLangPairs();
		
		Set<String> froms = new HashSet<String>();
		
		for (LangPair lp : langPairs) {
			if (lp.getFrom().equals(langFrom)) {
				froms.add(lp.getTo());
			}
		}
		
		SelectItem[] items = new SelectItem[froms.size()];
		int i = 0;
		for (String s : froms) {
			items[i++] = new SelectItem(s, s);
		}
		return items;						
	}


	public void setWsName(String wsName) {
		for (TranslationServiceDescription rd : configurationStorage.getRemoteWSs()) {
			log(this.getClass(), "trying to compare: " + rd.getName() + " vs " + wsName);
			if (rd.getName().equals(wsName)) {
				translationServiceDescription = rd;
				log(this.getClass(), "translationSeviceDescription set to: " + rd);
				break;
			}
		}
		log(this.getClass(), "failed to find suitable translationServiceDescription!");
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
		
		for (EnrichedFile ef : filesSelectionBean.getFiles()) {
			if (ef.getSelected()) {
				translationOrder = new TranslationOrder(
						ef.getDocumentModel().getRef(),
						new LangPair(langFrom, langTo), 
						ef.getTargetName(), 
						"", 
						translationServiceDescription.getWsId());
				mediator.enqueuRequest(translationOrder);
			}
		}	
		
		return "#";
	}

	public ConfigurationStorage getConfigurationStorage() {
		return configurationStorage;
	}

	public void setConfigurationStorage(ConfigurationStorage configurationStorage) {
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
	
}
