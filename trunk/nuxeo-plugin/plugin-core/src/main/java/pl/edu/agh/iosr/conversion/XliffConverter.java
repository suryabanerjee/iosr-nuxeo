package pl.edu.agh.iosr.conversion;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.PathRef;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.util.IosrLogger.Level;
import pl.edu.agh.xliffhandler.converter.Converter;
import pl.edu.agh.xliffhandler.converter.ConverterFactory;
import pl.edu.agh.xliffhandler.exceptions.ConversionException;
import pl.edu.agh.xliffhandler.utils.ConversionMode;
import pl.edu.agh.xliffhandler.utils.FileType;

/**
 * Implementacja AsynchronousConvertera wykorzystuj�ca bibliotek� file2xliff4j
 * <br>
 *  
 * 
 * @author Maria Szymczak
 * 
 * */

@Name("xliffConverter")
@Scope(ScopeType.APPLICATION)
public class XliffConverter extends AsynchronousConverter{
	
	private static Map<String, FileType> formats = new HashMap<String, FileType>();
	private Converter converter = null;

	public XliffConverter() {
		super();
        formats.put("html", FileType.HTML);
        formats.put("properties", FileType.JAVA_PROPERTIES);
        formats.put("doc", FileType.MSOFFICEDOC);
        formats.put("odt", FileType.ODT);
        formats.put("pdf", FileType.PDF);
        formats.put("txt", FileType.PLAINTEXT);
        formats.put("rtf", FileType.RTF);
        formats.put("word", FileType.WORD);
        formats.put("xlf", FileType.XLIFF);
        formats.put("xml", FileType.XML);
	}
	
	@Create
	public void init() {
		super.init();
	}
	
	@Destroy
	public void shutdown() {
		super.shutdown();
	}
	
	@Override
	public void proceed(ConversionTask conversionTask) {
		
		if(conversionTask.task == SupportedTasks.CONVERT) 
			convertFile(conversionTask.translationOrder);
		else
			reConvertFile(conversionTask.translationOrder);
	}
	
	private void convertFile(TranslationOrder translationOrder) {
		log(this.getClass(), "Converting file: " + translationOrder.getSourceDocument());
		File file = new File(translationOrder.getSourceDocument().toString());
		log(this.getClass(), "FILE: " + file.getAbsolutePath());
		log(this.getClass(), "FILE: " + file.getParent());
		String format = file.getName();
		format = format.substring(format.lastIndexOf(".") + 1);
		Locale locale = new Locale(translationOrder.getLangPair().getFromLang(), 
				translationOrder.getLangPair().getFromLang().toUpperCase());
		System.out.println("to: " + locale);
		StringWriter sw = new StringWriter();
		try {
			converter = ConverterFactory
				.createConverter(formats.get(format), formats.get("xlf"));
			converter.convert(ConversionMode.TO_XLIFF, locale, null, 0,
					Charset.forName("utf-8"), formats.get(format), file.getName(), 
					file.getParent(), null, null, sw);
			translationOrder.setXliff(sw.toString());
		} catch (ConversionException e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
	}
	
	private void reConvertFile(TranslationOrder translationOrder) {
		log(this.getClass(), "ReConverting file: " + translationOrder.getSourceDocument());
		File file = new File(translationOrder.getSourceDocument().reference().toString());
		String format = file.getName();
		format = format.substring(format.lastIndexOf(".") + 1);
		Locale locale = new Locale(translationOrder.getLangPair().getToLang(), 
				translationOrder.getLangPair().getToLang().toUpperCase());
		System.out.println("from: " + locale);
		try {
			converter = ConverterFactory
				.createConverter(formats.get("xlf"), formats.get(format));
			converter.convert(ConversionMode.FROM_XLIFF, locale, null, 0,
					Charset.forName("utf-8"), null, file.getName(), 
					file.getParent(), null, null, null);
		} catch (ConversionException e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
	}
	
	// method only for testing purposes
	public static void main(String[] args) {
		
		XliffConverter xliff = new XliffConverter();
		xliff.init();
		LangPair lp = new LangPair();
		lp.setFromLang("pl");
		lp.setToLang("en");
		TranslationOrder to = new TranslationOrder(new PathRef("E:\\Dokumenty\\STUDIA\\IOSR\\projekt\\trunk\\nuxeo-plugin\\plugin-core\\mary.txt"), lp, 
				"dest.xlf", "1", new Long(1), false);
		xliff.convert(to);
		xliff.reConvert(to);
		
	}
	
}
