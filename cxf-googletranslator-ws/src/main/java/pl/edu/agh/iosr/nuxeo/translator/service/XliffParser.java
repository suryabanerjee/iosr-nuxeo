package pl.edu.agh.iosr.nuxeo.translator.service;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.BitSet;



/**
 * Klasa ta pozwala na wyciagniecie z dokumentu typu xliff tresci ktora nalezy przetlumaczyc,
 * a takze dodac przetlumaczona tresc ujmujac je w odpowiednie tagi i wyeksportowac tak zmodyfikowany xliff do pliku.
 * W momencie tworzenia obiekt tej klasy zwiazany zostaje z konkretnym plikiem xliff
 * @author lewickitom
 * */ 
public class XliffParser {
	
	
	private File xliff;
	private static final String TRANSLATION_UNIT_TAG="trans-unit";
	private static final String SOURCE_TAG="source";
	private static final String SOURCE_VALUE_TAG="mrk";
	private static final String ID_TAG="id";
	
	private static final String TRANSLATED_TEXT_TAG="id";
	private static final String TRANSLATION_LANGUAGE_TAG="id";
	

	public XliffParser(File xliffFile) {
		xliff = xliffFile;
	}
	/**
	 * wyjmuje tresc ktora nalezy przetlumaczyc
	 * */
	public Map<String, String> getSourceText() throws IOException, ParserConfigurationException, SAXException {
		
		Document doc = getDocumentForXliff();		
		Map<String, String> text = new HashMap<String, String>();
		
		NodeList units = doc.getElementsByTagName(TRANSLATION_UNIT_TAG);
		
		for(int i=0; i < units.getLength(); ++i) {
			Element unit = (Element) units.item(i); 
			Element source = (Element) unit.getElementsByTagName(SOURCE_TAG).item(0);
			Element mrk = (Element) source.getElementsByTagName(SOURCE_VALUE_TAG).item(0);
			text.put(unit.getAttributeNode(ID_TAG).getValue(), mrk.getTextContent());
		}
		 
		return text;	
		
	}
	
	/**
	 * Dodaje przetlumaczona tresc ujeta w odpowiednie tagi oraz eksportuje tak zmodyfikowany xliff do pliku
	 * */
	public File createXliffWithTranslation(Map<String, String> translatedText, String translationLanguage, String outputFilename) throws TransformerException, IOException, ParserConfigurationException, SAXException, TransformerConfigurationException {

		Document doc = getDocumentForXliff();
		NodeList units = doc.getElementsByTagName(TRANSLATION_UNIT_TAG);
		
		for(int i=0; i < units.getLength(); ++i) {
			Element unit = (Element) units.item(i); 
			String id = unit.getAttributeNode(ID_TAG).getValue();
			Element target = doc.createElement(TRANSLATED_TEXT_TAG);
			target.setAttribute(TRANSLATION_LANGUAGE_TAG, translationLanguage);
			target.setTextContent(translatedText.get(id));
			unit.appendChild(target);
		}

		File outputFile=documentToFile(outputFilename, doc);  
		return outputFile;
		
	}

	private File documentToFile(String outputFilename, Document doc)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException {
		
		TransformerFactory tranFactory = TransformerFactory.newInstance();  
		Transformer aTransformer = tranFactory.newTransformer();  
		Source src = new DOMSource(doc);  
		Result dest = new StreamResult(outputFilename);  
		aTransformer.transform(src, dest);
		return new File(outputFilename);
		
	}

	private Document getDocumentForXliff() throws ParserConfigurationException,
			SAXException, IOException {
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse (xliff);
		return doc;
		
	}

	public static void main(String[] args) {
		
		try {
			XliffParser x = new XliffParser(new File("C:\\Documents and Settings\\Tomek\\Pulpit\\nuxeo\\marianka\\marianka\\mary.html.xliff"));
			Map<String, String> map = x.getSourceText();
			x.createXliffWithTranslation(map, "en_EN","result");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
