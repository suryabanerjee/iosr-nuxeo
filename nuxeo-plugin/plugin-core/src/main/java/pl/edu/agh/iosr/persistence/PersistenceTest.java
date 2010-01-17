package pl.edu.agh.iosr.persistence;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.model.DocumentRefWrapper;
import pl.edu.agh.iosr.model.DocumentType;
import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.Quality;
import pl.edu.agh.iosr.model.StringEntity;
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

	/**
	 * Test na obsługę obiektów klasy {@link TranslationOrder}.
	 * */
	public void testTranslationOrder() {
		LangPair lp = new LangPair("pl", "en");
		TranslationOrder to = new TranslationOrder();
		to.setLangPair(lp);
		
		DocumentRefWrapper df = new DocumentRefWrapper();
		df.setName("name");
		df.setPath("pith/path");
		df.setType("type");
		
		try {
			
			IosrLogger.log(this.getClass(),
					"trying to persist translationORder: " + to);
			
			to = translationOrderService.saveOrUpdateTranslationOrder(to);

			to.setQuality("abc");
			to.nextState();
			
			to.setDestinationDocument(df);

			IosrLogger.log(this.getClass(),
					"trying to update translationORder: " + to);
			
			to = translationOrderService.saveOrUpdateTranslationOrder(to);

			TranslationOrder tmp = translationOrderService
					.getTranslationOrder(to.getRequestId());

			IosrLogger.log(this.getClass(),
					"updated translationORder: " + tmp);
			
			if (tmp == null) {
				IosrLogger.log(this.getClass(),
					"failed to update translationOrder - retrived null");
				return;
			}
			
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
			
			if (!df.getPath().equals(tmp.getDestinationDocument().getPath())) {
				IosrLogger.log(this.getClass(),
						"failed to update translationOrder, documentRef");
				return;
			}
			
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

	/**
	 * Test na obsługę obiektów klasy {@link TranslationServiceDescription}.
	 * */
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
			
			tsd.getSupportedDocumentTypes().add(new DocumentType("pdf"));
			tsd.getSupportedQualities().add(new Quality("double checked"));
			
			IosrLogger.log(this.getClass(),
				"trying to update translationServiceDescription: " + tsd);
		
			tsd = translationServicesConfigService
					.saveOrUpdateTranslationService(tsd);

			TranslationServiceDescription tmp = translationServicesConfigService
					.getTranslationService(tsd.getWsId());
			
			IosrLogger.log(this.getClass(),
					"updated translationServiceDescription: " + tmp);
			
			if (tmp == null) {
				IosrLogger.log(this.getClass(),
					"failed to update translationServiceDescription - retrived null");
				return;
			}
			
			if (!tmp.getSupportedLangPairs().iterator().hasNext()) {
				
				LangPair l = tmp.getSupportedLangPairs().iterator().next(); 
				if (!l.getFromLang().equals(lp.getFromLang()) || !l.getToLang().equals(lp.getToLang())) {
						IosrLogger.log(this.getClass(),
							"failed to update translationServiceDescription, supportedLangPairs");
						return;
				}
			}
			
			if (!"endpoint".equals(tmp.getEndpoint())) {
				IosrLogger.log(this.getClass(),
						"failed to update translationServiceDescription, endpoint");
				return;
			}
			
			if (!tmp.getSupportedDocumentTypes().iterator().hasNext()) {
					
				StringEntity se = tmp.getSupportedDocumentTypes().iterator().next(); 
				if (!se.getValue().equals("pdf")) {
						IosrLogger.log(this.getClass(),
							"failed to update translationServiceDescription, supportedDocumentTypes");
						return;
				}
			}
			
			if (!tmp.getSupportedQualities().iterator().hasNext()) {
				
				StringEntity se = tmp.getSupportedQualities().iterator().next(); 
				if (!se.getValue().equals("double checked")) {
						IosrLogger.log(this.getClass(),
							"failed to update translationServiceDescription, supportedQualities");
						return;
				}
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
