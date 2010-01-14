package pl.edu.agh.iosr.nuxeo.ws.result;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class ServerPasswordCallback implements CallbackHandler {

    private static final String BUNDLE_LOCATION = "AuthServer";
    private static final String PASSWORD_PROPERTY_NAME = "auth.manager.password";

    private static String password;
    static {
        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_LOCATION);
        password = bundle.getString(PASSWORD_PROPERTY_NAME);
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];

        if (pc.getIdentifier().equals("nuxeo")) {
           if (!pc.getPassword().equals(password)) {
        	   throw new SecurityException ("Password is invalid!");
           }
        }
    }

}
