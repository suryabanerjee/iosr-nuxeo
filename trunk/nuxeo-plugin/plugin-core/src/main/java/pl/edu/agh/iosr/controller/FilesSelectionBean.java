package pl.edu.agh.iosr.controller;

import static pl.edu.agh.iosr.util.IosrLogger.log;

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
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import pl.edu.agh.iosr.persistence.CoreSessionProxy;

/**
 * Pobiera liste plików z workspace'a
 * 
 * Teoretycznie wykonuje pewne funkcje warstwy prezentacji: validacje, ale w tym
 * pakiecie pasuje lepiej
 * */
@Scope(ScopeType.CONVERSATION)
@Name("filesSelectionBean")
public class FilesSelectionBean {

    @In(create = true)
    private CoreSessionProxy coreSessionProxy;
	
    private CoreSession coreSession;
    
	private List<EnrichedFile> files = new LinkedList<EnrichedFile>();

	@Create
	public void init() {

		coreSession = coreSessionProxy.getCoreSession();

		if (coreSession == null) {
			log(this.getClass(), "coreSession is null");
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
