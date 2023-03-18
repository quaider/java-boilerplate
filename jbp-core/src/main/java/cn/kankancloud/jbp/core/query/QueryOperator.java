package cn.kankancloud.jbp.core.query;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@AllArgsConstructor
public enum QueryOperator {

    EQUAL("EQ"),
    LIKE("LK"),
    LEFT_LIKE("LLK"),
    RIGHT_LIKE("RLK"),
    GREAT_THAN("GT"),
    GREAT_THAN_EQUAL("GTE"),
    LESS_THAN("LT"),
    LESS_THAN_EQUAL("LTE"),
    IN("IN"),
    BETWEEN("BTW");

    public final String operator;

    public static QueryOperator of(String operator) {
        return ofDefault(operator, null);
    }

    public static QueryOperator ofDefault(String operator, QueryOperator defaultV) {
        if (StringUtils.isBlank(operator)) {
            return defaultV;
        }

        return Arrays.stream(QueryOperator.values()).filter(f -> f.operator.equalsIgnoreCase(operator.trim())).findFirst().orElse(defaultV);
    }

}
