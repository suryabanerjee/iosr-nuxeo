package pl.edu.agh.iosr.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.ecm.webapp.pagination.ResultsProvidersCache;
import org.nuxeo.runtime.api.Framework;

import static pl.edu.agh.iosr.util.IosrLogger.log;

/**
 * Pobiera liste plików z workspace'a
 * 
 * Teoretycznie wykonuje pewne funkcje warstwy prezentacji: validacje, ale w tym
 * pakiecie pasuje lepiej
 * */
@Scope(ScopeType.EVENT)
@Name("filesSelectionBean")
public class FilesSelectionBean {

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true)
    protected transient WebActions webActions;

    @In(create = true)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(required = true)
    protected transient ResultsProvidersCache resultsProvidersCache;
	
	@In(create = true, required = false)
	protected transient CoreSession coreSession;
	
	@In(create=true)
	protected transient DocumentsListsManager documentsListsManager;
	 

	private List<EnrichedFile> files = new LinkedList<EnrichedFile>();

	@Create
	public void init() {

		try {
			if (coreSession == null) {
				//coreSession = Framework.getService(CoreSession.class);
				coreSession = navigationContext.getOrCreateDocumentManager();
				log(this.getClass(), "Core session injected by Framework",
						Level.WARNING);
			}

		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (coreSession != null) {
			try {
				DocumentModelList dml = coreSession
						.query("SELECT * FROM Document");
				
				for (DocumentModel dm : dml) {
					if (dm.hasFacet("Downloadable")) {
						files.add(new EnrichedFile(dm));
						log(this.getClass(), "coreService added: " + dm, Level.INFO);
					}
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (documentsListsManager != null) {
			for (DocumentModel dm : documentsListsManager.getWorkingList(DocumentsListsManager.DEFAULT_WORKING_LIST)) {
				files.add(new EnrichedFile(dm));
				log(this.getClass(), "coreService added: " + dm, Level.INFO);
			}
		}

	}

	public List<EnrichedFile> getFiles() {
		return files;
	}

	public void setFiles(List<EnrichedFile> files) {
		this.files = files;
	}

	/**
	 * sprawdza, czy nazwy docelowe nowych plikow nie pokrywaja sie z
	 * istniejacymi oraz czy w srod nich samych nie ma dubli
	 * */
	public void validate() {
		for (EnrichedFile ef : files) {
			if (ef.getSelected()) {
				if (ef.getDocumentModel().getName().equals(ef.getTargetName())) {
					FacesMessage fm = new FacesMessage(
							"Cannot create file with the same name \""
									+ ef.getTargetName() + "\".");
					FacesContext.getCurrentInstance().addMessage("", fm);
				}
			}
		}

		for (EnrichedFile ef1 : files) {
			if (ef1.getSelected()) {
				for (EnrichedFile ef2 : files) {
					if (ef2.getSelected() && !ef1.equals(ef2)) {
						if (ef1.getTargetName().equals(ef2.getTargetName())) {
							FacesMessage fm = new FacesMessage(
									"Cannot create target files with the same name \""
											+ ef1.getTargetName() + "\".");
							FacesContext.getCurrentInstance()
									.addMessage("", fm);
						}
					}
				}
			}
		}

	}

}
