package cn.kankancloud.jbp.core.principal;

public class PrincipalContext {

    private PrincipalContext() {
    }

    public static UserPrincipal getPrincipal() {
        for (PrincipalFactory factory : SupportedPrincipalFactories.getPrincipalFactories()) {

            if (!factory.supported() || factory.getCurrent() == null) {
                continue;
            }

            return factory.getCurrent();
        }

        return null;
    }
}
