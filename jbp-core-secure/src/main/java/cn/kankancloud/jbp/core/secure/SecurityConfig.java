package cn.kankancloud.jbp.core.secure;

import cn.kankancloud.jbp.core.secure.jwt.JwtConfig;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
public class SecurityConfig {

    private JwtConfig jwt;
    private Set<String> ignoreAuthenticateUrls;

    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PRIVATE)
    private Set<String> innerIgnoreAuthenticateUrls;

    public SecurityConfig() {
        innerIgnoreAuthenticateUrls = Sets.newHashSet(
                "/swagger-ui/**",
                "/doc.html"
        );
    }

    public Set<String> getIgnoreAuthenticateUrls() {
        return ignoreAuthenticateUrls == null ? innerIgnoreAuthenticateUrls : Sets.union(ignoreAuthenticateUrls, innerIgnoreAuthenticateUrls);
    }
}
