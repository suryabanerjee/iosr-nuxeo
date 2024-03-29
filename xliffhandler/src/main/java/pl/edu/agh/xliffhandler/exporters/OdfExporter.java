/*
 * OdfExporter.java
 *
 * Copyright (C) 2006. Lingotek, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA
 */
 
package pl.edu.agh.xliffhandler.exporters;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
import pl.edu.agh.xliffhandler.converter.Converter;
import pl.edu.agh.xliffhandler.exceptions.ConversionException;
import pl.edu.agh.xliffhandler.notifier.Notifier;
import pl.edu.agh.xliffhandler.tu.TuPreener;
import pl.edu.agh.xliffhandler.tu.TuStrings;
import pl.edu.agh.xliffhandler.utils.ConversionMode;
import pl.edu.agh.xliffhandler.utils.ConversionStatus;
import pl.edu.agh.xliffhandler.utils.FileType;
import pl.edu.agh.xliffhandler.utils.Format;
import pl.edu.agh.xliffhandler.utils.SegmentBoundary;
 
 /**
 * Class to export XLIFF to an OpenDocumentFormat document. If the ODF document
 * is an "intermediate" format, also convert it the "rest of the way" to the
 * ultimate document in a format such as Microsoft Word or similar formats.
 * @author Maria Szymczak, Weldon Whipple &lt;weldon@lingotek.com&gt;
 */
public abstract class OdfExporter implements Converter {

	/** The value assigned to the file element's "original" attribute in
     * the XLIFF. (The most distant descendant of the OdfExporter class assigns
     * a value to this variable, which is used to construct the names of the
     * xliff, skeleton, format and other intermediate files.)
     */
    String xliffOriginalFileName = ""; 
    
    private final String SUFFIX = ".transl";
    
    /** The following maps TU identifiers to target strings: */
    private TuStrings tuMap = new TuStrings();
    private Format format;             // Maps bx/ex etc. to original format characters.
    private BufferedWriter outWriter;  // To write exports to.
    
    // Matcher for the placeholders in the skeleton.
    Matcher expandMatcher = Pattern.compile("<lt:(tu|format) (id=|ids=)(['\"])(.+?)\\3/>", 
            Pattern.DOTALL).matcher("");
    
    // Matcher for other miscellaneous junk.
    Matcher miscMatcher = Pattern.compile("&lt;/?mrk\\s[^>]*&gt;", Pattern.DOTALL).matcher("");
    
    /** Creates a new instance of OdfExporter */
    public OdfExporter() { }
    
