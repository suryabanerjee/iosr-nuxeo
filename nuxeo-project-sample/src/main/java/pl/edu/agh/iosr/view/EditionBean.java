package pl.edu.agh.iosr.view;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;

import pl.edu.agh.iosr.controller.ConfigurationStorage;
import pl.edu.agh.iosr.controller.EnrichedFile;
import pl.edu.agh.iosr.controller.FilesSelectionBean;
import pl.edu.agh.iosr.controller.Mediator;
import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.TranslationRequest;
import pl.edu.agh.iosr.ws.RemoteWSDescription;

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
	
	private RemoteWSDescription remoteWSDescription;
	private String langTo, langFrom;
	
	@PostConstruct
	public void init() {
		if (configurationStorage != null && configurationStorage.getRemoteWSs() != null) {
			remoteWSDescription = configurationStorage.getRemoteWSs().get(0);
			
			List<LangPair> list = remoteWSDescription.getSupportedTranslation();
			
			if (list != null && list.size() > 0) {
				langFrom = list.get(0).getFrom();
				langTo = list.get(0).getTo();
			}
		}		
	}
	
	/**
	 * Pobiera liste zarejestrowanych serwisow tlumaczacych
	 * */
	public SelectItem[] getAvailableServices() {
		List<RemoteWSDescription> wss = configurationStorage.getRemoteWSs();
		SelectItem[] items = new SelectItem[wss.size()];
		int i = 0;
		for (RemoteWSDescription rd : wss) {
			items[i++] = new SelectItem(rd.getName(), rd.getName());
		}
		return items;
	}
	
	
	/**
	 * Po wybraniu serwisu, pobiera dla niego liste jezykow z ktorych
	 * mozna tlumaczyc
	 * */
	public SelectItem[] getLangsFrom() {
		if (remoteWSDescription == null) {
			return new SelectItem[0];
		}
		
		List<LangPair> langPairs = remoteWSDescription.getSupportedTranslation();
		
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
		if (remoteWSDescription == null || langFrom == null) {
			return new SelectItem[0];
		}
		
		List<LangPair> langPairs = remoteWSDescription.getSupportedTranslation();
		
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

	/**
	 * Potrzebne do usuwania wsow
	 * */
	public void setWsName(String wsName) {
		for (RemoteWSDescription rd : configurationStorage.getRemoteWSs()) {
			if (rd.getName().equals(wsName)) {
				remoteWSDescription = rd;
				break;
			}
		}
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
		
		TranslationRequest translationRequest = new TranslationRequest();
		
		translationRequest.setAuthor(FacesContext.getCurrentInstance().
				getExternalContext().getUserPrincipal().getName());
		
		for (EnrichedFile ef : filesSelectionBean.getFiles()) {
			if (ef.getSelected()) {
				translationRequest.getDocuments().add(ef.clone());
			}
		}
		
		if (translationRequest.getDocuments().size() == 0) {
			FacesContext.getCurrentInstance().addMessage("", 
					new FacesMessage("Select at least one document."));
			return "#";
		}
		
		translationRequest.setLangPair(new LangPair(langFrom, langTo));
		
		// TODO zweryfikować poprawność budowy requestu
		mediator.enqueuRequest(translationRequest);
		
		return "#";
	}

	public ConfigurationStorage getConfigurationStorage() {
		return configurationStorage;
	}

	public void setConfigurationStorage(ConfigurationStorage configurationStorage) {
		this.configurationStorage = configurationStorage;
	}

	public RemoteWSDescription getRemoteWSDescription() {
		return remoteWSDescription;
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
