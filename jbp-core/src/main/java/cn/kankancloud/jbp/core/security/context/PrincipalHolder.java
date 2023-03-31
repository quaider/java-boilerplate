package cn.kankancloud.jbp.core.security.context;

import cn.kankancloud.jbp.core.security.principal.IPrincipal;

public interface PrincipalHolder {

    String name();

    IPrincipal getCurrent();

    default boolean supported() {
        return true;
    }

    void setCurrent(IPrincipal principal);
}
