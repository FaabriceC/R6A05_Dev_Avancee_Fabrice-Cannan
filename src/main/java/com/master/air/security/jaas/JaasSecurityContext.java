package com.master.air.security.jaas;

import jakarta.ws.rs.core.SecurityContext;

import javax.security.auth.Subject;
import java.security.Principal;

/**
 * SecurityContext JAX-RS base sur un Subject JAAS.
 */
public class JaasSecurityContext implements SecurityContext {

    private final Subject subject;
    private final boolean secure;

    public JaasSecurityContext(Subject subject, boolean secure) {
        this.subject = subject;
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return subject.getPrincipals(UserPrincipal.class).stream().findFirst().orElse(null);
    }

    public Subject getSubject() {
        return subject;
    }

    @Override
    public boolean isUserInRole(String role) {
        return subject.getPrincipals(RolePrincipal.class).stream().anyMatch(r -> r.getName().equals(role));
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}
