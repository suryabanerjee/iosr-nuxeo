package pl.edu.agh.iosr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Wrapper dla typu String.
 * */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class StringEntity {

	private String value;
	
	private Long id;

	public StringEntity() {}
	
	public StringEntity(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String toString() {
		return value;
	}
	
}
