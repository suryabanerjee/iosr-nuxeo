package pl.edu.agh.iosr.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Name;

import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.services.TranslationServicesConfigService;
import pl.edu.agh.iosr.util.IosrLogger;

@Name("translationServicesConfigService")
@Stateless
public class TranslationServicesConfigServiceImpl implements
		TranslationServicesConfigService, Serializable {

	private static final long serialVersionUID = 4577750858672263215L;

	@PersistenceContext(name = "iosr")
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(Long id) {
		if (id == null) {
			IosrLogger.log(this.getClass(), "id is null!!");
			return;
		}

		TranslationServiceDescription t = em.find(
				TranslationServiceDescription.class, id);
		if (t != null) {
			em.remove(t);
		}
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public TranslationServiceDescription getTranslationService(Long wsId) {
		if (wsId == null) {
			IosrLogger.log(this.getClass(), "wsId is null!!");
			return null;
		}

		// return em.find(TranslationServiceDescription.class, wsId);
		TranslationServiceDescription tsd;
		List<TranslationServiceDescription> list = em.createQuery(
						"from TranslationServiceDescription tsd where tsd.wsId = :wsId")
				.setParameter("wsId", wsId).getResultList();
		if (list.isEmpty()) {
			return null;
		}
		else {
			tsd = list.get(0);
			tsd.getSupportedDocumentTypes().size();
			tsd.getSupportedLangPairs().size();
			tsd.getSupportedQualities().size();
			return tsd;
		}
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Collection<TranslationServiceDescription> getTranslationServices() {
		
		return em.createQuery("FROM TranslationServiceDescription tsd FETCH JOIN" +
				" tsd.supportedDocumentTypes FETCH JOIN " +
				" tsd.supportedLangPairs FETCH JOIN " +
				" tsd.supportedQualities")
				.getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public TranslationServiceDescription saveOrUpdateTranslationService(
			TranslationServiceDescription translationService) {

		if (translationService == null) {
			IosrLogger.log(this.getClass(), "translationService is null!!");
			return null;
		}

		if (translationService.getWsId() == null) {
			em.persist(translationService);
		}
		else {
			TranslationServiceDescription t = getTranslationService(translationService
					.getWsId());
			if (t == null) {
				em.persist(translationService);
			}
			else {
				return em.merge(translationService);
			}
		}
		return translationService;
	}

}
