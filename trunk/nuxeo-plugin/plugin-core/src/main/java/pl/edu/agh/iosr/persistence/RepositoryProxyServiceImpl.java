package pl.edu.agh.iosr.persistence;

import java.io.File;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentRef;

import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.services.RepositoryProxyService;

@Name("repositoryProxyService")
@Scope(ScopeType.APPLICATION)
public class RepositoryProxyServiceImpl implements RepositoryProxyService{

	@Override
	public File getFile(DocumentRef sourceDocument) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileExtension(DocumentRef sourceDocument) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveFile(TranslationOrder order, File resultFile) {
		// TODO Auto-generated method stub
		
	}

}
