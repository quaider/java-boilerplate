package cn.kankancloud.jbp.core.security.principal;

public interface IIdentity {
    String name();

    String authenticationType();

    boolean isAuthenticated();
}
