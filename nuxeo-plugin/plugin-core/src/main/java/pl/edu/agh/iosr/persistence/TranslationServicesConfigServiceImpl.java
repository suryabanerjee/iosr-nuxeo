package pl.edu.agh.iosr.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import pl.edu.agh.iosr.model.DocumentType;
import pl.edu.agh.iosr.model.Quality;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.services.TranslationServicesConfigService;
import pl.edu.agh.iosr.util.IosrLogger;
import pl.edu.agh.iosr.ws.RemoteWSInvoker;

@Name("translationServicesConfigService")
@Stateless
public class TranslationServicesConfigServiceImpl implements
		TranslationServicesConfigService, Serializable {

	private static final long serialVersionUID = 4577750858672263215L;

	@PersistenceContext(name = "iosr")
	private EntityManager em;

	@In(value = "#{remoteWSInvoker}")
	private RemoteWSInvoker remoteWsInvoker;

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
		List<TranslationServiceDescription> list = em
				.createQuery(
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
	public List<TranslationServiceDescription> getTranslationServices() {
		/*
		 * return em.createQuery(
		 * "FROM TranslationServiceDescription tsd JOIN FETCH " +
		 * " tsd.supportedDocumentTypes JOIN FETCH " +
		 * " tsd.supportedLangPairs JOIN FETCH " +
		 * " tsd.supportedQualities").getResultList();
		 */
		List<TranslationServiceDescription> list = em.createQuery(
				"FROM TranslationServiceDescription tsd").getResultList();
		
		for (TranslationServiceDescription t : list) {
			t.getSupportedDocumentTypes().size();
			t.getSupportedLangPairs().size();
			t.getSupportedQualities().size();
		}
		
		return list;
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

	/**
	 * Synchroniczne odświeżanie informacji o web servicie.
	 * 
	 * Dobrze by było, jakby zniecierpliwiony brakiem odpowiedzi, po pewnym
	 * czasie, WsInvoker rzucał jakiś wyjątek.
	 * */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void refreshWs(Long id) throws InterruptedException {
		if (id == null) {
			return;
		}
		TranslationServiceDescription t = getTranslationService(id);
		if (t == null) {
			return;
		}
		else {
			try {
				t
						.setSupportedQualities(transform(remoteWsInvoker
								.getSuppportedQualities(t),
								stringToQualityTransformer));
				t.setSupportedLangPairs(remoteWsInvoker
						.getSuppportedTranslations(t));
				t.setSupportedDocumentTypes(transform(remoteWsInvoker
						.getSuppportedFileFormats(t),
						stringToDocumentTypeTransformer));
				em.merge(t);
			}
			catch (Exception e) {
				throw new InterruptedException(e.getMessage());
			}
		}
	}

	/* zabawy czopykowe z templatami START */

	public interface Transformer<S, D> {
		public D transform(S object);
	}

	private Transformer<String, Quality> stringToQualityTransformer = new Transformer<String, Quality>() {
		public Quality transform(String object) {
			return new Quality(object);
		}
	};

	private Transformer<String, DocumentType> stringToDocumentTypeTransformer = new Transformer<String, DocumentType>() {
		public DocumentType transform(String object) {
			return new DocumentType(object);
		}
	};

	private <S, D> List<D> transform(Collection<S> c,
			Transformer<S, D> transformer) {
		List<D> list = new LinkedList<D>();
		for (S s : c) {
			list.add(transformer.transform(s));
		}
		return list;
	}

	/* zabawy czopykowe z templatami END */

}
