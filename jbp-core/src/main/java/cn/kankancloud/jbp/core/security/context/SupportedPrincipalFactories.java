package cn.kankancloud.jbp.core.security.context;

import cn.kankancloud.jbp.core.abstraction.IDisposable;
import cn.kankancloud.jbp.core.RequestScopeDisposableTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SupportedPrincipalFactories {

    private static final Logger log = Logger.getLogger(SupportedPrincipalFactories.class.getName());

    private SupportedPrincipalFactories() {
    }

    private static final List<PrincipalFactory> principalFactories = new ArrayList<>();

    public static void register(PrincipalFactory factory) {
        if (principalFactories.stream().noneMatch(f -> f.name().equals(factory.name()))) {
            principalFactories.add(factory);

            if (factory instanceof IDisposable) {
                RequestScopeDisposableTracker.track((IDisposable) factory);
            }

            return;
        }

        log.log(Level.WARNING, "PrincipalFactory with name `{}` already exist", factory.name());
    }

    public static List<PrincipalFactory> getPrincipalFactories() {
        return Collections.unmodifiableList(principalFactories);
    }
}
