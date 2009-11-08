package pl.edu.agh.iosr.view;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.ecm.webapp.pagination.ResultsProvidersCache;

@Scope(ScopeType.CONVERSATION)
@Name("translator")
public class TranslatorBean implements Serializable, Translator {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(TranslatorBean.class);
    
    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true)
    protected transient WebActions webActions;

    @In(create = true)
    protected transient CoreSession documentManager;

    @In(create = true)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(required = true)
    protected transient ResultsProvidersCache resultsProvidersCache;

    @In(create=true)
    protected transient DocumentsListsManager documentsListsManager;
    
    /* tutaj integracja ze springiem */
    @In("#{dupek}")
    protected SpringStub ss; 
    
    /* wstrzyknieta konfiguracja tlumaczenia, przez Seam */
    @In("#{translationConfig}")
    protected TranslationConfig translationConfig;
    
    /**
     * Prawdziwe tlumaczenie.
     * Na razie loguje to co mialoby sie stac :].
     * 
     * Tzn jakie zazanaczone dokumenty mialy by byc przetlumaczone z
     * czego na co.
     * */
	public String translate() throws Exception {
		
		String report = "Trying to translate ";

		if (documentsListsManager != null) {
			
			List<DocumentModel> list = documentsListsManager.getWorkingList(documentsListsManager.CURRENT_DOCUMENT_SELECTION);
			
			if (list != null) {
				report += list.size() + " file(s):\n";
				for (DocumentModel dModel : list) {
					report += "\t" + dModel.getName() + "\n";
				}
			}
		}
		
		if (translationConfig != null) {
			report += "from " + translationConfig.getLangFrom()
			+ " to " + translationConfig.getLangTo() + ".";
		}
		
		log.info(report);
		return "#";
	}

    
}
