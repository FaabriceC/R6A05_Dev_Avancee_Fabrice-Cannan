package com.master.air.security.jaas;

import com.master.air.model.User;
import com.master.air.service.UserService;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
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
 * LoginModule JAAS: authentification username/password.
 * - Pas de session HTTP
 * - Ajoute UserPrincipal + RolePrincipal dans le Subject.
 */
public class DbLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;

    private boolean loginSucceeded = false;

    private Long userId;
    private String username;
    private final Set<RolePrincipal> rolesToAdd = new HashSet<>();
    private UserPrincipal userPrincipal;

    private final UserService userService = new UserService();

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

        NameCallback nameCb = new NameCallback("username");
        PasswordCallback passCb = new PasswordCallback("password", false);

        try {
            callbackHandler.handle(new javax.security.auth.callback.Callback[]{nameCb, passCb});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Erreur CallbackHandler: " + e.getMessage());
        }

        String u = nameCb.getName();
        char[] pchars = passCb.getPassword();
        String p = pchars != null ? new String(pchars) : "";

        Optional<User> userOpt = userService.authenticate(u, p);
        if (userOpt.isEmpty()) {
            loginSucceeded = false;
            throw new FailedLoginException("Invalid credentials");
        }

        User user = userOpt.get();
        this.userId = user.getId();
        this.username = user.getUsername();

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
