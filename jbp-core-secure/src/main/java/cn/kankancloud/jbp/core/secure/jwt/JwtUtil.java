package cn.kankancloud.jbp.core.secure.jwt;

import cn.kankancloud.jbp.core.secure.UseRoleIdentity;
import cn.kankancloud.jbp.core.secure.UserDetail;
import cn.kankancloud.jbp.core.security.principal.ClaimTypes;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {

    private JwtUtil() {
    }

    public static String createToken(String secret, int expireInSeconds, UseRoleIdentity useRoleIdentity) {
        return JWT.create()
                .withClaim(shorName(ClaimTypes.FULLNAME), useRoleIdentity.getUserDetail().getFullname())
                .withClaim(shorName(ClaimTypes.ACCOUNT), useRoleIdentity.getUserDetail().getAccount())
                .withArrayClaim(shorName(ClaimTypes.ROLE), useRoleIdentity.getRoles().toArray(new String[0]))
                .withExpiresAt(new Date(System.currentTimeMillis() + expireInSeconds * 1000L))
                .sign(Algorithm.HMAC256(secret));
    }

    public static String createToken(String secret, int expireInSeconds, Map<String, Object> payload) {
        return JWT.create()
                .withPayload(payload)
                .withExpiresAt(new Date(System.currentTimeMillis() + expireInSeconds * 1000L))
                .sign(Algorithm.HMAC256(secret));
    }

    public static boolean verifyToken(String secret, String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static UseRoleIdentity fromToken(String token) {
        try {
            DecodedJWT decode = JWT.decode(token);
            String fullname = decode.getClaim(shorName(ClaimTypes.FULLNAME)).asString();
            String account = decode.getClaim(shorName(ClaimTypes.ACCOUNT)).asString();
            String[] roles = decode.getClaim(shorName(ClaimTypes.ROLE)).asArray(String.class);

            return new UseRoleIdentity(new UserDetail(null, account, fullname), Lists.newArrayList(roles));
        } catch (JWTDecodeException e) {
            log.warn("decode token error, token={}", token, e);
        }

        return null;
    }

    private static String shorName(String claimType) {
        return claimType.substring(ClaimTypes.CLAIM_TYPE_NAMESPACE.length());
    }
}
