package cn.kankancloud.jbp.core.principal;

public interface PrincipalFactory {

    String DEFAULT_IDENTITY_CLAIM = "X-UserId";
    String DEFAULT_ACCOUNT_CLAIM = "X-Account";
    String DEFAULT_FULLNAME_CLAIM = "X-FullName";

    String name();

    UserPrincipal getCurrent();

    default boolean supported() {
        return true;
    }

    void setCurrent(UserPrincipal userPrincipal);
}
