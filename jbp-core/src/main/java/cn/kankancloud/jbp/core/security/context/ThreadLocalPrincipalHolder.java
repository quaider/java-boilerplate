package cn.kankancloud.jbp.core.security.context;

import cn.kankancloud.jbp.core.abstraction.IDisposable;
import cn.kankancloud.jbp.core.security.principal.IPrincipal;

public class ThreadLocalPrincipalHolder implements PrincipalHolder, IDisposable {

    private static final ThreadLocal<IPrincipal> ctx = new InheritableThreadLocal<>();

    @Override
    public String name() {
        return ThreadLocalPrincipalHolder.class.getSimpleName();
    }

    @Override
    public IPrincipal getCurrent() {
        return ctx.get();
    }

    @Override
    public void setCurrent(IPrincipal principal) {
        ctx.set(principal);
    }

    @Override
    public void dispose() {
        ctx.remove();
    }

}
