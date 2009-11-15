package pl.edu.agh.iosr.nuxeo.translator.service;

import javax.jws.WebService;

import pl.edu.agh.iosr.nuxeo.schema.translator.TranslateRequestWrapper;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType;

@WebService(targetNamespace = "http://agh.edu.pl/iosr/nuxeo/wsdl/Translator.wsdl", 
        portName="TranslatorPort",
        serviceName="TranslatorService", 
        endpointInterface="pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType")
        
public class OtherTranslatorPortTypeImpl implements TranslatorPortType{
	@Override
	public String translate(TranslateRequestWrapper parameters) {		
		return parameters.getContent() + "form other";
	}
	
}