    /** 
     * Convert one set of targets (in the translation units of an XLIFF file) back
     * to the original format used by the content.xml component of an 
     * OpenOffice.org OpenDocument Format odt file. Use (besides the XLIFF file) 
     * the skeleton and format files that were generated when the XLIFF file was 
     * created.
     * <p>Note: This conversion likely constitutes only one stage of the total
     * conversion of a set of XLIFF targets back to the final destination format.
     * (Example: An heir of this converter might be using the OdfExporter to convert
     * to an intermediate format that will be further converted to a final Word, 
     * WordPerfect or Excel [say] document by the heir class.)
     * @param mode The mode of conversion (to or from XLIFF).
     * @param language The language of the XLIFF targets to use in constructing
     *        the ODF document. The language is used in constructing a unique
     *        name for the output file. For example, if ja_JP is specified,
     *        the output file name is content.ja_jp.xml.
     * @param phaseName The name of the phase to export. If this parameter's
     *        value is not null, it is matched against the value of the 
     *        optional phase-name attribute of the target elements of the
     *        XLIFF document. If null, no check is made against a phase-name
     *        attribute.
     *        <p>If the phase name string consists entirely of numeric digit(s) 
     *        equivalent to an integer with value greater than 1 but less than 
     *        or equal to maxPhase (see next parameter) search for targets with 
     *        lower numbered phase names.
     * @param maxPhase The maximum phase number. If phaseName is specified as 
     *        "0" and maxPhase is a non-negative integer, search for the highest 
     *        "numbered" phase, starting at maxPhase, and searching down to phase
     *        "1".
     * @param nativeEncoding The encoding of the native document. This parameter
     *        is ignored for OpenDocument Format, since the content.xml file 
     *        will always be encoded in UTF-8.
     * @param nativeFileType This parameter is ignored. (The content.xml-format
     *        file that is generated by this exporter must be further processed
     *        before reaching the final-destination format specified when the 
     *        XLIFF was created and stored in the XLIFF. For example, to become
     *        an OpenOffice.org odt document, the content.xml file must be
     *        placed in the ZIP-formatted odt file. To become a Word document,
     *        OpenOffice.org can be used to convert the odt file to a doc file.)
     * @param nativeFileName This parameter is ignored. (Although other
     *        XLIFF-to-native format converters use the nativeFileName as the
     *        prefix to the names for the xliff, skeleton and format files,
     *        this converter will always use content.xml.xliff, content.xml.skeleton
     *        and content.xml.format as the names of those respective files. Also,
     *        the output file will be named content.&lt;language&gt;.xml, where
     *        &lt;language&gt; corresponds to the language code specified in the
     *        earlier language parameter. The reason for ignoring thie parameter
     *        is because the OdfExporter will *always* be used by an heir class
     *        to complete the export of the final file.)
     * @param baseDir The directory (in the file system) from which input files
     *        (XLIFF, skeleton and format files) will be read, and to which the 
     *        output file will be written.
     * @param notifier Instance of a class that implements the Notifier
     *        interface (to send notifications in case of conversion error).
     * @param boundary (Ignored. The boundary on which to segment translation 
     *        units (e.g., on paragraph or sentence boundaries) is meaningful
     *        only for importers--converters that generate XLIFF from documents.)
     * @param generatedFileName If non-null, the converter will write the name
     *        of the file (without parent directories) to which the generated
     *        output file was written.
     * @return Indicator of the status of the conversion.
     * @throws file2xliff4j.ConversionException
     *         If a conversion exception is encountered.
     */
    public ConversionStatus convert(ConversionMode mode,
            Locale language,
            String phaseName,
            int maxPhase,
            Charset nativeEncoding,
            FileType nativeFileType,
            String nativeFileName,
            String baseDir,
            Notifier notifier,
            SegmentBoundary boundary,
            StringWriter generatedFileName) throws ConversionException {
            
            // Verify input arguments
        if ((language == null) || (nativeEncoding == null)
            || (nativeFileName == null)
                || (nativeFileName.length() == 0)
                || (baseDir == null)
                || (baseDir.length() == 0)
                || (! mode.equals(ConversionMode.FROM_XLIFF))) {
            throw new ConversionException("Required parameter(s)"
                    + " omitted, incomplete or incorrect.");
        }
        
        String inXliff = baseDir + File.separator + xliffOriginalFileName 
                + Converter.xliffSuffix  + SUFFIX;
        String inSkeleton = baseDir + File.separator + xliffOriginalFileName
                + Converter.skeletonSuffix;
        String inFormat = baseDir + File.separator + xliffOriginalFileName
                + Converter.formatSuffix;
        
        String outOdf = baseDir + File.separator + "content."
                + language.toString() + ".xml";
        if (generatedFileName != null) {
            // Tell caller the name of the output file (wo/directories)
            generatedFileName.write("content." + language.toString() + ".xml");
        }

        // This next file is for the translated styles.xml file.
        String outStyles = baseDir + File.separator + "styles."
                + language.toString() + ".xml";
        
        // We created an empty map of TU strings when this class was loaded.
        // Make sure that actually happened.
        if (tuMap == null) {
            throw new ConversionException("Unable to get  target strings"
                + " from file " + inXliff);
        }
        
        // Now load that empty map with the target strings for the language
        // we are exporting.
        tuMap.loadStrings(inXliff, language, phaseName, maxPhase, true);
        
        // Before trying to export, check if notifier is non-null. If it is,
        // check to see if the skeleton is well-formed XML. If it isn't,
        // call the notifier. 
        boolean skelOK = true;
        if (notifier != null) {
            String notice = "";
            File skelFile = new File(inSkeleton);
            // Does the skeleton even exist?
            if (!skelFile.exists()) {
                skelOK = false;
                notice = "Document exporter cannot find a skeleton file named "
                        + inSkeleton;
                System.err.println(notice);
                notifier.sendNotification("0001", "OdfExporter", Notifier.ERROR, notice);
            }
            // Is it well-formed?
            else {
                Charset charset = Charset.defaultCharset();
                
                try {
                    XMLReader parser = XMLReaderFactory.createXMLReader();

                    // We don't care about namespaces at the moment.
                    parser.setFeature("http://xml.org/sax/features/namespaces", false);

                    Reader inReader = new InputStreamReader(new FileInputStream(skelFile), charset);
                    InputSource skelIn = new InputSource(inReader);
                    if (skelIn != null) {
                        parser.parse(skelIn); 
                        inReader.close();
                    }
                    else {
                        skelOK = false;
                        notice = "Unable to read skeleton file " 
                                + inSkeleton;
                        System.err.println(notice);
                        notifier.sendNotification("0002", "OdfExporter", Notifier.ERROR, notice);
                    }
                }
                catch(SAXParseException e) {
                    skelOK = false;
                    notice = "Skeleton file " + inSkeleton
                            + " is not well-formed at line "
                            + e.getLineNumber() + ", column " + e.getColumnNumber()
                            + "\n" + e.getMessage() + "\n" + this.getStackTrace(e);
                    System.err.println(notice);
                    notifier.sendNotification("0003", "OdfExporter", Notifier.ERROR, notice);
                }
                catch(SAXException e) {
                    skelOK = false;
                    notice = "Skeleton file " + inSkeleton
                            + " caused an XML parser error: " + e.getMessage()
                            + "\n" + this.getStackTrace(e);
                    System.err.println(notice);
                    notifier.sendNotification("0004", "OdfExporter", Notifier.ERROR, notice);
                }
                catch(IOException e) {
                    skelOK = false;
                    notice = "The validator of skeleton file " + inSkeleton
                            + " experienced an I/O error while reading input: " + e.getMessage()
                            + "\n" + this.getStackTrace(e);
                    System.err.println(notice);
                    notifier.sendNotification("0005", "OdfExporter", Notifier.ERROR, notice);
                }
            }
        }
        
        // If the skeleton isn't OK, there is no point proceeding. The export 
        // will fail.
        if (! skelOK) {
            throw new ConversionException("Problems encountered reading the "
                    + "skeleton file. Support has been notified.");
        }
        
        //////////////////////////////////////////////////////////////////
        // Get readers/writers on the in/out files and necessary objects
        //////////////////////////////////////////////////////////////////

        // Skeleton (UTF-8, of course!)
        BufferedReader inSkel = null;
         try {
            inSkel = new BufferedReader(new InputStreamReader(new FileInputStream(inSkeleton),
                    "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {  // What!!?? Can't read UTF-8?
            System.err.println("Cannot decode UTF-8 skeleton file: "
                    + e.getMessage());
            throw new ConversionException("Cannot decode UTF-8 skeleton file: "
                    + e.getMessage());
        }
        catch (FileNotFoundException e) {
            System.err.println("Cannot find the skeleton file: ");
            System.err.println(e.getMessage());
            throw new ConversionException("Cannot find the skeleton file: "
                    + e.getMessage());
        }
        
        // Format (to resolve bx/ex tags (etc.))
        try {
            format = new Format(inFormat);
        }
        catch (IOException e) {
            System.err.println("Cannot access the format file.");
            System.err.println(e.getMessage());
        }

        // Odf output file (content.<language>.xml)
        try {
            outWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outOdf), "UTF-8"));
        }
        catch(FileNotFoundException e ) {
            System.err.println("Cannot write to the ODF file: " + e.getMessage());
            throw new ConversionException("Cannot write to the ODF file: "
                    + e.getMessage());
        }
        catch(UnsupportedEncodingException e) {  // Way bogus!! (this is UTF-8!)
            System.err.println("Cannot write UTF-8 to the ODF file: "
                    + e.getMessage());
            throw new ConversionException("Cannot write UTF-8 to the ODF file: "
                    + e.getMessage());
        }
        
