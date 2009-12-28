package pl.edu.agh.iosr.persistence;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Name;

import pl.edu.agh.iosr.exceptions.DataInconsistencyException;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationOrder.RequestState;
import pl.edu.agh.iosr.services.TranslationOrderService;

@Stateless
@Name("translationOrderService")
public class TranslationOrderServiceImpl implements TranslationOrderService {

	@PersistenceContext(name="iosr")
	private EntityManager em;
	
	public void delete(Long id) {
		TranslationOrder to = em.find(TranslationOrder.class, id);
		if (to != null) {
			em.remove(to);
		}
	}

	public TranslationOrder getTranslationOrder(Long id) {
		return em.find(TranslationOrder.class, id);
	}

	public Collection<TranslationOrder> getTranslationOrders(RequestState state) {
		return em.createQuery("from TranslationOrder to where to.reguestState = '" + state + "'").getResultList();
		
	}

	public void saveOrUpdateTranslationOrder(TranslationOrder translationOrder)
			throws DataInconsistencyException {
		TranslationOrder to = getTranslationOrder(translationOrder.getRequestId());
		if (to == null) {
			em.persist(translationOrder);
		}
		else {
			em.merge(translationOrder);
		}
	}

}
