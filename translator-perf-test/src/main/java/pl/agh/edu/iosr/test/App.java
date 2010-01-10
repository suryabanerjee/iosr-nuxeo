package pl.agh.edu.iosr.test;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.xml.sax.SAXException;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

/**
 * Klasa,ktora jest samplerem dla narzedzia JMeter, pozwala
 * ona przeprowadzic testy wydajnosciowe dla serwisu GoogleTranslator
 * Zobacz http://jakarta.apache.org/jmeter/usermanual/component_reference.html#Java_Request
 * @author lewickitom
 */
public class App implements JavaSamplerClient{
	
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

	@Override
	public Arguments getDefaultParameters() {
		Arguments args=new Arguments();
		Argument arg=new Argument();
		arg.setName("taki");
		args.addArgument(arg);
		
		return args;
	}
	
	/**
	 * 
	 * 
	 */
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		SampleResult result=new SampleResult();
		try {
			Translate.setHttpReferrer("localhost");
			result.sampleStart();
			Translate.execute("Building", Language.ENGLISH ,Language.POLISH);
			result.sampleEnd();
			//translateFile(new File("test.xliff"),Integer.valueOf(100));
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.setSuccessful(true);
		return result; 
	}

	@Override
	public void setupTest(JavaSamplerContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teardownTest(JavaSamplerContext arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private static void translateFile(File sourceFile,int chunkSize) throws IOException,		//TODO optimization
	ParserConfigurationException, SAXException, Exception,
	TransformerException, TransformerConfigurationException {
		
		XliffParser xliffParser=new XliffParser(sourceFile);
		Map<String, String> sourceText=xliffParser.getSourceText();
		Map<String,String> translatedText=new HashMap<String, String>();
		for(Map.Entry<String,String> unit:sourceText.entrySet()){
			//String translatedUnitText=Translate.execute(unit.getValue(), Language.ENGLISH ,Language.POLISH);
		//	translatedText.put(unit.getKey(), translatedUnitText);
			System.out.println("next");
		}
		File resultFile=xliffParser.createXliffWithTranslation(translatedText, Language.POLISH.toString(),"result.sliff");

	}

}
