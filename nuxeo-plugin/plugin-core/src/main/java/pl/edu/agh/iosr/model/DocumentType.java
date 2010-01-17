package pl.edu.agh.iosr.model;

import javax.persistence.Entity;

/**
 * Oznacza typy dokumentów. Istnieje z powodu błędów w implementacji JPA1.0
 * */
@Entity
public class DocumentType extends StringEntity {

	public DocumentType() {
		super();
	}

	public DocumentType(String value) {
		super(value);
	}

}
