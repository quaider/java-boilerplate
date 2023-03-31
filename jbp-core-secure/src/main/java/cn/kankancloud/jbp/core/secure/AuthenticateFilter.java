package cn.kankancloud.jbp.core.secure;

import cn.kankancloud.jbp.core.abstraction.IDisposable;
import cn.kankancloud.jbp.core.security.context.SupportedPrincipalHolders;
import cn.kankancloud.jbp.core.security.principal.IPrincipal;
import cn.kankancloud.jbp.core.security.principal.PrincipalResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticateFilter extends OncePerRequestFilter {

    private final ObjectProvider<PrincipalResolver> principalResolverProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        PrincipalResolver principalResolver = principalResolverProvider.getIfAvailable();
        if (principalResolver == null) {
            log.warn("PrincipalResolver is not provided, please check whether or not your application needs user principal");
            filterChain.doFilter(request, response);
            return;
        }

        // fetch user principal from request
        IPrincipal principal = principalResolver.resolve(request);

        // append principal to supported principal holders
        SupportedPrincipalHolders.getPrincipalFactories().forEach(fac -> {
            if (fac instanceof IDisposable) {
                ((IDisposable) fac).dispose();
            } else {
                fac.setCurrent(principal);
            }
        });

        filterChain.doFilter(request, response);
    }
}
