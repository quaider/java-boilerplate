package cn.kankancloud.jbp.core.security.principal;

public interface IPrincipal {

    /**
     * Retrieve the identity object
     */
    IIdentity identity();

    /**
     * Perform a check for a specific role
     */
    boolean isInRole(String role);
}
