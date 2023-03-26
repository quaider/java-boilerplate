package cn.kankancloud.jbp.core.security.context;

import cn.kankancloud.jbp.core.security.principal.IPrincipal;

public class PrincipalContext {

    private PrincipalContext() {
    }

    public static IPrincipal getPrincipal() {
        for (PrincipalFactory factory : SupportedPrincipalFactories.getPrincipalFactories()) {

            if (factory.supported() && factory.getCurrent() != null) {
                return factory.getCurrent();
            }
        }

        return null;
    }
}
