package XliffConverter;

import file2xliff4j.*;
import java.util.Map;
import java.util.HashMap;

class Constants {

	public static Map<String, FileType> formats = new HashMap<String, FileType>();

	public Constants() {
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
};
