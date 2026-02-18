package com.master.air.security.jaas;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * CallbackHandler pour le login username/password.
 */
public class UsernamePasswordCallbackHandler implements CallbackHandler {

    private final String username;
    private final char[] password;

    public UsernamePasswordCallbackHandler(String username, String password) {
        this.username = username;
        this.password = password != null ? password.toCharArray() : new char[0];
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback cb : callbacks) {
            if (cb instanceof NameCallback nc) {
                nc.setName(username);
            } else if (cb instanceof PasswordCallback pc) {
                pc.setPassword(password);
            } else {
                throw new UnsupportedCallbackException(cb, "Callback non supporte: " + cb.getClass());
            }
        }
    }
}
