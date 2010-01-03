package pl.edu.agh.iosr.model;

import javax.persistence.Entity;

@Entity
public class DocumentType extends StringEntity {

	public DocumentType() {
		super();
	}

	public DocumentType(String value) {
		super(value);
	}
	
}
