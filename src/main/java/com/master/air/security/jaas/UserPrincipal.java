package com.master.air.security.jaas;

import java.security.Principal;
import java.util.Objects;

/**
 * Principal JAAS representant l'utilisateur courant.
 */
public class UserPrincipal implements Principal {
    private final Long userId;
    private final String name;

    public UserPrincipal(Long userId, String username) {
        this.userId = userId;
        this.name = username;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPrincipal that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }

    @Override
    public String toString() {
        return "UserPrincipal{userId=" + userId + ", name='" + name + "'}";
    }
}
