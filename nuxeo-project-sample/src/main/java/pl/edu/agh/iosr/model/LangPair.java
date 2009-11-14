package pl.edu.agh.iosr.model;

/**
 * Para kodow jezykow
 * */
public class LangPair{
	
	private String from, to;
	
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
