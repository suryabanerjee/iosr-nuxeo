package pl.edu.agh.iosr.controller;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.filemanager.api.FileManager;
import org.nuxeo.ecm.webapp.contentbrowser.DocumentActionsBean;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;

import static pl.edu.agh.iosr.util.IosrLogger.log;

/**
 * Pobiera liste plików z workspace'a
 * 
 * Teoretycznie wykonuje pewne funkcje warstwy prezentacji: validacje,
 * ale w tym pakiecie pasuje lepiej
 * */
@Scope(ScopeType.CONVERSATION)
@Name("filesSelectionBean")
public class FilesSelectionBean {
	
	@In
    protected DocumentsListsManager documentsListsManager;

    @In
    protected CoreSession coreSession;

	private List<EnrichedFile> files = new LinkedList<EnrichedFile>();
	
	@Create
	public void init() {
		
		if (documentsListsManager == null) {
			log(this.getClass(), "Failed to inject documentsListsManager.\n");
		}
		else {
			documentsListsManager.initListManager();
			List<DocumentModel> documents = documentsListsManager.
						getWorkingList(DocumentsListsManager.DEFAULT_WORKING_LIST);
			
			for (DocumentModel dm : documents) {
				files.add(new EnrichedFile(dm));
			}
			
			log(this.getClass(), "FilesSelectionBean successfully initialized." +
					" Found " + documents.size() + " files.\n");
			
			
			try {
				DocumentModel dm = coreSession.getRootDocument();
				
				log(this.getClass(), "Root document name: " + dm.getName());
				log(this.getClass(), "Root document title: " + dm.getTitle());
				log(this.getClass(), "Root document type: " + dm.getDocumentType());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
					FacesMessage fm = new FacesMessage("Cannot create file with the same name \""
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
							FacesContext.getCurrentInstance().addMessage("", fm);
						}
					}
				}
			}
		}
		
		
	}

	public DocumentsListsManager getDocumentsListsManager() {
		return documentsListsManager;
	}

	public void setDocumentsListsManager(DocumentsListsManager documentsListsManager) {
		this.documentsListsManager = documentsListsManager;
	}
}
