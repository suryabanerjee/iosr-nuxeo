package org.nuxeo.project.czopson;

import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Scope(ScopeType.SESSION)
@Name("translationConfig")
public class TranslationConfig {
	
	private String langFrom, langTo;
	private SelectItem[] langOptions;
	
	protected FacesContext facesContext = FacesContext.getCurrentInstance();
	
	/**
	 * Pobieramy takie lokale, żeby miały wszystko co potrzeba.
	 * Tylko w nich będzie można przebierać.
	 * 
	 * Prawdopobonie trzeba będzie tą listę inicjować jakoś wcześniej,
	 * bo pewnie na tyle języków co zna java to się nie da tłumaczyć
	 * */
	@PostConstruct
	public void init() {
		
		try {
			// lokale z kontekstu z sesji
			final Locale userLocale;
			Locale tmpLocale = getSeamLocale();
			
			if (facesContext != null) {
				userLocale = facesContext.getExternalContext().getRequestLocale();
			}
			else {
				userLocale = tmpLocale;
			}
			
			Locale[] ll = java.util.Locale.getAvailableLocales();

			// dla porządku nie chcemy, że się jakieś dublowały
			Set<Locale> locales = new TreeSet<Locale>(new Comparator<Locale>() {
				public int compare(Locale arg0, Locale arg1) {
					return arg0.getDisplayLanguage(userLocale).compareTo(
							arg1.getDisplayLanguage(userLocale));
				}
			});

			// tylko te kompletne bierzemy
			for (Locale l : ll) {
				if (l.getDisplayLanguage() != null
						&& l.getDisplayLanguage() != ""
						&& l.getDisplayCountry() != null
						&& l.getDisplayCountry() != "") {
					locales.add(l);
				}
			}

			langOptions = new SelectItem[locales.size()];
			int i = 0;
			for (Locale l : locales) {
				langOptions[i++] = new SelectItem(l.getISO3Language(), l
						.getDisplayLanguage(userLocale));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Locale getSeamLocale() {
	      final org.jboss.seam.Component localeComponent =
	         (org.jboss.seam.Component)Contexts
	         .lookupInStatefulContexts("org.jboss.seam.core.locale.component");
	      if (localeComponent != null) {
	         final org.jboss.seam.core.Locale seamLocale =
	            (org.jboss.seam.core.Locale) localeComponent.newInstance();
	         if (seamLocale != null) {
	            return seamLocale.getLocale();
	         }
	      }
	      return Locale.ENGLISH;
	   }
	
	
	/**
	 * Ta metoda bedzie moze przywracac widok z zakladki z konfiguracja
	 * do listy plikow 
	 * */
	public String submit() {
		return "#";
	}
	
	/* GETTERS AND SETTERS */	
	
	public SelectItem[] getLangOptions() {
		return langOptions;
	}

	public void setLangOptions(SelectItem[] langOptions) {
		this.langOptions = langOptions;
	}

	public String getLangFrom() {
		return langFrom;
	}

	public void setLangFrom(String langFrom) {
		this.langFrom = langFrom;
	}

	public String getLangTo() {
		return langTo;
	}

	public void setLangTo(String langTo) {
		this.langTo = langTo;
	} 

}
