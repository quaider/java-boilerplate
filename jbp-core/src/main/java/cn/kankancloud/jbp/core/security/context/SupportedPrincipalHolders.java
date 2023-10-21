package cn.kankancloud.jbp.core.security.context;

import cn.kankancloud.jbp.core.abstraction.IDisposable;
import cn.kankancloud.jbp.core.RequestScopeDisposableTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SupportedPrincipalHolders {

    private static final Logger log = Logger.getLogger(SupportedPrincipalHolders.class.getName());

    private SupportedPrincipalHolders() {
    }

    private static final List<PrincipalHolder> principalFactories = new ArrayList<>();

    public static void register(PrincipalHolder factory) {
        if (principalFactories.stream().noneMatch(f -> f.name().equals(factory.name()))) {
            principalFactories.add(factory);

            if (factory instanceof IDisposable disposableFactory) {
                RequestScopeDisposableTracker.track(disposableFactory);
            }

            return;
        }

        log.log(Level.WARNING, "PrincipalFactory with name `{}` already exist", factory.name());
    }

    public static List<PrincipalHolder> getPrincipalFactories() {
        return Collections.unmodifiableList(principalFactories);
    }
}
