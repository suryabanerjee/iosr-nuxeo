package pl.edu.agh.iosr.nuxeo.translator.service;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.ws.security.WSPasswordCallback;


/**
 * Klasa zapewniajaca dane potrzebne do autentykacji w serwisie przeznaczonym do zwracania rezultatow rezulaty
 * @author lewickitom
 * */
public class ClientPasswordHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];

        // set the password for our message.
        pc.setPassword("ala123");
    }

}
