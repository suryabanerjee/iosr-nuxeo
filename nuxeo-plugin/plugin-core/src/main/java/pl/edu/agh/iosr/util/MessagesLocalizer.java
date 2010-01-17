package pl.edu.agh.iosr.util;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * Klasa użytkowa, pozwala pobierać odpowiednie frazy z plików zasobów.
 * */
public class MessagesLocalizer {

	// to be injected and overwritten
	public static String RESOURCES_PREFIX = "pl.edu.agh.iosr.bundles.";

	
	private static ClassLoader classLoader;
	private static Map<String, ResourceBundle> bundles = new HashMap<String, ResourceBundle>();

	public static String getMessage(String resourceId) {
		return getMessage("translation", resourceId);
	}

	
	/**
	 * Returns message value from specified bundle and message key.
	 * 
	 * @param bundleName
	 *            - unqualified bundle name. For example:<br/>
	 *            Full bundle name:
	 *            <b>org.myOrganization.myApplication.myResurces
	 *            .messages.properties</b></br> Proper name: <b>messages</b>.
	 * @param resourceId
	 *            message key.
	 * @return Message value.
	 * */
	public static String getMessage(String bundleName, String resourceId) {

		bundleName = RESOURCES_PREFIX + bundleName;

		FacesContext context = FacesContext.getCurrentInstance();
		if (context == null) {
			log(MessagesLocalizer.class, "GetMessage(). FacesContext is null");
		}

		Locale locale = getLocale(context);
		if (locale == null) {
			log(MessagesLocalizer.class, "GetMessage(). Locale is null");
		}

		ClassLoader loader = getClassLoader();
		if (loader == null) {
			log(MessagesLocalizer.class, "GetMessage(). ClassLoader is null");
		}

		return getString(bundleName, resourceId, locale, loader);
	}

	private static String getString(String bundleName, String resourceId,
			Locale locale, ClassLoader loader) {

		String resource = null;
		ResourceBundle bundle = bundles.get(bundleName);

		if (bundle == null) {
			bundle = ResourceBundle.getBundle(bundleName, locale, loader);
			if (bundle != null) {
				bundles.put(bundleName, bundle);
			} else {
				log(MessagesLocalizer.class, "No bundle found for bundle name: "
						+ bundleName);
				return null;
			}
		}

		if (bundle != null)
			try {
				resource = bundle.getString(resourceId);
				log(MessagesLocalizer.class, "Unbundling: " + resource);
			} catch (MissingResourceException ex) {
				log(MessagesLocalizer.class, "GetString(). " + ex.getMessage());
			}

		return resource;
	}

	public static Locale getLocale(FacesContext context) {
		Locale locale = null;
		UIViewRoot viewRoot = context.getViewRoot();
		if (viewRoot != null)
			locale = viewRoot.getLocale();
		if (locale == null)
			locale = Locale.getDefault();
		return locale;
	}

	public static ClassLoader getClassLoader() {
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null)
				classLoader = ClassLoader.getSystemClassLoader();
		}
		return classLoader;
	}

	public static String getResourcesPrefix() {
		return RESOURCES_PREFIX;
	}

	public static void setResourcesPrefix(String resourcePrefix) {
		RESOURCES_PREFIX = resourcePrefix;
	}

}