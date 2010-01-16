package pl.edu.agh.xliffhandler.exporters;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Locale;

import pl.edu.agh.xliffhandler.converter.Converter;
import pl.edu.agh.xliffhandler.exceptions.ConversionException;
import pl.edu.agh.xliffhandler.notifier.Notifier;
import pl.edu.agh.xliffhandler.utils.ConversionMode;
import pl.edu.agh.xliffhandler.utils.ConversionStatus;
import pl.edu.agh.xliffhandler.utils.FileType;
import pl.edu.agh.xliffhandler.utils.SegmentBoundary;

public class PdfExporterToOOoText implements Converter {

	@Override
	public ConversionStatus convert(ConversionMode mode, Locale language,
			String phaseName, int maxPhase, Charset nativeEncoding,
			FileType nativeFileType, String nativeFileName, String baseDir,
			Notifier notifier, SegmentBoundary boundary,
			StringWriter generatedFileName) throws ConversionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getConversionProperty(String property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileType getFileType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConversionProperty(String property, Object value)
			throws ConversionException {
		// TODO Auto-generated method stub

	}

}
