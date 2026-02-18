package com.master.air.security.jaas;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * CallbackHandler pour le login par token.
 */
public class TokenCallbackHandler implements CallbackHandler {

    private final String token;

    public TokenCallbackHandler(String token) {
        this.token = token;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback cb : callbacks) {
            if (cb instanceof TokenCallback tc) {
                tc.setToken(token);
            } else {
                throw new UnsupportedCallbackException(cb, "Callback non supporte: " + cb.getClass());
            }
        }
    }
}
