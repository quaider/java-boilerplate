package cn.kankancloud.jbp.mbp.tenant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "mybatis-plus.plugin.tenant")
public class TenantProperties {

    /**
     * 是否启用多租户插件
     */
    private boolean enable = false;

    /**
     * 多租户字段名称
     */
    private String column = "tenant_id";

    /**
     * 多租户数据表
     */
    private List<String> ignoreTables = new ArrayList<>();
}
