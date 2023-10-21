package cn.kankancloud.jbp.web.security;

import cn.kankancloud.jbp.core.secure.UseRoleIdentity;
import cn.kankancloud.jbp.core.secure.jwt.JwtUtil;
import cn.kankancloud.jbp.core.secure.jwt.TokenGenerator;
import cn.kankancloud.jbp.web.JbpProperties;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator implements TokenGenerator {

    private final JbpProperties jbpProperties;

    public JwtTokenGenerator(JbpProperties jbpProperties) {
        if (jbpProperties == null || jbpProperties.getSecurity() == null || jbpProperties.getSecurity().getJwt() == null) {
            throw new IllegalArgumentException("jbpProperties");
        }

        this.jbpProperties = jbpProperties;
    }

    @Override
    public String generateToken(UseRoleIdentity identity) {
        return JwtUtil.createToken(jbpProperties.getSecurity().getJwt().getSecret(), jbpProperties.getSecurity().getJwt().getExpire(), identity);
    }

    @Override
    public boolean verifyToken(String token) {
        return JwtUtil.verifyToken(jbpProperties.getSecurity().getJwt().getSecret(), token);
    }
}
