package cn.kankancloud.jbp.core;

public class ContextKey {

    private ContextKey() {
    }

    /**
     * 租户数据范围类型
     */
    public static final String TENANT_SCOPE_TYPE = "data.scope.tenant.scope_type";

    /**
     * 租户数据范围值
     */
    public static final String TENANT_SCOPE_VALUES = "data.scope.tenant.values";
}
