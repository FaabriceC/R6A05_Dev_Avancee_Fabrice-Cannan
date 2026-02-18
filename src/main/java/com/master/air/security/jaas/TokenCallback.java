package com.master.air.security.jaas;

import javax.security.auth.callback.Callback;

/**
 * Callback custom pour transporter un token Bearer vers un LoginModule.
 */
public class TokenCallback implements Callback {
    private String token;

    public TokenCallback() {}

    public TokenCallback(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
