package cn.kankancloud.jbp.core.secure.jwt;

import lombok.Data;

@Data
public class JwtConfig {

    /**
     * 秘钥
     */
    private String secret;

    /**
     * 过期时间 (秒)
     */
    private int expire;
}
