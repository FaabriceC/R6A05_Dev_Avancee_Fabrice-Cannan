package com.master.air.security.jaas;

import java.security.Principal;
import java.util.Objects;

/**
 * Principal JAAS representant un role applicatif (ex: ROLE_USER, ROLE_ADMIN).
 */
public class RolePrincipal implements Principal {
    private final String name;

    public RolePrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolePrincipal that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "RolePrincipal{name='" + name + "'}";
    }
}
