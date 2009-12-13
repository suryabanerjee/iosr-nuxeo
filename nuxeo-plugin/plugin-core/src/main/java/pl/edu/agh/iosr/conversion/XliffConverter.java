package pl.edu.agh.iosr.conversion;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import file2xliff4j.ConversionException;
import file2xliff4j.ConversionMode;
import file2xliff4j.Converter;
import file2xliff4j.ConverterFactory;
import file2xliff4j.FileType;
import pl.edu.agh.iosr.model.TranslationOrder;

public class XliffConverter extends AsynchronousConverter{
	
	private static Map<String, FileType> formats = new HashMap<String, FileType>();
	private Converter converter = null;

	public XliffConverter() {
		super();
		formats.put("xls", FileType.EXCEL);
        formats.put("html", FileType.HTML);
        formats.put("properties", FileType.JAVA_PROPERTIES);
        formats.put("mif", FileType.MIF);
        formats.put("doc", FileType.MSOFFICEDOC);
        formats.put("odp", FileType.ODP);
        formats.put("ods", FileType.ODS);
        formats.put("odt", FileType.ODT);
        formats.put("pdf", FileType.PDF);
        formats.put("txt", FileType.PLAINTEXT);
        formats.put("po", FileType.PO);
        formats.put("ppt", FileType.PPT);
        formats.put("rtf", FileType.RTF);
        formats.put("winrc", FileType.WINRC);
        formats.put("word", FileType.WORD);
        formats.put("xlf", FileType.XLIFF);
        formats.put("xml", FileType.XML);
        formats.put("xuldtd", FileType.XULDTD);
	}
	
	@Override
	public void proceed(ConversionTask conversionTask) {
		
		if(conversionTask.task.equals(SupportedTasks.CONVERT)) 
			convertFile(conversionTask.translationOrder);
		else
			reConvertFile(conversionTask.translationOrder);
	}
	
	private void convertFile(TranslationOrder translationOrder) {
		log(this.getClass(), "Converting file: " + translationOrder.getSourceDocument());
		File file = new File(translationOrder.getSourceDocument().reference().toString());
		String format = file.getName();
		format = format.substring(format.lastIndexOf(".") + 1);
		Locale locale = new Locale(translationOrder.getLangPair().getTo(), 
				translationOrder.getLangPair().getTo().toUpperCase());
		StringWriter sw = new StringWriter();
		try {
			converter = ConverterFactory
				.createConverter(formats.get(format), formats.get("xlf"));
			converter.convert(ConversionMode.TO_XLIFF, locale, null, 0,
					Charset.forName("utf-8"), formats.get(format), file.getName(), 
					file.getParent(), null, null, sw);
			translationOrder.setXliff(sw.toString());
		} catch (ConversionException e) {
			log(this.getClass(), e.getMessage(), Level.SEVERE);
		}
	}
	
	private void reConvertFile(TranslationOrder translationOrder) {
		log(this.getClass(), "ReConverting file: " + translationOrder.getSourceDocument());
		File file = new File(translationOrder.getSourceDocument().reference().toString());
		String format = file.getName();
		format = format.substring(format.lastIndexOf(".") + 1);
		Locale locale = new Locale(translationOrder.getLangPair().getFrom(), 
				translationOrder.getLangPair().getFrom().toUpperCase());
		try {
			converter = ConverterFactory
				.createConverter(formats.get("xlf"), formats.get(format));
			converter.convert(ConversionMode.FROM_XLIFF, locale, null, 0,
					Charset.forName("utf-8"), null, file.getName(), 
					file.getParent(), null, null, null);
		} catch (ConversionException e) {
			log(this.getClass(), e.getMessage(), Level.SEVERE);
		}
	}
	
}
