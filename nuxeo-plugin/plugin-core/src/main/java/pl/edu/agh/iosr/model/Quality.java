package pl.edu.agh.iosr.model;

import javax.persistence.Entity;

@Entity
public class Quality extends StringEntity {

	public Quality() {
		super();
	}

	public Quality(String value) {
		super(value);
	}
	
}
