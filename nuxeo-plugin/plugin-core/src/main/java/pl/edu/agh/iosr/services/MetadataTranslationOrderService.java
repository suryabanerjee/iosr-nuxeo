package pl.edu.agh.iosr.services;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jboss.seam.annotations.In;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import pl.edu.agh.iosr.exceptions.DataInconsistencyException;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationOrder.RequestState;

public class MetadataTranslationOrderService implements TranslationOrderService {

	
	@In(create = true, required = false)
    protected transient CoreSession coreSession;
	
	public Map <Long,TranslationOrder> translationOrders=Collections.synchronizedMap(new HashMap<Long,TranslationOrder>());
	

	public TranslationOrder getTranslationOrder(Long id) {
		return translationOrders.get(id);
	}

	public Collection<TranslationOrder> getTranslationOrders(RequestState state) {
		
		List<TranslationOrder> results=new ArrayList<TranslationOrder>();
		for(TranslationOrder translationOrder: translationOrders.values()){
			if(translationOrder.getState()==state)
				results.add(translationOrder);
			
		}
		return results;
	}

	public void saveOrUpdateTranslationOrder(TranslationOrder translationOrder)
			throws DataInconsistencyException {
		
		log(this.getClass(), "saveOrUpdateTranslationOrder called", Level.INFO);
		
		try {
			DocumentModel documentModel=coreSession.getDocument(translationOrder.getSourceDocument());
			
			
		} catch (ClientException e) {
			log(this.getClass(), e.getMessage(), Level.SEVERE);
			e.printStackTrace();
		}
		
		
	}

	public void delete(Long id) {
		translationOrders.remove(id);
	}

	
}
