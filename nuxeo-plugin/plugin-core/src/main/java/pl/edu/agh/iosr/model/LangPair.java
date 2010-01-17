package pl.edu.agh.iosr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Para kodow jezykow.
 * */
@Entity
@Table(name="LANG_PAIR")
//@Embeddable
public class LangPair implements java.io.Serializable {
	
	private static final long serialVersionUID = 6751218410817677316L;

	private String fromLang;
	
	private String toLang;
	
	private Long id;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public LangPair() {}
	
	public LangPair(String fromLang, String toLang) {
		super();
		this.fromLang = fromLang;
		this.toLang = toLang;
	}

	public String getFromLang() {
		return fromLang;
	}

	public void setFromLang(String fromLang) {
		this.fromLang = fromLang;
	}

	public String getToLang() {
		return toLang;
	}

	public void setToLang(String toLang) {
		this.toLang = toLang;
	}
	
} 
