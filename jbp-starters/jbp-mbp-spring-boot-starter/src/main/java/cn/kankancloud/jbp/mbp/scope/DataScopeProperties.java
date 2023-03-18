package cn.kankancloud.jbp.mbp.scope;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "mybatis-plus.plugin.scope.data")
public class DataScopeProperties {
    private boolean enable = false;

    /**
     * 忽略目标表中不存在数据字段时的过滤，即不追加数据字段条件
     */
    private boolean ignoreNonExistField = false;

    /**
     * 数据字段名称
     */
    private String column = "";

    /**
     * 多租户数据表
     */
    private List<String> ignoreTables = new ArrayList<>();
}
