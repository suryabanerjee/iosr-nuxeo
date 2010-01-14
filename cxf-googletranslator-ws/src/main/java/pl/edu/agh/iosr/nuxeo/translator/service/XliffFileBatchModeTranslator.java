package pl.edu.agh.iosr.nuxeo.translator.service;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;


public class XliffFileBatchModeTranslator {

	public static final int CHUNK_SIZE_LIMIT=5000;			//renenber about google translator string length limits	
	public static final String SEGMENTS_DELIMITER=". %%%";  //be careful when choosing delmiter, Google translator removes some blank spaces in returned string a
	public static final Pattern SEGMENTS_DELIMITER_PATTERN = Pattern.compile(SEGMENTS_DELIMITER);
	public static final Pattern END_FRAGMENT_PATTERN = Pattern.compile(".");
	
	private Map<String, String> sourceText;
	private Map<String,String> translatedText=new HashMap<String, String>();
	private Map<String,String> chunkSegments=new HashMap<String,String>();
	private XliffParser xliffParser;
	int chunkCurrentSize;
	boolean chunkFull;
	String restOfSplittedSegment;
	
	
	void init(File sourceFile) throws IOException, ParserConfigurationException, SAXException{
	
		chunkCurrentSize=0;
		chunkFull=false;
		restOfSplittedSegment=null;
		xliffParser=new XliffParser(sourceFile);
		sourceText=xliffParser.getSourceText();
		Translate.setHttpReferrer("localhost");	
		
	}
	
	public File translateFile(File sourceFile,String sourceLanguage,String destinationLanguage) throws IOException,		
		ParserConfigurationException, SAXException, Exception,
		TransformerException, TransformerConfigurationException {
		
		init(sourceFile);
		
		for(Map.Entry<String,String> unit:sourceText.entrySet()){
				
				
				int newSizeWithEntireSegment=chunkCurrentSize+unit.getValue().length()+SEGMENTS_DELIMITER.length();
				if(newSizeWithEntireSegment<CHUNK_SIZE_LIMIT){	//there is still place for entire segment
						addEntireSegment(unit);
				}
				else if(newSizeWithEntireSegment>CHUNK_SIZE_LIMIT && chunkCurrentSize+SEGMENTS_DELIMITER.length()<CHUNK_SIZE_LIMIT){
						addPartOfSegment(unit);
				}
				if(chunkFull){	
					translateAndResetChunk(sourceLanguage,destinationLanguage);
					if(restOfSplittedSegment!=null){
						handleRestOfSplittedSegment(unit,sourceLanguage,destinationLanguage);
						restOfSplittedSegment=null;
					}
						
				}
				
				System.out.println("next");
		}
		translateAndResetChunk(sourceLanguage,destinationLanguage);
		return xliffParser.createXliffWithTranslation(translatedText, destinationLanguage+"_"+destinationLanguage.toUpperCase(),"result.sliff");
			
	}

	private void translateAndResetChunk(String sourceLanguage,String destinationLanguage) throws Exception {
		System.out.println("SEGMENT IS FULL");
		Map<String,String> chunkTranslation=translateChunk(chunkSegments,sourceLanguage,destinationLanguage);
		String existing=null;String translation=null;
		for(Map.Entry<String, String> entry:chunkTranslation.entrySet()){
			if((existing=translatedText.get(entry.getKey()))!=null){
						System.out.println("APPEND TRANSLATION");
				translation=existing+entry.getValue();
			}
			else
				translation=entry.getValue();
			translatedText.put(entry.getKey(), translation);
		}
		chunkFull=false;
		chunkCurrentSize=0;	
		chunkSegments.clear();
	}

	private void handleRestOfSplittedSegment(Map.Entry<String, String> unit,String sourceLanguage,String destinationLanguage)
			throws Exception {
		System.out.println("REST!LEN="+restOfSplittedSegment.length());

		while(restOfSplittedSegment.length()>CHUNK_SIZE_LIMIT){
			System.out.println("TOO BIG!");
					
			int limit=findSplitIndex(restOfSplittedSegment,CHUNK_SIZE_LIMIT);
			String chunk=restOfSplittedSegment.substring(0,limit);
			String translated=Translate.execute(chunk, Language.fromString(sourceLanguage),Language.fromString(destinationLanguage));	
			restOfSplittedSegment=restOfSplittedSegment.substring(limit);
			String existing2=translatedText.get(unit.getKey());
			translatedText.put(unit.getKey(), existing2+translated);
			
		}
		if(restOfSplittedSegment.length()>0){
			System.out.println("YEAhLEN="+restOfSplittedSegment.length());
				
			chunkSegments.put(unit.getKey(),restOfSplittedSegment);
			chunkCurrentSize=restOfSplittedSegment.length();
			
		}
	}

	private void addPartOfSegment(Map.Entry<String, String> unit) {
		System.out.println("PART OF SEGMENT ADDED");
		int remainingSpace=CHUNK_SIZE_LIMIT-chunkCurrentSize-SEGMENTS_DELIMITER.length();		//segment must be splitted
		int limit=findSplitIndex(unit.getValue(),remainingSpace);
		System.out.println("LIMIT IS"+limit);
		chunkSegments.put(unit.getKey(),unit.getValue().substring(0,limit));
		chunkCurrentSize+=unit.getValue().substring(0,limit).length()+SEGMENTS_DELIMITER.length();
		System.out.println("TOTAL SIZE IS"+chunkCurrentSize);
		restOfSplittedSegment=unit.getValue().substring(limit);
		chunkFull=true;
	}

	private void addEntireSegment(Map.Entry<String, String> unit) {
		System.out.println("FULL SEGMENT ADDED");
		chunkSegments.put(unit.getKey(),unit.getValue());
		chunkCurrentSize+=unit.getValue().length()+SEGMENTS_DELIMITER.length();
		if(chunkCurrentSize+SEGMENTS_DELIMITER.length()>=CHUNK_SIZE_LIMIT)
		chunkFull=true;
	}
	
	private static Map<String,String> translateChunk(Map<String, String> chunkSegments,String sourceLanguage,String destinationLanguage) throws Exception {
		
		Map<String,String> resultMap=new HashMap<String, String>();
		StringBuilder sb=new StringBuilder();
		List<String> idsInChunk=new ArrayList<String>();
		for(Map.Entry<String, String> entry:chunkSegments.entrySet()){
			sb.append(entry.getValue());
			idsInChunk.add(entry.getKey());
			sb.append(SEGMENTS_DELIMITER);
			
		}
		System.out.println("CHUNK\n\n"+sb);
		String translatedChunk=Translate.execute(sb.toString(), Language.fromString(sourceLanguage),Language.fromString(destinationLanguage));	
		System.out.println("TRANSLATED CHUNK\n\n"+translatedChunk);
		int segmentCounter=0;
		String[] translatedSegments=translatedChunk.split(SEGMENTS_DELIMITER);		
		for(String segmentTranslation:translatedSegments){
			resultMap.put(idsInChunk.get(segmentCounter),segmentTranslation);
			segmentCounter++;
		}
		return resultMap;
		
	}
		
	private static int findSplitIndex(String segment, int remainingSpace) {
		Matcher matcher = END_FRAGMENT_PATTERN.matcher(segment);
		int currentIndex=0;
		while(matcher.find() && currentIndex<remainingSpace){
			currentIndex=matcher.end();
		}
		if(currentIndex==0){					//TODO not sure
			System.out.println("ZERO INDEX!");
			return remainingSpace;
		}
		else
			return currentIndex;
	}
	
}
