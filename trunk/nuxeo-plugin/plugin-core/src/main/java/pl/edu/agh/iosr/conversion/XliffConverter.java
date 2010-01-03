package pl.edu.agh.iosr.conversion;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.io.File;
import java.io.StringWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.lang.StringBuffer;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.CoreSession;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.util.IosrLogger.Level;
import pl.edu.agh.iosr.persistence.CoreSessionProxy;
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

    @In(create = true)
    private CoreSessionProxy coreSessionProxy;
	
    private CoreSession coreSession;

	
	private static Map<String, FileType> formats = new HashMap<String, FileType>();
	private String TMP_PATH;
	private Converter converter = null;

	public XliffConverter() {
		//super();
    }
    
    private void prepare() {
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
        TMP_PATH = getPath();
        log(this.getClass(), "TMP_PATH: " + TMP_PATH);
    }
	
	private String getPath() {
		try {
			String tmp = (new File(".")).getCanonicalPath();
			tmp = tmp.substring(0, tmp.lastIndexOf(File.separator) + 1);
			return tmp + "xliff";
		} catch (IOException e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
		return "";
	}
	
	@Create
	public void init() {
		super.init();
		
		coreSession = coreSessionProxy.getCoreSession();

		if (coreSession == null) {
			log(this.getClass(), "coreSession is null");
		}
		
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
	
	private void copyFileContent(String path, DocumentRef ref) throws IOException {
		try {
			int size = 128;
			byte[] buffer = new byte[size];
			int data;
			FileOutputStream fos = new FileOutputStream(path);
			DocumentModel dm = coreSession.getDocument(ref);
			Blob blob = (Blob) dm.getProperty("file", "content");
			InputStream fis = blob.getStream();
			while((data = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, data);
			}
			fis.close();
			fos.close();
			
		} catch(Exception e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
	}
	
	private void convertFile(TranslationOrder translationOrder) {
		prepare();
		log(this.getClass(), "Converting file: " + translationOrder.getSourceDocument().toString());
		String fileName = translationOrder.getSourceDocument().toString();
		fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		int i = fileName.lastIndexOf("-");
		fileName = fileName.substring(0, i) + '.' + fileName.substring(i + 1);
		log(this.getClass(), "file name: " + fileName);
		
		String filePath = TMP_PATH + File.separator + translationOrder.getRequestId().toString();
		log(this.getClass(), "New file location: " + filePath);
		File file = null;
		
		try {
			(new File(filePath)).mkdirs();
			log(this.getClass(), "Creating file: " + filePath + File.separator + fileName);
			file = new File(filePath + File.separator + fileName);
			file.createNewFile();
			log(this.getClass(), "Created file: " + file.getCanonicalPath());
			copyFileContent(filePath + File.separator + fileName, translationOrder.getSourceDocument());
		} catch (IOException e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
		String format = fileName.substring(fileName.lastIndexOf(".") + 1);
		Locale locale = new Locale(translationOrder.getLangPair().getFromLang(), 
				translationOrder.getLangPair().getFromLang().toUpperCase());
		StringWriter sw = new StringWriter();
		try {
			converter = ConverterFactory
				.createConverter(formats.get(format), formats.get("xlf"));
			//converter.convert(ConversionMode.TO_XLIFF, locale, null, 0,
			//		Charset.forName("utf-8"), formats.get(format), file.getName(), 
			//		file.getParent(), null, null, sw);
			converter.convert(ConversionMode.TO_XLIFF, locale, null, 0,
					Charset.forName("utf-8"), formats.get(format), fileName, 
					filePath, null, null, sw);
			log(this.getClass(), "Generated file: " + sw.toString());
			translationOrder.setXliff(sw.toString());
		} catch (ConversionException e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
	}
	
	private void reConvertFile(TranslationOrder translationOrder) {
		prepare();
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