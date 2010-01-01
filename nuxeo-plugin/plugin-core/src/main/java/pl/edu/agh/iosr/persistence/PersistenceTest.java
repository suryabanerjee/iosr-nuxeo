package pl.edu.agh.iosr.persistence;

import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentRef;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.services.TranslationOrderService;
import pl.edu.agh.iosr.services.TranslationServicesConfigService;
import pl.edu.agh.iosr.util.IosrLogger;
import pl.edu.agh.iosr.util.IosrLogger.Level;

/**
 * Klasa posiada testy integracyjne na mechanizmy persystencji.
 * */
@Scope(ScopeType.APPLICATION)
@Name("persistenceTest")
public class PersistenceTest implements Serializable {

	private static final long serialVersionUID = 3586056440915677944L;

	@In(create = true, value = "#{translationOrderService}")
	private TranslationOrderService translationOrderService;

	@In(create = true, value = "#{translationServicesConfigService}")
	private TranslationServicesConfigService translationServicesConfigService;

	public void testTranslationOrder() {
		LangPair lp = new LangPair("pl", "en");
		TranslationOrder to = new TranslationOrder();
		to.setLangPair(lp);
		DocumentRef df = new DocumentRef() {
			private static final long serialVersionUID = 1218374980873504947L;

			public int type() {
				return 0;
			}

			public Object reference() {
				return null;
			}
		};
		
		try {
			
			IosrLogger.log(this.getClass(),
					"trying to persist translationORder: " + to);
			
			to = translationOrderService.saveOrUpdateTranslationOrder(to);

			to.setQuality("abc");
			to.nextState();
			
			
//		 	transient wiec nie są persystowane
		//	to.setDestinationDocument(df);

			IosrLogger.log(this.getClass(),
					"trying to update translationORder: " + to);
			
			to = translationOrderService.saveOrUpdateTranslationOrder(to);

			TranslationOrder tmp = translationOrderService
					.getTranslationOrder(to.getRequestId());

			IosrLogger.log(this.getClass(),
					"updated translationORder: " + tmp);
			
			if (!"abc".equals(tmp.getQuality())) {
				IosrLogger.log(this.getClass(),
						"failed to update translationOrder, quality");
				return;
			}
			if (0 != TranslationOrder.RequestState.UNDER_CONVERSION.compareTo(tmp
					.getState())) {
				IosrLogger.log(this.getClass(),
						"failed to update translationOrder, state");
				return;
			}
			/*
			if (!df.equals(tmp.getDestinationDocument())) {
				IosrLogger.log(this.getClass(),
						"failed to update translationOrder, documentRef");
				return;
			}
			*/
			IosrLogger.log(this.getClass(),
				"TranslationOrder test passed!", Level.INFO);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				translationOrderService.delete(to.getRequestId());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void testServicesDescriptions() {
		LangPair lp = new LangPair("pl", "en");
		TranslationServiceDescription tsd = new TranslationServiceDescription();
		
		try {
			
			IosrLogger.log(this.getClass(),
				"trying to persist translationServiceDescription: " + tsd);
			
			tsd = translationServicesConfigService
					.saveOrUpdateTranslationService(tsd);

			tsd.getSupportedLangPairs().add(lp);
			
			tsd.setEndpoint("endpoint");
			
		// 	transient wiec nie są persystowane
		//	tsd.setSupportedDocumentTypes(new String[] {"pdf", "txt"});
		//	tsd.setSupportedQualities(new String[] {"double checked"});
			
			IosrLogger.log(this.getClass(),
				"trying to update translationServiceDescription: " + tsd);
		
			tsd = translationServicesConfigService
					.saveOrUpdateTranslationService(tsd);

			TranslationServiceDescription tmp = translationServicesConfigService
					.getTranslationService(tsd.getWsId());
			
			IosrLogger.log(this.getClass(),
					"updated translationServiceDescription: " + tmp);
			
			if (!tmp.getSupportedLangPairs().iterator().hasNext() ||
				!tmp.getSupportedLangPairs().iterator().next().getFromLang().equals(lp.getFromLang()) ||
				!tmp.getSupportedLangPairs().iterator().next().getToLang().equals(lp.getToLang())) {
					IosrLogger.log(this.getClass(),
						"failed to update translationServiceDescription, supportedLangPairs");
					return;
			}
			if (!"endpoint".equals(tmp.getEndpoint())) {
				IosrLogger.log(this.getClass(),
						"failed to update translationServiceDescription, endpoint");
				return;
			}
			
			IosrLogger.log(this.getClass(),
					"TranslationServiceDescription test passed!", Level.INFO);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				translationServicesConfigService.delete(tsd.getWsId());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
