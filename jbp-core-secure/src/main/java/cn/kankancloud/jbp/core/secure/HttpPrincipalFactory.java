package cn.kankancloud.jbp.core.secure;

import cn.kankancloud.jbp.core.security.context.PrincipalFactory;
import cn.kankancloud.jbp.core.security.principal.IPrincipal;
import cn.kankancloud.jbp.web.util.RequestUtil;

public class HttpPrincipalFactory implements PrincipalFactory {

    public static final String PRINCIPAL_ATTR = "__JBP_PRINCIPAL_TAG__";

    @Override
    public String name() {
        return HttpPrincipalFactory.class.getSimpleName();
    }

    @Override
    public IPrincipal getCurrent() {
        return (IPrincipal) RequestUtil.getRequest().getAttribute(PRINCIPAL_ATTR);
    }

    @Override
    public void setCurrent(IPrincipal principal) {
        RequestUtil.getRequest().setAttribute(PRINCIPAL_ATTR, principal);
    }
}