        // WWhipple 1/2/2007: Add support for headers/footers (etc.) in the
        // styles.xml file (in the same odt [etc.] file as content.xml)
        boolean newFormat = false;      
        boolean inContentXml = false;
        boolean inStylesXml = false;
        /*******************************
         * E X P O R T   T H E   O D F *
         *******************************/
        boolean expandedLine = false;  // We don't need to empty the expansion buf yet
        
        
        try {
            for (;;) {
                String skelLine = inSkel.readLine();
                if (skelLine == null) {
                    break;
                }
            
                if (skelLine.startsWith("<lt:skeleton xmlns:lt")) {
                    newFormat = true;
                    continue;
                }
                else if (skelLine.startsWith("<lt:content_xml><![CDATA[")) {
                    inContentXml = true;    // Starting with the next line
                    continue;
                }
                else if (skelLine.equals("]]></lt:content_xml>")) {
                    // We're at the end of content.xml
                    inContentXml = false;
                    outWriter.close();    // Close the content.ll_LL.xml file
                    
                    continue;
                }
                else if (skelLine.startsWith("<lt:styles_xml><![CDATA[")) {
                    inStylesXml = true;    // Starting with the next line
                    
                    // We need a styles file to write to.
                    try {
                        outWriter = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(outStyles), "UTF-8"));
                    }
                    catch(FileNotFoundException e ) {
                        System.err.println("Cannot write to the styles file: " + e.getMessage());
                        throw new ConversionException("Cannot write to the styles file: "
                                + e.getMessage());
                    }
                    catch(UnsupportedEncodingException e) {  // Way bogus!! (this is UTF-8!)
                        System.err.println("Cannot write UTF-8 to the styles file: "
                                + e.getMessage());
                        throw new ConversionException("Cannot write UTF-8 to the styles file: "
                                + e.getMessage());
                    }

                    continue;
                }
                else if (skelLine.equals("]]></lt:styles_xml>")) {
                    // We're at the end of styles.xml
                    inStylesXml = false;
                    outWriter.close();
                    continue;
                }
                else if (skelLine.equals("</lt:skeleton>")) {
                    // We're done!
                    break;
                }
                // If line has tu place-holder, substitute the TU in its place
                if ((skelLine.indexOf("<lt:tu id") != -1)
                    || (skelLine.indexOf("<lt:format id") != -1)) {
                    expandTus(skelLine);
                    expandedLine = true;
                }
            
