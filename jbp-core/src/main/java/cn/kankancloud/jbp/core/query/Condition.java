package cn.kankancloud.jbp.core.query;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Tag(name = "查询字段实体")
public class Condition implements Serializable {

    @Schema(description = "运算符：支持EQ|GT|GTE|LT|LTE|LK|LLK|RLK|IN|BTW")
    private String operator;

    @Schema(description = "字段名")
    private String field;

    @Schema(description = "字段值：支持基本类型和对应的数组类型")
    private transient Object value;

    public Condition(String fieldName, Object fieldValue, String operator) {
        if (StringUtils.isEmpty(fieldName)) {
            throw new IllegalArgumentException("动态查询的字段名不能为空");
        }

        this.operator = StringUtils.isEmpty(operator) ? QueryOperator.EQUAL.operator : operator;
        this.field = fieldName;
        this.value = fieldValue;
    }

    /**
     * 根据字符串条件构造查询字段实体对象
     *
     * @param conditionStr 查询条件(field|operator|value)
     */
    public Condition(String conditionStr) {
        if (!checkFilter(conditionStr)) {
            throw new IllegalArgumentException(conditionStr + "查询字段设置有误！");
        }

        String[] arr = conditionStr.split("\\|");
        if (arr.length <= 2) {
            throw new IllegalArgumentException(conditionStr + "查询字段设置有误！");
        }

        this.field = arr[0];
        this.operator = StringUtils.isEmpty(arr[1]) ? QueryOperator.EQUAL.operator : arr[1];
        this.value = arr[2];
    }

    private boolean checkFilter(String columnFilter) {
        return columnFilter.matches("^\\S+$");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Condition field1 = (Condition) o;

        return new EqualsBuilder().append(field, field1.field).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(field).toHashCode();
    }

}
