package cn.kankancloud.jbp.core.query;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Tag(name = "查询组件")
public class QueryConditions implements Serializable {

    @Schema(description = "项组合逻辑：支持AND|OR")
    private String logic;

    @Schema(description = "查询条件")
    private List<Condition> conditions;

    @Schema(description = "查询组组合")
    private List<QueryConditions> groups;

    @Schema(description = "查询组组合逻辑：支持AND|OR")
    private String groupLogic;

    public QueryConditions() {
        this.logic = QueryLogic.AND.name();
        this.groups = new ArrayList<>();
        this.conditions = new ArrayList<>();
    }

    public QueryConditions addItems(Condition... conditions) {
        this.conditions.addAll(Arrays.asList(conditions));

        return this;
    }

    public static QueryConditions group(QueryLogic logic, QueryConditions... parameters) {
        QueryConditions tmpl = new QueryConditions();
        tmpl.groups.addAll(Arrays.asList(parameters));

        tmpl.groupLogic = logic.name();
        return tmpl;
    }

    public QueryConditions asGroup(QueryLogic logic, QueryConditions... parameters) {
        if (parameters == null || parameters.length == 0) {
            return this;
        }

        QueryConditions copyTmpl = SerializationUtils.clone(this);

        // 将提供的查询模板组合
        QueryConditions tmpl = group(logic, parameters);

        // 把自己另入组合内
        tmpl.groups.add(copyTmpl);

        // copy
        this.groups = tmpl.groups;
        this.groupLogic = tmpl.groupLogic;

        return this;
    }

    /**
     * 能否构建查询
     */
    public boolean canBuildQuery() {
        return recursiveCanBuildQuery(this);
    }

    /**
     * 递归表达式
     */
    private boolean recursiveCanBuildQuery(QueryConditions parentGroup) {
        boolean isEnabled = false;
        if (ObjectUtils.isNotEmpty(parentGroup.groups)) {
            for (QueryConditions cg : parentGroup.groups) {
                isEnabled = recursiveCanBuildQuery(cg);
                if (isEnabled)
                    break;
            }
        } else {
            isEnabled = parentGroup.checkSelfItem();
        }

        return isEnabled;
    }


    /**
     * 检查组合是否满足创建表达式(递归所有组及单元成员)
     */
    public boolean checkSelfGroup() {
        return !ObjectUtils.isEmpty(this.groups);
    }

    /**
     * 判断单元是否满足创建表达式
     */
    public boolean checkSelfItem() {
        return this.conditions.stream().anyMatch(o -> ObjectUtils.isNotEmpty(o.getValue()));
    }

    public void removeQueryField(String fieldName) {
        recursiveRemoveQueryField(this, fieldName);
    }

    public boolean containQueryField(String fieldName) {
        return ObjectUtils.isNotEmpty(findQueryField(fieldName));
    }

    public List<Condition> findQueryField(String fieldName) {
        return allQueryFields().stream().filter(o -> o.getField().equals(fieldName)).collect(Collectors.toList());
    }

    public List<Condition> allQueryFields() {
        return recursiveFindQueryField(this);
    }

    protected List<Condition> recursiveFindQueryField(QueryConditions group) {
        Set<Condition> set = new HashSet<>();
        if (group.checkSelfGroup()) {
            for (QueryConditions childGroup : group.groups) {
                if (childGroup.checkSelfGroup()) {
                    List<Condition> childList = recursiveFindQueryField(childGroup);
                    set.addAll(childList);
                } else
                    // 判断item有没有组合项，没有的话本身就是一个QueryTmpl对象
                    if (childGroup.checkSelfItem()) {
                        set.addAll(childGroup.conditions);
                    }
            }
        } else
            // 没有组合项时，本身就是Tmpl
            if (group.checkSelfItem()) {
                set.addAll(group.conditions);
            }

        return Lists.newArrayList(set);
    }

    protected void recursiveRemoveQueryField(QueryConditions group, String fieldName) {
        if (!group.checkSelfGroup()) {
            removeItemField(fieldName, group);
        }

        for (QueryConditions childGroup : group.groups) {
            if (childGroup.checkSelfGroup()) {
                recursiveRemoveQueryField(childGroup, fieldName);
            } else {
                // 判断item有没有组合项，没有的话本身就是一个QueryTmpl对象
                removeItemField(fieldName, childGroup);
            }
        }
    }

    private void removeItemField(String fieldName, QueryConditions childGroup) {
        if (!childGroup.checkSelfItem()) {
            return;
        }

        List<Condition> queryList = childGroup.conditions.stream().filter(o -> o.getField().equals(fieldName)).collect(Collectors.toList());
        for (Condition item : queryList) {
            childGroup.conditions.remove(item);
        }
    }

    public QueryLogic getLogic() {
        if (StringUtils.isEmpty(logic)) {
            return QueryLogic.AND;
        }

        return QueryLogic.valueOf(logic);
    }

    public QueryLogic getGroupLogic() {
        if (StringUtils.isEmpty(groupLogic)) {
            return null;
        }

        return QueryLogic.valueOf(groupLogic);
    }
}
