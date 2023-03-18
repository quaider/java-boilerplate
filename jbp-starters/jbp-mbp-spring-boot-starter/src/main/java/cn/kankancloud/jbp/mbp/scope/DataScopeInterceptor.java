package cn.kankancloud.jbp.mbp.scope;

import cn.kankancloud.jbp.mbp.utils.ServiceProvider;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import cn.kankancloud.jbp.core.util.StrUtil;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 实现参考 TenantInterceptor
 */
public abstract class DataScopeInterceptor extends JsqlParserSupport implements InnerInterceptor, Ordered {

    /**
     * 获取范围值，注意该方法应该会被多次调用，务必轻量处理(如 请求上下文、线程上下文、内存缓存)
     */
    public abstract ExpressionList getScopeValues();

    public abstract String getScopeColumn();

    private boolean ignoreTableWithNoSuchField = false;

    private final ConcurrentMap<String, DisableDataScope> disabledDataScopeMap = new ConcurrentHashMap<>(8);

    protected DataScopeInterceptor(boolean ignoreTableWithNoSuchField) {
        this.ignoreTableWithNoSuchField = ignoreTableWithNoSuchField;
    }

    /**
     * 是否应用插件
     */
    @SuppressWarnings("all")
    protected boolean support(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler<?> resultHandler, BoundSql boundSql) throws SQLException {
        return true;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {

        if (SqlCommandType.SELECT != ms.getSqlCommandType() || StatementType.CALLABLE == ms.getStatementType()) {
            return;
        }

        // 查找mapper方法上的注解 DisableDataScope
        DisableDataScope disableDataScope = findAnnotation(ms);
        if (disableDataScope != null && disableDataScope.value() == DataScopeType.ALL) {
            return;
        }

        if (StringUtils.isEmpty(getScopeColumn())) {
            return;
        }

        if (!support(executor, ms, parameter, rowBounds, resultHandler, boundSql)) {
            return;
        }

        // 执行数据权限的过滤
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        // parserSingle 内部会调用 processSelect() 方法
        mpBs.sql(parserSingle(mpBs.sql(), null));
    }

    /**
     * parserSingle 和 parserMulti 内部会调用 processSelect
     */
    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        processSelectBody(select.getSelectBody());
        List<WithItem> withItemsList = select.getWithItemsList();
        if (!CollectionUtils.isEmpty(withItemsList)) {
            withItemsList.forEach(this::processSelectBody);
        }
    }

    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    protected void processSelectBody(SelectBody selectBody) {
        if (selectBody == null) {
            return;
        }

        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            processSelectBody(withItem.getSubSelect().getSelectBody());
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            List<SelectBody> selectBodyList = operationList.getSelects();
            if (CollectionUtils.isNotEmpty(selectBodyList)) {
                selectBodyList.forEach(this::processSelectBody);
            }
        }
    }

    @Override
    protected void processInsert(Insert insert, int index, String sql, Object obj) {
        // do nothing
    }

    /**
     * update 语句处理
     */
    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        // do nothing
    }

    /**
     * delete 语句处理
     */
    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {
        // do nothing
    }

    /**
     * 处理 PlainSelect
     */
    protected void processPlainSelect(PlainSelect plainSelect) {

        List<SelectItem> selectItems = plainSelect.getSelectItems();
        if (CollectionUtils.isNotEmpty(selectItems)) {
            selectItems.forEach(this::processSelectItem);
        }

        // 处理 where 中的子查询
        Expression where = plainSelect.getWhere();
        processWhereSubSelect(where);

        // 处理 fromItem
        FromItem fromItem = plainSelect.getFromItem();
        List<Table> list = processFromItem(fromItem);
        List<Table> mainTables = new ArrayList<>(list);

        // 处理 join
        List<Join> joins = plainSelect.getJoins();
        if (CollectionUtils.isNotEmpty(joins)) {
            mainTables = processJoins(mainTables, joins);
        }

        // 当有 mainTable 时，进行 where 条件追加
        if (CollectionUtils.isNotEmpty(mainTables)) {
            plainSelect.setWhere(builderExpression(where, mainTables));
        }
    }

    private List<Table> processFromItem(FromItem fromItem) {
        // 处理括号括起来的表达式
        while (fromItem instanceof ParenthesisFromItem) {
            fromItem = ((ParenthesisFromItem) fromItem).getFromItem();
        }

        List<Table> mainTables = new ArrayList<>();
        // 无 join 时的处理逻辑
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            mainTables.add(fromTable);
        } else if (fromItem instanceof SubJoin) {
            // SubJoin 类型则还需要添加上 where 条件
            List<Table> tables = processSubJoin((SubJoin) fromItem);
            mainTables.addAll(tables);
        } else {
            // 处理下 fromItem
            processOtherFromItem(fromItem);
        }
        return mainTables;
    }

    /**
     * 处理where条件内的子查询
     * <p>
     * 支持如下:
     * 1. in
     * 2. =
     * 3. >
     * 4. <
     * 5. >=
     * 6. <=
     * 7. <>
     * 8. EXISTS
     * 9. NOT EXISTS
     * <p>
     * 前提条件:
     * 1. 子查询必须放在小括号中
     * 2. 子查询一般放在比较操作符的右边
     *
     * @param where where 条件
     */
    protected void processWhereSubSelect(Expression where) {
        if (where == null) {
            return;
        }

        if (where instanceof FromItem) {
            processOtherFromItem((FromItem) where);
            return;
        }

        if (where.toString().indexOf("SELECT") <= 0) {
            return;
        }

        // 有子查询
        if (where instanceof BinaryExpression) {
            // 比较符号 , and , or , 等等
            BinaryExpression expression = (BinaryExpression) where;
            processWhereSubSelect(expression.getLeftExpression());
            processWhereSubSelect(expression.getRightExpression());
        } else if (where instanceof InExpression) {
            // in
            InExpression expression = (InExpression) where;
            Expression inExpression = expression.getRightExpression();
            if (inExpression instanceof SubSelect) {
                processSelectBody(((SubSelect) inExpression).getSelectBody());
            }
        } else if (where instanceof ExistsExpression) {
            // exists
            ExistsExpression expression = (ExistsExpression) where;
            processWhereSubSelect(expression.getRightExpression());
        } else if (where instanceof NotExpression) {
            // not exists
            NotExpression expression = (NotExpression) where;
            processWhereSubSelect(expression.getExpression());
        } else if (where instanceof Parenthesis) {
            Parenthesis expression = (Parenthesis) where;
            processWhereSubSelect(expression.getExpression());
        }
    }

    protected void processSelectItem(SelectItem selectItem) {
        if (selectItem instanceof SelectExpressionItem) {
            SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
            if (selectExpressionItem.getExpression() instanceof SubSelect) {
                processSelectBody(((SubSelect) selectExpressionItem.getExpression()).getSelectBody());
            } else if (selectExpressionItem.getExpression() instanceof Function) {
                processFunction((Function) selectExpressionItem.getExpression());
            }
        }
    }

    /**
     * 处理函数
     * <p>支持: 1. select fun(args..) 2. select fun1(fun2(args..),args..)<p>
     * <p> fixed gitee pulls/141</p>
     */
    protected void processFunction(Function function) {
        ExpressionList parameters = function.getParameters();
        if (parameters != null) {
            parameters.getExpressions().forEach(expression -> {
                if (expression instanceof SubSelect) {
                    processSelectBody(((SubSelect) expression).getSelectBody());
                } else if (expression instanceof Function) {
                    processFunction((Function) expression);
                }
            });
        }
    }

    /**
     * 处理子查询等
     */
    protected void processOtherFromItem(FromItem fromItem) {
        // 去除括号
        while (fromItem instanceof ParenthesisFromItem) {
            fromItem = ((ParenthesisFromItem) fromItem).getFromItem();
        }

        if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {
            logger.debug("Perform a subQuery, if you do not give us feedback");
        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }
        }
    }

    /**
     * 处理 sub join
     *
     * @param subJoin subJoin
     * @return Table subJoin 中的主表
     */
    private List<Table> processSubJoin(SubJoin subJoin) {
        List<Table> mainTables = new ArrayList<>();
        if (subJoin.getJoinList() != null) {
            List<Table> list = processFromItem(subJoin.getLeft());
            mainTables.addAll(list);
            mainTables = processJoins(mainTables, subJoin.getJoinList());
        }
        return mainTables;
    }

    /**
     * 处理 joins
     *
     * @param mainTables 可以为 null
     * @param joins      join 集合
     * @return List<Table> 右连接查询的 Table 列表
     */
    @SuppressWarnings("all")
    private List<Table> processJoins(List<Table> mainTables, List<Join> joins) {
        // join 表达式中最终的主表
        Table mainTable = null;
        // 当前 join 的左表
        Table leftTable = null;

        mainTables = mainTables == null ? new ArrayList<>() : mainTables;
        if (mainTables.size() == 1) {
            mainTable = mainTables.get(0);
            leftTable = mainTable;
        }

        // 对于 on 表达式写在最后的 join，需要记录下前面多个 on 的表名
        Deque<List<Table>> onTableDeque = new LinkedList<>();
        for (Join join : joins) {
            // 处理 on 表达式
            FromItem joinItem = join.getRightItem();

            // 获取当前 join 的表，subJoint 可以看作是一张表
            List<Table> joinTables = null;
            if (joinItem instanceof Table) {
                joinTables = new ArrayList<>();
                joinTables.add((Table) joinItem);
            } else if (joinItem instanceof SubJoin) {
                joinTables = processSubJoin((SubJoin) joinItem);
            }

            if (joinTables == null) {
                processOtherFromItem(joinItem);
                return mainTables;
            }

            // 如果是隐式内连接
            if (join.isSimple()) {
                mainTables.addAll(joinTables);
                continue;
            }

            // 当前表是否忽略
            Table joinTable = joinTables.get(0);

            List<Table> onTables = null;
            // 如果不要忽略，且是右连接，则记录下当前表
            if (join.isRight()) {
                mainTable = joinTable;
                if (leftTable != null) {
                    onTables = Collections.singletonList(leftTable);
                }
            } else if (join.isLeft()) {
                onTables = Collections.singletonList(joinTable);
            } else if (join.isInner()) {
                if (mainTable == null) {
                    onTables = Collections.singletonList(joinTable);
                } else {
                    onTables = Arrays.asList(mainTable, joinTable);
                }
                mainTable = null;
            }

            mainTables = new ArrayList<>();
            if (mainTable != null) {
                mainTables.add(mainTable);
            }

            // 获取 join 尾缀的 on 表达式列表
            Collection<Expression> originOnExpressions = join.getOnExpressions();
            // 正常 join on 表达式只有一个，立刻处理
            if (originOnExpressions.size() == 1 && onTables != null) {
                List<Expression> onExpressions = new LinkedList<>();
                onExpressions.add(builderExpression(originOnExpressions.iterator().next(), onTables));
                join.setOnExpressions(onExpressions);
                leftTable = joinTable;
                continue;
            }

            // 表名压栈，忽略的表压入 null，以便后续不处理
            onTableDeque.push(onTables);

            // 尾缀多个 on 表达式的时候统一处理
            if (originOnExpressions.size() > 1) {
                Collection<Expression> onExpressions = new LinkedList<>();
                for (Expression originOnExpression : originOnExpressions) {
                    List<Table> currentTableList = onTableDeque.poll();
                    if (CollectionUtils.isEmpty(currentTableList)) {
                        onExpressions.add(originOnExpression);
                    } else {
                        onExpressions.add(builderExpression(originOnExpression, currentTableList));
                    }
                }
                join.setOnExpressions(onExpressions);
            }

            leftTable = joinTable;
        }

        return mainTables;
    }

    /**
     * 处理条件
     */
    protected Expression builderExpression(Expression currentExpression, List<Table> tables) {
        // 没有表需要处理直接返回
        if (CollectionUtils.isEmpty(tables)) {
            return currentExpression;
        }

        // 范围值
        final ExpressionList scopeValues = getScopeValues();

        // 全部范围，无需追加条件
        if (scopeValues instanceof AllScopeExpressionList) {
            return currentExpression;
        }

        // 构造每张表的条件
        List<Expression> inExps = tables.stream()
                .filter(f -> !this.ignoreTable(f.getName()))
                .filter(this::ignoreNonExistField)
                .map(item -> {
                    int size = scopeValues.getExpressions().size();
                    if (size == 1) {
                        return new EqualsTo(getAliasColumn(item), scopeValues.getExpressions().get(0));
                    }

                    if (size > 1) {
                        return new InExpression(getAliasColumn(item), scopeValues);
                    }

                    // where 1 = 0
                    return new EqualsTo(new Column("1"), new LongValue(0));
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(inExps)) {
            return currentExpression;
        }

        // 注入的表达式
        Expression injectExpression = inExps.get(0);
        // 如果有多表，则用 and 连接
        if (inExps.size() > 1) {
            for (int i = 1; i < inExps.size(); i++) {
                injectExpression = new AndExpression(injectExpression, inExps.get(i));
            }
        }

        if (currentExpression == null) {
            return injectExpression;
        }

        if (currentExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(currentExpression), injectExpression);
        } else {
            return new AndExpression(currentExpression, injectExpression);
        }
    }

    private boolean ignoreNonExistField(Table table) {
        if (!ignoreTableWithNoSuchField) {
            return true;
        }

        return TableInfoHelper.getTableInfo(table.getName()).getFieldList()
                .stream()
                .anyMatch(field -> field.getColumn().equals(getScopeColumn()));
    }

    public abstract boolean ignoreTable(String tableName);

    /**
     * 租户字段别名设置
     * <p>tenantId 或 tableAlias.tenantId</p>
     *
     * @param table 表对象
     * @return 字段
     */
    protected Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        // 为了兼容隐式内连接，没有别名时条件就需要加上表名
        if (table.getAlias() != null) {
            column.append(table.getAlias().getName());
        } else {
            column.append(table.getName());
        }
        column.append(StringPool.DOT).append(getScopeColumn());
        return new Column(column.toString());
    }

    /**
     * 获取数据权限注解信息
     *
     * @param mappedStatement mappedStatement
     * @return DataAuth
     */
    protected DisableDataScope findAnnotation(MappedStatement mappedStatement) {
        String id = mappedStatement.getId();
        return disabledDataScopeMap.computeIfAbsent(id, key -> {
            String className = key.substring(0, key.lastIndexOf(StringPool.DOT));
            String mapperBean = StrUtil.lowerFirst(ClassUtils.getShortName(className));
            Object mapper = ServiceProvider.getService(mapperBean);
            String methodName = key.substring(key.lastIndexOf(StringPool.DOT) + 1);

            Class<?>[] interfaces = ClassUtils.getAllInterfaces(mapper);
            for (Class<?> mapperInterface : interfaces) {
                for (Method method : mapperInterface.getDeclaredMethods()) {
                    if (methodName.equals(method.getName()) && method.isAnnotationPresent(DisableDataScope.class)) {
                        return method.getAnnotation(DisableDataScope.class);
                    }
                }
            }

            return null;
        });
    }
}
