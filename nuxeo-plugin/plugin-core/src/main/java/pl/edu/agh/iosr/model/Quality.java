package pl.edu.agh.iosr.model;

import javax.persistence.Entity;

/**
 * Oznacza jakość tłumaczenia. Istnieje z powodu błędów w implementacji JPA1.0
 * */
@Entity
public class Quality extends StringEntity {

	public Quality() {
		super();
	}

	public Quality(String value) {
		super(value);
	}

}
