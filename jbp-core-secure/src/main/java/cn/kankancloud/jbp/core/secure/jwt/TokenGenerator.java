package cn.kankancloud.jbp.core.secure.jwt;

import cn.kankancloud.jbp.core.secure.UseRoleIdentity;

public interface TokenGenerator {
    String generateToken(UseRoleIdentity UseRoleIdentity);

    boolean verifyToken(String token);
}
