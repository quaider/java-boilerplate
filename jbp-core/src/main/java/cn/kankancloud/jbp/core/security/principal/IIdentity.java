package cn.kankancloud.jbp.core.security.principal;

public interface IIdentity {

    /**
     * 票据名(如用户名)
     */
    String name();

    String authenticationType();

    boolean isAuthenticated();
}
