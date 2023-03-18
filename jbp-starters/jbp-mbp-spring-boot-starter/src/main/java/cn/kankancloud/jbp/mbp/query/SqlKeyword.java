package cn.kankancloud.jbp.mbp.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.kankancloud.jbp.core.query.Condition;
import cn.kankancloud.jbp.core.query.QueryConditions;
import cn.kankancloud.jbp.core.query.QueryLogic;
import cn.kankancloud.jbp.core.query.QueryOperator;
import cn.kankancloud.jbp.core.util.StrUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 定义常用的 sql关键字
 */
public class SqlKeyword {

    private SqlKeyword() {
    }

    static void buildCondition(QueryConditions filters, QueryWrapper<?> wrapper) {
        if (!filters.canBuildQuery()) {
            return;
        }

        // filters是否含有group
        if (filters.checkSelfGroup()) {
            buildChildGroup(filters, wrapper);
        } else if (filters.checkSelfItem()) {
            // 没有group时，本身就是Tmpl
            buildChildSelfItem(filters.getLogic(), filters.getConditions(), wrapper);
        }
    }

    private static void buildChildGroup(QueryConditions parent, QueryWrapper<?> queryWrapper) {

        for (QueryConditions childGroup : parent.getGroups()) {

            Consumer<QueryWrapper<?>> consumer = q -> {
                // 子项含有组
                if (childGroup.checkSelfGroup()) {
                    buildCondition(childGroup, q);
                } else if (childGroup.checkSelfItem()) {
                    buildChildSelfItem(childGroup.getLogic(), childGroup.getConditions(), q);
                }
            };

            if (parent.getGroupLogic() == QueryLogic.OR) {
                queryWrapper.or(consumer::accept);
            } else {
                queryWrapper.and(consumer::accept);
            }
        }
    }

    private static void buildChildSelfItem(QueryLogic itemLogic, List<Condition> items, QueryWrapper<?> wrapper) {
        for (Condition field : items) {
            if (ObjectUtils.isEmpty(field.getValue())) {
                continue;
            }

            parseItemOperator(field, wrapper);

            if (itemLogic == QueryLogic.OR) {
                wrapper = wrapper.or();
            }
        }
    }

    private static void parseItemOperator(Condition field, QueryWrapper<?> wrapper) {
        QueryOperator operator = QueryOperator.ofDefault(field.getOperator(), QueryOperator.EQUAL);

        switch (operator) {
            case LIKE:
                wrapper.like(getColumn(field.getField()), field.getValue());
                break;
            case LEFT_LIKE:
                wrapper.likeLeft(getColumn(field.getField()), field.getValue());
                break;
            case RIGHT_LIKE:
                wrapper.likeRight(getColumn(field.getField()), field.getValue());
                break;
            case GREAT_THAN:
                wrapper.gt(getColumn(field.getField()), field.getValue());
                break;
            case GREAT_THAN_EQUAL:
                wrapper.ge(getColumn(field.getField()), field.getValue());
                break;
            case LESS_THAN:
                wrapper.lt(getColumn(field.getField()), field.getValue());
                break;
            case LESS_THAN_EQUAL:
                wrapper.le(getColumn(field.getField()), field.getValue());
                break;
            case IN:
                if (ObjectUtils.isEmpty(field.getValue())) {
                    return;
                }

                Object[] values = parseArrFromObject(field.getValue());
                if (ObjectUtils.isNotEmpty(values)) {
                    wrapper.in(getColumn(field.getField()), values);
                }

                break;
            case BETWEEN:
                if (ObjectUtils.isEmpty(field.getValue())) {
                    return;
                }

                values = parseArrFromObject(field.getValue());
                if (ObjectUtils.isEmpty(values)) {
                    return;
                }

                if (values.length == 1) {
                    wrapper.eq(getColumn(field.getField()), values[0]);
                } else {
                    wrapper.between(getColumn(field.getField()), values[0], values[1]);
                }

                break;
            case EQUAL:
            default:
                wrapper.eq(getColumn(field.getField()), field.getValue());
                break;
        }
    }

    private static Object[] parseArrFromObject(Object value) {
        if (value instanceof String) {
            return ((String) value).split(",");
        }

        if (value.getClass().isArray()) {
            Object[] result = new Object[Array.getLength(value)];
            for (int i = 0; i < result.length; i++) {
                result[i] = Array.get(value, i);
            }

            return result;
        }

        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            return collection.toArray();
        }

        return new Object[0];
    }

    /**
     * 获取数据库字段
     *
     * @param column 字段名
     * @return String
     */
    private static String getColumn(String column) {
        return StrUtil.camelCaseToUnderline(column);
    }
}
