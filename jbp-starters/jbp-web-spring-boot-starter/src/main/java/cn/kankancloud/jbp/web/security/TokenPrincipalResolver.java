package cn.kankancloud.jbp.web.security;

import cn.kankancloud.jbp.core.secure.UserRolePrincipal;
import cn.kankancloud.jbp.core.secure.jwt.JwtUtil;
import cn.kankancloud.jbp.core.secure.jwt.TokenGenerator;
import cn.kankancloud.jbp.core.security.principal.IPrincipal;
import cn.kankancloud.jbp.core.security.principal.PrincipalResolver;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
@RequiredArgsConstructor
public class TokenPrincipalResolver implements PrincipalResolver {

    private final TokenGenerator tokenGenerator;

    @Override
    public IPrincipal resolve(Object source) {
        if (source instanceof HttpServletRequest request) {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ")) {
                return null;
            }

            token = token.substring(7);
            if (StringUtils.isEmpty(token) || !tokenGenerator.verifyToken(token)) {
                return null;
            }

            return new UserRolePrincipal(JwtUtil.fromToken(token));
        }

        return null;
    }
}
