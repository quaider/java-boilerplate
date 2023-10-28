package cn.kankancloud.jbp.core.secure.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Token {
    private final String accessToken;
    private final Integer expiresIn;

}
