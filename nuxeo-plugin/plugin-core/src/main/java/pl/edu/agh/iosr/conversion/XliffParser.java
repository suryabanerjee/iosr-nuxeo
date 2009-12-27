package pl.edu.agh.iosr.conversion;

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
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.BitSet;

public class XliffParser {
	
	File xliff;
	Map<String, String> text = new HashMap<String, String>();

	public XliffParser(String file) {
		xliff = new File(file);
	}

	public Map<String, String> getText() throws IOException, ParserConfigurationException, SAXException {
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse (new File(xliff.getName()));
		
		NodeList units = doc.getElementsByTagName("trans-unit");
		
		for(int i=0; i < units.getLength(); ++i) {
			Element unit = (Element) units.item(i); 
			Element source = (Element) unit.getElementsByTagName("source").item(0);
			Element mrk = (Element) source.getElementsByTagName("mrk").item(0);
			text.put(unit.getAttributeNode("id").getValue(), mrk.getTextContent());
		}
		
		
		
		//SAXBuilder builder = new SAXBuilder();
		//Document document = builder.build(xliff.getName());
		//Element root = document.getRootElement();
		//Element file = (Element) root.getChildren().get(0);
		//Element body = (Element) file.getChildren().get(1);
		//List<Element> units = body.getChildren();
		/*for(Element e: units) {
			Element source = (Element) e.getChildren().get(0);
			List<Element> children = source.getChildren();
			for(Element el: children)
				if(el.getName() == "mrk")
					text.put(e.getAttributeValue("id"), el.getText());
		}*/
		System.out.println(text);
		  
		 
		return text;	
	}

	public void putText(Map<String, String> text, String lang) throws TransformerException, IOException, ParserConfigurationException, SAXException, TransformerConfigurationException {

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse (new File(xliff.getName()));

		NodeList units = doc.getElementsByTagName("trans-unit");
		
		for(int i=0; i < units.getLength(); ++i) {
			Element unit = (Element) units.item(i); 
			String id = unit.getAttributeNode("id").getValue();
			Element target = doc.createElement("target");
			target.setAttribute("xml:lang", lang);
			target.setTextContent(text.get(id));
			unit.appendChild(target);
			//Element source = (Element) unit.getElementsByTagName("source").item(0);
			//text.put(unit.getAttributeNode("id").getValue(), ((Element) source.getElementsByTagName("mrk").item(0)).getNodeValue());
		}

		/*for(Element e: units) {
			String id = e.getAttributeValue("id");
			Element target = new Element("target", namespace);
			target.setAttribute("xml:lang", lang);
			target.setText(text.get(id));
			e.addContent(target);
		}
*/
		/*
		XMLOutputter out = new XMLOutputter();
		FileWriter writer = new FileWriter(xliff);
		out.output(doc, writer);
		writer.flush();
		writer.close();
		*/
		TransformerFactory tranFactory = TransformerFactory.newInstance();  
		Transformer aTransformer = tranFactory.newTransformer();  
		Source src = new DOMSource(doc);  
		Result dest = new StreamResult(xliff);  
		aTransformer.transform(src, dest);  
	}

	public static void main(String[] args) {
		try {
		XliffParser x = new XliffParser("E:\\Dokumenty\\STUDIA\\IOSR\\projekt\\nuxeo-project-sample\\mary.odt.xliff");
		Map<String, String> map = x.getText();
		x.putText(map, "en_EN");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
