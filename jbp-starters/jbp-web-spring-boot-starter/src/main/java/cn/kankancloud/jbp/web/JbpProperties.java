package cn.kankancloud.jbp.web;

import cn.kankancloud.jbp.core.secure.SecurityConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jbp")
@Data
public class JbpProperties {
    private SecurityConfig security;
}
