package pl.edu.agh.xliffhandler.tu;

/**
 * Class to represent a (sentence, probably) segment in a muti-sentence
 * paragraph.
 *
 * @author Maria Szymczak, Weldon Whipple &lt;weldon@lingotek.com&gt;
 */

public class SegmentInfo {
	
    private String segment;       // Candidate segment string
    private boolean translatable; // Does this candidate have translatable text?
    
    /**
     * Constructor that takes a candidate segment string and an indication of
     * whether it is translatable text. (If the segment contains no translatable
     * information, we will write it to the format file and put a placeholder
     * in the skeleton file to retrieve the information and put it in the
     * translated document that is generated from a target in the XLIFF.)
     * @param segment A (sentence) segment in a paragraph
     * @param isTranslatable Indicator of whether the segment contains
     *        translatable text.
     */
    public SegmentInfo(String segment, boolean isTranslatable) {
        this.segment = segment;
        this.translatable = isTranslatable;
    }
    
    /**
     * Return the candidate segment string
     * @return The segment.
     */
    public String getSegmentStr() {
        return segment;
    }

    public void setSegmentStr(String segStr) {
        this.segment = segStr;
    }
    
    /**
     * Indicate whether the segment has translatable text.
     * @return true if text is translatable, else false.
     */
    public boolean translatable() {
        return translatable;
    }
}
