package cn.kankancloud.jbp.core.security.principal;

public interface PrincipalResolver {

    /**
     * resolve principal from source
     *
     * @param source HttpServletRequest or any other object
     * @return a principal
     */
    IPrincipal resolve(Object source);
}
