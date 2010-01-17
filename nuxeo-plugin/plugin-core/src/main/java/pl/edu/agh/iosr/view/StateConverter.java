package pl.edu.agh.iosr.view;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import pl.edu.agh.iosr.model.TranslationOrder.RequestState;
import pl.edu.agh.iosr.util.MessagesLocalizer;

/**
 * Konwerter zgodny ze specyfikacją JSF 1.2.
 * 
 * Odpowiada za prawidłowe wyświetlanie statusu tłumaczenia.
 * Wykorzystuje pliki zasobów.
 * */
public class StateConverter implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		try {
			return RequestState.valueOf(arg2);
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if (!(arg2 instanceof RequestState)) {
			return "unknown";
		}
		else {
			return MessagesLocalizer.getMessage(((RequestState)arg2).toString());
		}
	}

}
