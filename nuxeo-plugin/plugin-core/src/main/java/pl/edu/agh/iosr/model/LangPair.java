package pl.edu.agh.iosr.model;

import javax.persistence.Embeddable;

/**
 * Para kodow jezykow
 * */
@Embeddable
public class LangPair implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 407005456052342702L;
	
	private String from, to;
	
	public LangPair() {}
	
	public LangPair(String from, String to) {
		super();
		this.from = from;
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
} 
