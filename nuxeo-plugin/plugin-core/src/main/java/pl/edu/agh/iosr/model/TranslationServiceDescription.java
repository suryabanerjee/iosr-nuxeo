package pl.edu.agh.iosr.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import pl.edu.agh.iosr.util.IosrRandomGenerator;

/**
 * Opisuje konfigurację pojedynczego serwisu tłumaczącego
 * */
@Entity
@Table(name = "TRANSLATION_SERVICE_DESCRIPTION")
public class TranslationServiceDescription implements java.io.Serializable {

	private static final long serialVersionUID = 2668787645568473617L;

	public TranslationServiceDescription() {
	}

	/**
	 * id web serwisu
	 * */
	private Long wsId = IosrRandomGenerator.nextLong();

	/**
	 * nietrudno się domyśleć
	 * */
	private String endpoint;

	private Boolean languageDetection = false;

	private Collection<Quality> supportedQualities = new HashSet<Quality>();

	private Collection<LangPair> supportedLangPairs = new HashSet<LangPair>();

	private Collection<DocumentType> supportedDocumentTypes = new HashSet<DocumentType>();

	private String name;

	private String description;

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("\tServiceDescription no: " + wsId + "\n");
		result.append("\tName: " + name + "\n");
		result.append("\tDescription: " + description + "\n");
		result.append("\tLanguage detection: " + languageDetection + "\n");

		if (supportedLangPairs != null) {
			result.append("\tSupported lang pairs:\n");
			for (LangPair tmp : supportedLangPairs) {
				result.append("\t\tFrom: " + tmp.getFromLang() + "; To: "
						+ tmp.getToLang() + "\n");
			}
		}

		if (supportedQualities != null) {
			result.append("\tSupported qualities:\n");
			for (StringEntity q : supportedQualities) {
				result.append("\t\t" + q + "\n");
			}
		}

		if (supportedDocumentTypes != null) {
			result.append("\tSupported document types:\n");
			for (StringEntity dt : supportedDocumentTypes) {
				result.append("\t\t" + dt + "\n");
			}
		}
		return result.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Id
	public Long getWsId() {
		return wsId;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Boolean getLanguageDetection() {
		return languageDetection;
	}

	public void setLanguageDetection(Boolean languageDetection) {
		this.languageDetection = languageDetection;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setWsId(Long wsId) {
		this.wsId = wsId;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Collection<LangPair> getSupportedLangPairs() {
		return supportedLangPairs;
	}

	public void setSupportedLangPairs(Collection<LangPair> supportedLangPairs) {
		this.supportedLangPairs = supportedLangPairs;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Collection<Quality> getSupportedQualities() {
		return supportedQualities;
	}

	public void setSupportedQualities(Collection<Quality> supportedQualities) {
		this.supportedQualities = supportedQualities;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Collection<DocumentType> getSupportedDocumentTypes() {
		return supportedDocumentTypes;
	}

	public void setSupportedDocumentTypes(
			Collection<DocumentType> supportedDocumentTypes) {
		this.supportedDocumentTypes = supportedDocumentTypes;
	}

	
}
