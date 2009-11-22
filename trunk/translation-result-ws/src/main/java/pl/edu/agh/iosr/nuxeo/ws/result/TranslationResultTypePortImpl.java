package pl.edu.agh.iosr.nuxeo.ws.result;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.TranslationStatus;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;

public class TranslationResultTypePortImpl implements TranslationResultPortType{

	@Override
	public String sendFileResult(String translationRequestID, String file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendStatus(String translationRequestID, TranslationStatus status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendStringResult(String translationRequestID, String text) {
		System.out.println("<<< Translation Result WS >>>");
		System.out.println("Text in response:");
		System.out.println(text);
		return null;
	}

}
