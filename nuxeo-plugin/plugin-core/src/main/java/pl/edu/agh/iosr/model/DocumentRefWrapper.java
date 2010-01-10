package pl.edu.agh.iosr.model;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;

@Embeddable
public class DocumentRefWrapper {

	private String path, name, type;
	
	@Transient
	public DocumentModel getDocumentModel() {
		return new DocumentModelImpl(path, name, type);
	}
	
	public DocumentRefWrapper() {}
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
