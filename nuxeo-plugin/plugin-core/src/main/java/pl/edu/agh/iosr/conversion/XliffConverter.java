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

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.Contexts;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.CoreSession;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.DocumentRefWrapper;
import pl.edu.agh.iosr.controller.Mediator;
import pl.edu.agh.iosr.util.IosrLogger.Level;
import pl.edu.agh.iosr.persistence.CoreSessionProxy;
import pl.edu.agh.xliffhandler.converter.Converter;
import pl.edu.agh.xliffhandler.converter.ConverterFactory;
import pl.edu.agh.xliffhandler.exceptions.ConversionException;
import pl.edu.agh.xliffhandler.utils.ConversionMode;
import pl.edu.agh.xliffhandler.utils.FileType;

/**
 * Implementacja AsynchronousConvertera wykorzystująca bibliotekę pol.edu.agh.xliffhandler utworzoną na podstawie file2xliff4j
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
    
    private final Mediator mediator;

	private static final Map<String, FileType> formats = new HashMap<String, FileType>() {{
		put("html", FileType.HTML);
        put("properties", FileType.JAVA_PROPERTIES);
        put("doc", FileType.MSOFFICEDOC);
        put("odt", FileType.ODT);
        put("pdf", FileType.PDF);
        put("txt", FileType.PLAINTEXT);
        put("rtf", FileType.RTF);
        put("word", FileType.WORD);
        put("xlf", FileType.XLIFF);
        put("xml", FileType.XML);
	}};
	
	private String basePath;
	private Converter converter = null;
	private static final int BLOCK_SIZE = 128;

	/**
     * Konstruktor XliffConvertera 
     */
	public XliffConverter() {
		super();
		mediator = (Mediator) Contexts.getApplicationContext().get("mediator");
    }
	
	@Create
	public void init() {
	
		super.init();
		basePath = getPath();
		
		coreSession = coreSessionProxy.getCoreSession();

		if (coreSession == null) {
			log(this.getClass(), "coreSession is null");
		}
	}
	
	@Destroy
	public void shutdown() {
		super.shutdown();
	}
	
	/**
	 * Przeprowadza konwersję i rekonwersję o parametrach zadanych w <code>conversionTask</code>
	 * i sygnalizuje zakończenie zadania
	 *
	 * @param conversionTask zlecenie tłumaczenia
	 * */
	@Override
	public void proceed(ConversionTask conversionTask) {
		
		if(conversionTask.task == SupportedTasks.CONVERT) {
			convertFile(conversionTask.translationOrder);
			mediator.performExactTranslation(conversionTask.translationOrder);
		 } else {
			reConvertFile(conversionTask.translationOrder);
			mediator.completeTranslation(conversionTask.translationOrder);
		}
	}
	
	/**
	 * Główna metoda konwersji. <br>
	 * Rozpoznaje format pliku po jego rozszerzeniu, przeprowadza konwersję,
	 * tworzy tymczasowy folder, zawierający pliki pomocnicze konwersji.
	 *
	 * @param translationOrder zlecenie tłumaczenia
	 * */
	private void convertFile(TranslationOrder translationOrder) {

		DocumentRef doc = translationOrder.getSourceDocument().getDocumentModel().getRef();
		String fileName = doc.toString();
		
		StringWriter sw = new StringWriter();
		String format;
		
		log(this.getClass(), "Converting file: " + fileName);
		
		String filePath = basePath + File.separator + translationOrder.getRequestId().toString();
		Locale locale = new Locale(translationOrder.getLangPair().getFromLang(), 
									translationOrder.getLangPair().getFromLang().toUpperCase());
		
		fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		
		int lastDot = fileName.lastIndexOf(".");
		if (lastDot == -1) {		// in case of name's changing in nuxeo
			lastDot = fileName.lastIndexOf("-");
			format = fileName.substring(lastDot + 1);
		} else 
			format = fileName.substring(lastDot + 1);
		
		// Creating temporary file and getting content form nuxeo repo
		File file = null;
		try {
			(new File(filePath)).mkdirs();
			file = new File(filePath + File.separator + fileName);
			file.createNewFile();
			copyFileContent(file.getCanonicalPath(), doc);
		} catch (IOException e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
 
		// exact conversion
		try {
			converter = ConverterFactory
							.createConverter(formats.get(format), formats.get("xlf"));
			converter.convert(ConversionMode.TO_XLIFF, locale, null, 0,
							Charset.forName("utf-8"), formats.get(format), fileName, 
							filePath, null, null, sw);
							
			log(this.getClass(), "Generated file: " + sw.toString());
			translationOrder.setXliff(sw.toString());
		} catch (ConversionException e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
	}
	
	/**
	 * Główna metoda rekonwersji. <br>
	 * Rozpoznaje format pliku po jego rozszerzeniu, przeprowadza rekonwersję,
	 * tworzy wynikowy plik o żądanej nazwie w repozytorium Nuxeo i usuwa pomocnicze pliki.
	 * Utworzony plik znajduje się w tym samym folderze co plik bazowy.
	 *
	 * @param translationOrder zlecenie tłumaczenia
	 * */
	private void reConvertFile(TranslationOrder translationOrder) {
	
		DocumentRef doc = translationOrder.getSourceDocument().getDocumentModel().getRef();
		String fileName = doc.toString();
		
		StringWriter sw = new StringWriter();
		String format;
		
		String filePath = basePath + File.separator + translationOrder.getRequestId().toString();
		Locale locale = new Locale(translationOrder.getLangPair().getToLang(), 
								translationOrder.getLangPair().getToLang().toUpperCase());

		log(this.getClass(), "ReConverting file: " + fileName);
		
		String destPath =  fileName.substring(0, fileName.lastIndexOf("/"));	// path in nuxeo repo
		fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		
		int lastDot = fileName.lastIndexOf(".");
		if (lastDot == -1) {		// in case of name's changing in nuxeo
			lastDot = fileName.lastIndexOf("-");
			format = fileName.substring(lastDot + 1);
		} else 
			format = fileName.substring(lastDot + 1);
		
		// exact reconversion
		try {
			converter = ConverterFactory
						.createConverter(formats.get("xlf"), formats.get(format));
			converter.convert(ConversionMode.FROM_XLIFF, locale, null, 0,
							Charset.forName("utf-8"), null, fileName, 
							filePath, null, null, sw);
							
			log(this.getClass(), "Generated file: " + sw.toString());
			
			String destName = translationOrder.getDestinationDocumentName();
			if (destName == null) 		// default value
				destName = sw.toString().substring(sw.toString().lastIndexOf(File.separator) + 1);
			String destFile = destPath + File.separator + destName;
			
			// creating result file in nuxeo repo
			try {
				DocumentModel source = coreSession.getDocument(doc);
				String path = source.getPathAsString();
				path = path.substring(0, path.lastIndexOf("/"));
				
				DocumentRefWrapper drw = new DocumentRefWrapper();
				drw.setName(destName);
				drw.setPath(path);
				drw.setType(source.getType());
				translationOrder.setDestinationDocument(drw);
				
				createResultFile(path, source.getType(), destName, sw.toString(), format);
				deleteDirectory(new File(filePath));			//deleting temporary directory
			} catch(Exception ec) {
				ec.printStackTrace();
				log(this.getClass(), ec.getMessage(), Level.FATAL);
			}
			
		} catch(ConversionException e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
	}
	
	/**
	 * Pomocnicza funkcja zwracająca ścieżkę bezwzględną do katalogu,
	 * który będzie zawierał foldery z plikami tymczasowymi konwersji
	 *
	 * @return bazowa ścieżka bezwzględna dla tymczasowych folderów.
	 * */
	private String getPath() {
	
		try {
			String tmp = (new File(".")).getCanonicalPath();
			return tmp.substring(0, tmp.lastIndexOf(File.separator) + 1) + "xliff";
		} catch (IOException e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
		return "";
	}
	
	/**
	 * Pomocnicza funkcja pobierająca zawartość dokumentu z repozytorium Nuxeo
	 * i kopiująca go do wskazanego pliku.
	 *
	 * @param path Ścieżka do pliku, w którym zostanie zapisana skopiowana zawartość.
	 * @param ref Referencja do dokumentu w repozytorium Nuxeo, którego zawartość będzie kopiowana.
	 * */
	private void copyFileContent(String path, DocumentRef ref) throws IOException {
	
		try {
			int data;
			byte[] buffer = new byte[BLOCK_SIZE];
			
			FileOutputStream fos = new FileOutputStream(path);
			DocumentModel dm = coreSession.getDocument(ref);
			Blob blob = (Blob) dm.getProperty("file", "content");
			InputStream fis = blob.getStream();
			
			while((data = fis.read(buffer)) > 0) 
				fos.write(buffer, 0, data);
			
			fis.close();
			fos.close();	
		} catch(Exception e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
	}
	
	/**
	 * Pomocnicza funkcja tworząca wynikowy plik w repozytorium Nuxeo
	 *
	 * @param path Ścieżka do folderu w repozytorium Nuxeo.
	 * @param type Typ dokumentu, który zostanie utworzony.
	 * @param name Nazwa, pod którą dokument ma być widziany w Nuxeo.
	 * @param generatedFile Ścieżka do pliku utworzonego podczas rekonwersji, który zostanie skopiowany do Nuxeo.
	 * */
	private void createResultFile(String path, String type, String name, String generatedFile, String format) {
	
		try {
			DocumentModel dm = new DocumentModelImpl(path, name, type);	
			//FileBlob fb = new FileBlob(new File(generatedFile), format);
			FileBlob fb = new FileBlob(new File(generatedFile), format);
			fb.setFilename(name);
			dm.setProperty("file", "content", fb);
			dm.setProperty("file", "filename", name);
			//System.out.println("title: " + dm.getTitle());
			coreSession.createDocument(dm);
			coreSession.save();
			log(this.getClass(), "document created and saved.");
		} catch (Exception e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
	}
	
	/**
	 * Pomocnicza funkcja usuwająca tymczasowy folder z pomocniczymi plikami konwersji.
	 *
	 * @param dir Folder, który ma zostać skasowany wraz z zawartością.
	 * */
	private void deleteDirectory(File dir) {
		
		try {
			if (dir.exists() && dir.isDirectory()) {
				File[] files = dir.listFiles(); 
				for (int i = 0; i < files.length; ++i) { 
					if (files[i].isDirectory())
						deleteDirectory(files[i]);
					else {
						log(this.getClass(), "Deleting: " + files[i].getCanonicalPath());
						files[i].delete();
					}
				}
				dir.delete();
			}
		} catch(IOException e) {
			log(this.getClass(), e.getMessage(), Level.FATAL);
		}
	}
	
}
