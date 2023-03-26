package cn.kankancloud.jbp.core.security.principal;

import java.security.Principal;

public interface IPrincipal extends Principal {

    /**
     * Retrieve the identity object
     */
    IIdentity identity();

    /**
     * Perform a check for a specific role
     */
    boolean isInRole(String role);

    default String getName() {
        return this.getClass().getSimpleName();
    }
}
