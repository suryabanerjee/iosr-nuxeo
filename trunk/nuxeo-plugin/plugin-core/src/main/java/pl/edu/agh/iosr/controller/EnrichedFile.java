package pl.edu.agh.iosr.controller;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Wrapper dla dokumentow, przechowuje informacje pod jaka nazwa ma zaistniec
 * przetlumaczny dokument oraz czy zostal zaznaczony do tlumaczenia z listy
 * widoczne w graficznym interfejsie użytkownika.
 * */
public class EnrichedFile {

	private DocumentModel documentModel;
	private String targetName;
	private boolean selected;

	public EnrichedFile(DocumentModel documentModel) {
		this.documentModel = documentModel;
		targetName = "";
		selected = false;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

	public String getName() {
		return documentModel != null ? documentModel.getName() : null;
	}

}