                // Then write the line to the output stream;
                // We need to be careful not to write the xml declaration line
                // twice at the top of the content.ll_LL.xml file
                if (skelLine.equals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                    && inContentXml) {
                    continue;     // This will duplicate the previous line.
                } 
                
                if (!expandedLine) { 
                    outWriter.write(skelLine + "\n");
                }
                expandedLine = false;
                outWriter.flush();     // For debugging
            }
            
            if (!newFormat) {
                // Flush and close before leaving
                outWriter.flush();

                outWriter.close();
            }
        }
        catch (IOException e) {
            System.err.println("Cannot read skeleton file");
            throw new ConversionException("Cannot read skeleton file "
                    + inSkeleton );
        }

        // Before returning, make sure that the generated output file
        // (content.<lang>.xml) is valid XML. If not, throw an exception. 
        boolean contentOK = true;
        Charset charset = Charset.defaultCharset();
        
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();

            // We don't care about namespaces at the moment.
            parser.setFeature("http://xml.org/sax/features/namespaces", false);

            Reader inReader = new InputStreamReader(new FileInputStream(outOdf), charset);
            InputSource contentIn = new InputSource(inReader);
            if (contentIn != null) {
                parser.parse(contentIn); 
                inReader.close();
            }
            else {
                contentOK = false;
                if (notifier != null) {
                    String notice = "Unable to read generated content.xml file " 
                            + outOdf;
                    System.err.println(notice);
                    notifier.sendNotification("0011", "OdfExporter", Notifier.ERROR, notice);
                }
            }
        }
        catch(SAXParseException e) {
            contentOK = false;
            if (notifier != null) {
                String notice = "Generated file " + outOdf
                        + " is not well-formed at line "
                        + e.getLineNumber() + ", column " + e.getColumnNumber()
                        + "\n" + e.getMessage() + "\n" + this.getStackTrace(e);
                System.err.println(notice);
                notifier.sendNotification("0012", "OdfExporter", Notifier.ERROR, notice);
            }
        }
        
        catch(SAXException e) {
            contentOK = false;
            if (notifier != null) {
                String notice = "Generated file " + outOdf
                        + " caused an XML parser error: " + e.getMessage()
                        + "\n" + this.getStackTrace(e);
                System.err.println(notice);
                notifier.sendNotification("0013", "OdfExporter", Notifier.ERROR, notice);
            }
        }
        catch(IOException e) {
            contentOK = false;
            if (notifier != null) {
                String notice = "The validator of generated file " + outOdf
                        + " experienced an I/O error while reading input: " + e.getMessage()
                        + "\n" + this.getStackTrace(e);
                System.err.println(notice);
                notifier.sendNotification("0014", "OdfExporter", Notifier.ERROR, notice);
            }
        }

        // If the generated content.xml file isn't OK, there is no point proceeding. 
        // The export will fail.
        if (! contentOK) {
            String message = "The export process generated an invalid content.xml file " 
                + outOdf + ".";
            if (notifier != null) {
                message += " Support has been notified.";
            }
            throw new ConversionException(message);
        }
        
        // If we're using a new-format skeleton file, validate the styles:
        if (newFormat) {
            boolean stylesOK = true;

            try {
                XMLReader parser = XMLReaderFactory.createXMLReader();

                // We don't care about namespaces at the moment.
                parser.setFeature("http://xml.org/sax/features/namespaces", false);

                Reader inReader = new InputStreamReader(new FileInputStream(outStyles), charset);
                InputSource contentIn = new InputSource(inReader);
                if (contentIn != null) {
                    parser.parse(contentIn); 
                    inReader.close();
                }
                else {
                    stylesOK = false;
                    if (notifier != null) {
                        String notice = "Unable to read generated styles.xml file " 
                                + outStyles;
                        System.err.println(notice);
                        notifier.sendNotification("0011", "OdfExporter", Notifier.ERROR, notice);
                    }
                }
            }
            catch(SAXParseException e) {
                stylesOK = false;
                if (notifier != null) {
                    String notice = "Generated file " + outStyles
                            + " is not well-formed at line "
                            + e.getLineNumber() + ", column " + e.getColumnNumber()
                            + "\n" + e.getMessage() + "\n" + this.getStackTrace(e);
                    System.err.println(notice);
                    notifier.sendNotification("0012", "OdfExporter", Notifier.ERROR, notice);
                }
            }
            catch(SAXException e) {
                stylesOK = false;
                if (notifier != null) {
                    String notice = "Generated file " + outStyles
                            + " caused an XML parser error: " + e.getMessage()
                            + "\n" + this.getStackTrace(e);
                    System.err.println(notice);
                    notifier.sendNotification("0013", "OdfExporter", Notifier.ERROR, notice);
                }
            }
            catch(IOException e) {
                stylesOK = false;
                if (notifier != null) {
                    String notice = "The validator of generated file " + outStyles
                            + " experienced an I/O error while reading input: " + e.getMessage()
                            + "\n" + this.getStackTrace(e);
                    System.err.println(notice);
                    notifier.sendNotification("0014", "OdfExporter", Notifier.ERROR, notice);
                }
            }

            // If the generated content.xml file isn't OK, there is no point proceeding. 
            // The export will fail.
            if (! stylesOK) {
                String message = "The export process generated an invalid styles.xml file " 
                    + outStyles + ".";
                if (notifier != null) {
                    message += " Support has been notified. Using original styles.xml file";
                }
                // throw new ConversionException(message);
                ///////////////////////////////////////////////////////////////
                // Rather than bailing, let's remove the (empty or bogus)
                // styles.ll_cc.xml file
                File deleteStyleSkel = new File(outStyles);
                if (deleteStyleSkel.exists()) {
                    deleteStyleSkel.delete();
                }
            }
        }
        
        return ConversionStatus.CONVERSION_SUCCEEDED;
    }
    
    /**
     * Passed a line from the skeleton file, expand all &lt;lt:tu id='?'/> tags
     * by replacing with them with the actual translation unit (in the appropriate
     * language. Return the expanded string.
     * @param skelLine Represents a line to be expanded.
     */
    private void expandTus(String skelLine) {
    
		expandMatcher.reset(skelLine);    // Tell regex matcher about this line
        // Find each TU placeholder and replace it with TUs
        // The next variables hold the start and end of the subsequence of non
        // placeholder characters just before the current placeholder.
        int copyFrom = 0;   // Where in the main string to copy non-placeholder chars from.
                            //   (i.e., where the previous placeholder ended) 
        int copyTo = 0;     // Where the current placeholder starts.
        int nextCopyFrom;   // Where the current placeholder ends. (We will copy
                            //   from here next time.)
        while (expandMatcher.find()) {
            copyTo = expandMatcher.start();     // Where this placeholder starts
            nextCopyFrom = expandMatcher.end(); // Where this placeholder ends (+1)
            String placeHolder = expandMatcher.group(0);
            String tuOrFormat = expandMatcher.group(1);
            boolean isSingleton = expandMatcher.group(2).equals("ids=") ? false : true;
            String tuID = expandMatcher.group(4);     // Get the identifier(s) from the tag in skel
            
            String text = "";
            if (tuOrFormat.equals("tu")) {
                if (isSingleton) {
                    text = tuMap.getTu(tuID, null, false, false);  // Get the TU target text
                }
                else { // Multiple TU (segment) references
                    Matcher mMult = Pattern.compile("^\\s*(tu|format):([-\\w]+)(.*)$").matcher("");
                    String tail = tuID; // All the TU/format IDs
                    boolean done = false;
                    while (! done) {
                        mMult.reset(tail);
                        if (mMult.find()) {
                            String tOrF = mMult.group(1);
                            String id = mMult.group(2);
                            tail = mMult.group(3);
                            if (tOrF.equals("tu")) {
                                text += tuMap.getTu(id, null, false, false);
                            }
                            else {  // Text from the format file
                                text += format.getReplacement(id);
                            }
                        }
                        else {
                            done = true;
                        }
                    }
                }

                text = TuPreener.removeCoreMarks(text);
                // Validate entire TU (sequence)
                text = TuPreener.validateAndRepairTu(text);
            }
            else { // Read the format file
                text = format.getReplacement(tuID);
            }
            // Expand bx, ex, x, ...
            text = resolveFormatCodes(text);
            text = TuPreener.removeMergerMarks(TuPreener.removeCoreMarks(text));
            text = text.replace("&lt;lt:core&gt;", "");
            text = text.replace("&lt;/lt:core&gt;", "");

            // Just in case ...:
            text = miscMatcher.reset(text).replaceAll("");

            // Copy the text between the previous placeholder and this one.
            try {
                outWriter.write(skelLine, copyFrom, copyTo-copyFrom);
            
                // Then copy what the placeholder expanded to.
                outWriter.write(text);
            }
            catch(IOException e) {
                System.err.println("Caught exception writing expansion.");
            }
            
            // Then get where (in skelLine) this placeholder ended, so we can
            // copy from that place next time.
            copyFrom = nextCopyFrom;
        }
        
        // We're done; append the text after the last placeholder in the skeleton
        try {
            outWriter.write(skelLine, copyFrom, skelLine.length()-copyFrom);
            outWriter.write("\n");
        }
        catch(IOException e) {
            System.err.println("Caught exception writing expansion.");
        }
    }
    
    /** 
     * Passed a translation unit, look for bx/ex tags (beginning and ending)
     * and replace them with their original strings.
     * (Note: As we expand our XLIFF, we will need to look for <x> codes
     * as well).
     * @param tuText The text of the Translation Unit that needs to have
     *               its format codes resolved
     * @return The expanded TU, with bx/ex codes replaced by their equivalents
     */
    private String resolveFormatCodes(String tuText) {
        if (this.format == null) {
            // Without a format class, we can't resolve the codes
            return tuText;                     // Just return
        }

        String newStr = tuText;
        boolean moreCodes = true;              // Be optimistic
        
        Matcher fmtMatcher = 
            Pattern.compile("<[be]?x\\b.*?\\bid=['\"]([^'\"]+)['\"].*?>").matcher("");
        
        // This matcher extracts the value of the xid attribute from a tag (if present)
        Matcher xidMatcher = 
            Pattern.compile("<[^/>]*?\\bxid=(['\"])([^'\"]+)\\1").matcher("");
        
        while (moreCodes) {
            // Find each format code and extract its id
            fmtMatcher.reset(newStr);
            if (fmtMatcher.find()) {
                String formatID = fmtMatcher.group(1);  // group 1 is 1st set of parens
                String wholeTag = fmtMatcher.group(0);  // group 0 matches entire regex
                String xID = "";                        // Possible reference to another TU
                
                // Check for an xid in the tag.
                if (wholeTag.contains("xid=")) {
                    xidMatcher.reset(wholeTag);
                    if (xidMatcher.find()) {
                        xID = xidMatcher.group(2);
                    }
                }
                
                if ((xID != null) && (xID.length() > 0)) { // A footnote, perhaps?
                    // 10/24/2006 WLW: This might be an x tag that expands to
                    // additional XLIFF tags. Try that first.
                    String formatText = format.getReplacement(formatID);
                    if (formatText.length() > 0) {  // If successful
                        // Does this format string have further xliff tags?
                        if (formatText.matches(".*<[be]?x [^>]*>.*")) {
                            // I hope this works!
                            formatText = resolveFormatCodes(formatText);
                        }
                        newStr = newStr.replace(wholeTag, formatText);
                    }
                    
                    else { // No replacement in format file; go for the
                           // text of the TU referenced by the xid attribute.
                        String subTuText = tuMap.getTu(xID, "", true);

                        // Remove lt:core tags from the sub TU text
                        subTuText = TuPreener.removeCoreMarks(subTuText);

                        subTuText = resolveFormatCodes(subTuText);
                        newStr = newStr.replace(wholeTag,subTuText);
                    }
                }
                else {
                    // Just get the replacement text for the bx/ex/x
                    String formatText = format.getReplacement(formatID);
                    if (formatText.length() > 0) {  // If successful
                        // Does this format string have further xliff tags?
                        if (formatText.matches(".*<[be]?x [^>]*>.*")) {
                            // I hope this works!
                            formatText = resolveFormatCodes(formatText);
                        }
                        newStr = newStr.replace(wholeTag, formatText);
                    }
                    else {
                        // Leave the format code unexpanded (not good!)
                    }
                }
            }
            else {
                // We're done--no more format codes
                moreCodes = false;
            }
        }
        
        return newStr;       // The TU with all format codes expanded.
    }
    
    /**
     * Convert a stack trace to a string.
     * @param t Throwable whose stack trace will be returned
     * @return String that contains the strack trace.
     */
    private String getStackTrace(Throwable t) {
        if (t == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);      // Print the stack trace
        pw.flush();
        sw.flush(); 
        return sw.toString();
    } 

}