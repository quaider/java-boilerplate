package cn.kankancloud.jbp.core.secure;

import cn.kankancloud.jbp.core.exception.BizAuthenticateException;
import cn.kankancloud.jbp.core.security.context.SupportedPrincipalHolders;
import cn.kankancloud.jbp.core.security.principal.IPrincipal;
import cn.kankancloud.jbp.core.security.principal.PrincipalResolver;
import cn.kankancloud.jbp.web.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticateChecker {

    private final ObjectProvider<PrincipalResolver> principalResolverProvider;

    protected void mustAuthenticated() {
        PrincipalResolver principalResolver = principalResolverProvider.getIfAvailable();
        if (principalResolver == null) {
            log.error("PrincipalResolver not found");
            throw new BizAuthenticateException();
        }

        // fetch user principal from request
        IPrincipal principal = principalResolver.resolve(RequestUtil.getRequest());
        if (principal == null || !principal.identity().isAuthenticated()) {
            throw new BizAuthenticateException();
        }

        // append principal to supported principal holders
        SupportedPrincipalHolders.getPrincipalFactories()
                .forEach(fac -> fac.setCurrent(principal));
    }
}
