package cn.kankancloud.jbp.mbp.scope;

import cn.kankancloud.jbp.mbp.persistence.EventPo;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import cn.kankancloud.jbp.core.util.tuple.KeyValue;
import cn.kankancloud.jbp.mbp.HintType;
import cn.kankancloud.jbp.mbp.InterceptorIgnoreHint;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据范围查询插件
 */
public class DefaultDataScopeInterceptor extends DataScopeInterceptor {

    private final DataScopeProperties properties;
    private final DataScopeProvider dataScopeProvider;

    public DefaultDataScopeInterceptor(DataScopeProperties properties, DataScopeProvider dataScopeProvider) {
        super(properties.isIgnoreNonExistField());
        this.properties = properties;
        this.dataScopeProvider = dataScopeProvider;
    }

    @Override
    public boolean support(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler<?> resultHandler, BoundSql boundSql) throws SQLException {
        if (!properties.isEnable()) {
            return false;
        }

        // 查找mapper方法上的注解 DisableDataScope
        DisableDataScope disableDataScope = findAnnotation(ms);
        if (disableDataScope != null && disableDataScope.value() == DataScopeType.TENANT_SCOPE) { // NOSONAR
            return false;
        }

        return true;
    }

    @Override
    public ExpressionList getScopeValues() {
        KeyValue<DataScope, List<String>> scopeValues = dataScopeProvider.getScopeValues();
        List<Expression> list = new ArrayList<>();

        if (scopeValues == null || CollectionUtils.isEmpty(scopeValues.getValue())) {
            return new ExpressionList(list);
        }

        if (scopeValues.getKey() == DataScope.ALL) {
            return new AllScopeExpressionList();
        }

        for (String scope : scopeValues.getValue()) {
            list.add(new StringValue(scope));
        }

        return new ExpressionList(list);
    }

    @Override
    public String getScopeColumn() {
        return properties.getColumn();
    }

    @Override
    public boolean ignoreTable(String tableName) {
        if (SqlHelper.table(EventPo.class).getTableName().equals(tableName)) {
            return true;
        }

        // 开启hint, 关闭跨租户查询控制
        if (InterceptorIgnoreHint.useHint(HintType.DATA_SCOPE, tableName)) {
            return true;
        }

        return ObjectUtils.isNotEmpty(properties.getIgnoreTables()) && properties.getIgnoreTables().contains(tableName);
    }
}
