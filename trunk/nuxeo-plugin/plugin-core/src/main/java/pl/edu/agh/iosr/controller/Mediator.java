package pl.edu.agh.iosr.controller;

import java.io.File;

import pl.edu.agh.iosr.conversion.XliffConverter;
import pl.edu.agh.iosr.exceptions.DataInconsistencyException;
import pl.edu.agh.iosr.exceptions.OrderDoesNotExistException;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.services.*;
import pl.edu.agh.iosr.ws.RemoteWSInvoker;

import static pl.edu.agh.iosr.util.IosrLogger.log;

/**
 * Mediator, koordynuje dzia�ania innych komponent�w,
 * nale�y unika� dodawania tu logiki za wyj�tkiem sterowania
 * 
 * Poniewaz se springiem jest kupa problemow prawdopodobnie
 * wszystko co mozliwe napiszemy w Seamie (to potem tez),
 * narazie to jest Spring, patrz ApplicationContext.xml
 * 
 * <br>
 * 1.12.2009
 * Trzeba będzie przemyśleć konieczność implementowania
 * wszystkich interfejsów, narazie daję wolną rękę
 * 
 * @author czopson
 * */
public class Mediator {
	
	private XliffConverter xliffConverter;
	
	private ValidationService validationService;
	
	private RepositoryProxyService documentAccessService;
	
	private TranslationOrderService translationOrderService;
	
	private TranslationServicesConfigService translationServicesConfigService;
	
	private RemoteWSInvoker remoteWSInvoker;
		
	/**
	 * Kolejkuje zamówienie
	 * */
	public void enqueuRequest(TranslationOrder request) {
		log(this.getClass(), "Złożono zamówienie na przekład:\n" + request.toString());
	}
	
	
	/**
	 * zglasza ��danie translacji i przprowadza operacje konieczne by wyslac je do tlumaczenia
	 * */
	public void submitTranslationOrder(TranslationOrder order){
		try {
			
			TranslationServiceDescription tsDescription=translationServicesConfigService.getTranslationService(order.getWsId());
			validationService.validate(order,tsDescription);
			translationOrderService.saveOrUpdateTranslationOrder(order);
		
			String fileExtension=documentAccessService.getFileExtension(order.getSourceDocument());
			if(validationService.isConversionNeeded(fileExtension,tsDescription)){
				xliffConverter.convert(order);
			}
			else{
				File fileToTranslate=documentAccessService.getFile(order.getSourceDocument());
				remoteWSInvoker.traslateAsync(tsDescription, order, fileToTranslate);
			}
			
		} catch (DataInconsistencyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * zglasza rezultaty translacji i przeprowadza operacje konieczne by przetlumaczenie zostalo zapisane 
	 * */
	public void submitTranslationResult(Long id,File resultFile){
		
		try {
			
			TranslationOrder order = translationOrderService.getTranslationOrder(id);

			if(validationService.isReconversionNeeded(order)){
				
				order.setXliff(resultFile);		
				xliffConverter.reConvert(order);					//TODO pobranie pliku i zapisanie go w Nuxeo
								
			}
			else{
				
				documentAccessService.saveFile(order,resultFile);
			
			}
			
		} catch (OrderDoesNotExistException e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	
	
}
