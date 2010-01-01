package pl.edu.agh.iosr.persistence;

import java.io.Serializable;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Name;

import pl.edu.agh.iosr.exceptions.DataInconsistencyException;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationOrder.RequestState;
import pl.edu.agh.iosr.services.TranslationOrderService;
import pl.edu.agh.iosr.util.IosrLogger;

@Stateless
@Name("translationOrderService")
public class TranslationOrderServiceImpl implements TranslationOrderService, Serializable {

	private static final long serialVersionUID = 816314691789456174L;

	@PersistenceContext(name="iosr")
	private EntityManager em;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(Long id) {
		
		if (id == null) {
			IosrLogger.log(this.getClass(), "id is null!!");
			return;
		}
		
		TranslationOrder to = em.find(TranslationOrder.class, id);
		if (to != null) {
			em.remove(to);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public TranslationOrder getTranslationOrder(Long id) {
		if (id == null) {
			IosrLogger.log(this.getClass(), "id is null!!");
			return null;
		}
		
		return em.find(TranslationOrder.class, id);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@SuppressWarnings("unchecked")
	public Collection<TranslationOrder> getTranslationOrders(RequestState state) {
		return em.createQuery("from TranslationOrder to where to.reguestState = '" + state + "'").getResultList();
		
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public TranslationOrder saveOrUpdateTranslationOrder(TranslationOrder translationOrder)
			throws DataInconsistencyException {
	
		if (translationOrder == null) {
			IosrLogger.log(this.getClass(), "translationOrder is null!!");
			return null;
		}
		if (translationOrder.getRequestId() == null) {
			em.persist(translationOrder);
		}
		else {
			TranslationOrder to = getTranslationOrder(translationOrder.getRequestId());
			if (to == null) {
				em.persist(translationOrder);
			}
			else {
				em.merge(translationOrder);
			}
		}
		return translationOrder;
	}

}
