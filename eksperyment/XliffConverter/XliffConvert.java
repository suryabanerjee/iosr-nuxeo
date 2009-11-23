package XliffConverter;

import file2xliff4j.ConverterFactory;
import file2xliff4j.Converter;
import file2xliff4j.ConversionException;
import file2xliff4j.HtmlHandler;
import file2xliff4j.XliffImporter;
import file2xliff4j.FileType;
import file2xliff4j.*;
import java.util.Locale;
import java.nio.charset.*;
import f2xutils.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.io.StringWriter;

public class XliffConvert {

    private static Map<String, FileType> formats = new HashMap<String, FileType>();
	private Converter converter = null;

	public XliffConvert() {

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

	private void convert(String sourceFormat, String destFormat, String sourceFile, Locale locale) {
			try {
					converter = ConverterFactory.createConverter(formats.get(sourceFormat), formats.get(destFormat));
			} catch(ConversionException e) {
				System.err.println("Error creating " + sourceFormat.toUpperCase() +
							   "-XLIFF converter: " + e.getMessage());
				System.exit(2);	
			}

			try {
				converter.convert(ConversionMode.TO_XLIFF, locale, null, 0, Charset.forName("utf-8"), formats.get(sourceFormat), sourceFile, ".", null, null, null);
			} catch(ConversionException e) {
				System.err.println("Error converting file " + sourceFile + " " + e.getMessage());
				System.exit(2);
			}
	}

	private StringWriter deconvert(String destFormat, String sourceFile, Locale locale) {
		try {
			converter = ConverterFactory.createConverter(formats.get("xlf"), formats.get(destFormat));
		} catch(ConversionException e) {
				System.err.println("Error creating XLIFF-" + destFormat.toUpperCase() +
							   " converter: " + e.getMessage());
				System.exit(2);	
		}

		StringWriter sw = null;
		try {
			sw = new StringWriter();
			converter.convert(ConversionMode.FROM_XLIFF, locale, null, 0, Charset.forName("utf-8"), null, sourceFile, ".", null, null, sw);
		} catch(ConversionException e) {
				System.err.println("Error converting file " + sourceFile + " " + e.getMessage());
				System.exit(2);
		}
		return sw;
	}

	public static void main(String[] args){
		
			if(args.length != 3) {
				System.out.println("USAGE: " + "\n" + "<source_file> <action> <locale>");
				System.out.println("ACTION: 1 - convert, 2 - deconvert");
				System.exit(1);
			}
			if(args[1].equals("1")) {
				System.out.println("Converting " + args[0] + " file in " + args[2] + "...");
				Locale locale = new Locale(args[2], args[2].toUpperCase());
				XliffConvert xliffConvert = new XliffConvert();
				String format = args[0].substring(args[0].lastIndexOf(".") + 1);
				xliffConvert.convert(format, "xlf", args[0], locale);		
				System.out.println("Generated file: " + args[0] + ".xliff");
			} else if(args[1].equals("2")){
				System.out.println("DeConverting " + args[0] + ".xliff to " + args[0] + " in " + args[2] + "...");
				Locale locale = new Locale(args[2], args[2].toUpperCase());
				XliffConvert xliffConvert = new XliffConvert();
				String format = args[0].substring(args[0].lastIndexOf(".") + 1);
				StringWriter sw = xliffConvert.deconvert(format, args[0], locale);
				System.out.println("Generated file: " + sw + " with " + locale + " locale.");
			}
	}
}
