package com.master.air.security.jaas;

import com.master.air.security.TokenStore;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * LoginModule JAAS: authentification par token.
 * - Valide le token (TokenStore)
 * - Reconstitue l'identite dans le Subject (UserPrincipal + RolePrincipal)
 */
public class TokenLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;

    private boolean loginSucceeded = false;

    private Long userId;
    private String username;
    private final Set<RolePrincipal> rolesToAdd = new HashSet<>();
    private UserPrincipal userPrincipal;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("CallbackHandler manquant");
        }

        TokenCallback tokenCb = new TokenCallback();

        try {
            callbackHandler.handle(new javax.security.auth.callback.Callback[]{tokenCb});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Erreur CallbackHandler: " + e.getMessage());
        }

        String token = tokenCb.getToken();
        if (token == null || token.isBlank()) {
            loginSucceeded = false;
            throw new FailedLoginException("Missing token");
        }

        Optional<TokenStore.TokenInfo> infoOpt = TokenStore.validateToken(token);
        if (infoOpt.isEmpty()) {
            loginSucceeded = false;
            throw new FailedLoginException("Invalid or expired token");
        }

        TokenStore.TokenInfo info = infoOpt.get();
        this.userId = info.getUserId();
        this.username = info.getUsername();

        this.userPrincipal = new UserPrincipal(userId, username);
        rolesToAdd.add(new RolePrincipal("ROLE_USER"));
        if ("admin".equalsIgnoreCase(username)) {
            rolesToAdd.add(new RolePrincipal("ROLE_ADMIN"));
        }

        loginSucceeded = true;
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) return false;
        subject.getPrincipals().add(userPrincipal);
        subject.getPrincipals().addAll(rolesToAdd);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        logout();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        if (subject != null) {
            if (userPrincipal != null) subject.getPrincipals().remove(userPrincipal);
            subject.getPrincipals().removeAll(rolesToAdd);
        }
        loginSucceeded = false;
        userId = null;
        username = null;
        rolesToAdd.clear();
        userPrincipal = null;
        return true;
    }
}
