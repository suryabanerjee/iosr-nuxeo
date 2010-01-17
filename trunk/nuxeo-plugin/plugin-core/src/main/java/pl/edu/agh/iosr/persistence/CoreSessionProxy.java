package pl.edu.agh.iosr.persistence;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

import pl.edu.agh.iosr.util.IosrLogger;

/**
 * Próba utrzymania referencji do obiektu <code>coreSession</code>.
 * */
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
			IosrLogger.log(this.getClass(), "coreSession is null");
		}
		else {
			IosrLogger.log(this.getClass(), "coreSession properly initialized");
		}

	}

	/**
	 * @return {@link CoreSession} - możliwe najbardziej działającą referencję.
	 * */
	public CoreSession getCoreSession() {

		if (coreSession == null) {
			try {
				init();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (coreSession == null) {
			IosrLogger.log(this.getClass(), "coreSession is null");
		}

		return coreSession;
	}

	public void setCoreSession(CoreSession coreSession) {
		this.coreSession = coreSession;
	}

}
