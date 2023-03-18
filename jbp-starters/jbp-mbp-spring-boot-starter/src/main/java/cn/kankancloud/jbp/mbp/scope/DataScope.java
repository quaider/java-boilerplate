package cn.kankancloud.jbp.mbp.scope;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataScope {
    ALL(1, "全部"),
    CUSTOM(5, "自定义");

    /**
     * 类型
     */
    private final int scope;

    /**
     * 描述
     */
    private final String description;

    public static DataScope of(Integer scope) {
        if (scope == null) {
            return null;
        }

        DataScope[] values = DataScope.values();
        for (DataScope scopeTypeEnum : values) {
            if (scopeTypeEnum.scope == scope) {
                return scopeTypeEnum;
            }
        }

        return null;
    }
}
