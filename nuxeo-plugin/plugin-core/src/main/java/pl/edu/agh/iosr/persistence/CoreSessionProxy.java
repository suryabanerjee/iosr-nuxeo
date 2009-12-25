package pl.edu.agh.iosr.persistence;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

@Name("coreSessionProxy")
@Scope(ScopeType.STATELESS)
public class CoreSessionProxy {

	private CoreSession coreSession;
	
    @In(create = true)
    protected transient NavigationContext navigationContext;

	@Create
	public void init() throws Exception {
		
		coreSession = navigationContext.getOrCreateDocumentManager();	
		
		if (coreSession == null) {
			System.out.println("######## coreSession is null");
		}
		else {
			System.out.println("######## " + coreSession);
		}
	}
	
	public CoreSession getCoreSession() {
		
		if (coreSession == null) {
			try {
				init();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (coreSession == null) {
			System.out.println("######## coreSession is null");
		}
		else {
			System.out.println("######## " + coreSession);
		}
		
		return coreSession;
	}

	public void setCoreSession(CoreSession coreSession) {
		this.coreSession = coreSession;
	}

}
