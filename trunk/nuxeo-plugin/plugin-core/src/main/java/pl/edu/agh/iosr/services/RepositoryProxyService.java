package pl.edu.agh.iosr.services;

import java.io.File;

import org.nuxeo.ecm.core.api.DocumentRef;

import pl.edu.agh.iosr.model.TranslationOrder;

public interface RepositoryProxyService {

	String getFileExtension(DocumentRef sourceDocument);

	File getFile(DocumentRef sourceDocument);

	void saveFile(TranslationOrder order, File resultFile);

}
