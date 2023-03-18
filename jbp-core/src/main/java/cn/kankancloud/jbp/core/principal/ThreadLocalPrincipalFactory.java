package cn.kankancloud.jbp.core.principal;

import cn.kankancloud.jbp.core.abstraction.IDisposable;

public class ThreadLocalPrincipalFactory implements PrincipalFactory, IDisposable {

    private static final ThreadLocal<UserPrincipal> ctx = new InheritableThreadLocal<>();

    @Override
    public String name() {
        return ThreadLocalPrincipalFactory.class.getSimpleName();
    }

    @Override
    public UserPrincipal getCurrent() {
        return ctx.get();
    }

    @Override
    public void setCurrent(UserPrincipal userPrincipal) {
        ctx.set(userPrincipal);
    }

    @Override
    public void dispose() {
        ctx.remove();
    }

}
