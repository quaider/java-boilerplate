package cn.kankancloud.jbp.mbp.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.kankancloud.jbp.core.query.PageQuery;
import cn.kankancloud.jbp.core.query.QueryConditions;
import cn.kankancloud.jbp.core.util.CastUtil;
import cn.kankancloud.jbp.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * QueryWrapper 增强
 */
@Slf4j
public class QueryUtil {

    private QueryUtil() {
    }

    public static final Map<String, String> SQL_KEYWORDS = new HashMap<>(); // NOSONAR

    static {
        SQL_KEYWORDS.put("name", "");
        SQL_KEYWORDS.put("key", "");
        SQL_KEYWORDS.put("on", "");
        SQL_KEYWORDS.put("left", "");
        SQL_KEYWORDS.put("right", "");
        SQL_KEYWORDS.put("join", "");
        SQL_KEYWORDS.put("out", "");
        SQL_KEYWORDS.put("outer", "");
        SQL_KEYWORDS.put("select", "");
        SQL_KEYWORDS.put("where", "");
        SQL_KEYWORDS.put("is", "");
        SQL_KEYWORDS.put("exists", "");
        SQL_KEYWORDS.put("not", "");
    }

    public static <T> IPage<T> buildPage(PageQuery query) {
        return buildPage(query, null);
    }

    /**
     * 转化成mybatis plus中的Page
     *
     * @param query 查询条件
     * @param clazz 泛型标识
     * @return IPage
     */
    public static <T> IPage<T> buildPage(PageQuery query, Class<T> clazz) {
        if (clazz != null) {
            // 躲避sonar检查，clazz用于骗过编译器的类型检查
        }

        Page<T> page = new Page<>(CastUtil.toInt(query.getCurrent(), 1), CastUtil.toInt(query.getPageSize(), 10));

        // 处理排序
        query.getSorts().forEach((k, v) -> {
            OrderItem orderItem;
            if ("DESC".equalsIgnoreCase(StrUtil.cleanIdentifier(v))) {
                orderItem = OrderItem.desc(StrUtil.camelCaseToUnderline(k));
            } else {
                orderItem = OrderItem.asc(StrUtil.camelCaseToUnderline(k));
            }

            page.addOrder(orderItem);
        });

        return page;
    }

    /**
     * 构建动态查询条件
     *
     * @param <T> 查询entity
     * @return QueryWrapper
     */
    public static <T> QueryWrapper<T> createDynamicQuery(QueryConditions queryConditions, Class<T> clazz) {
        QueryWrapper<T> qw = new QueryWrapper<>();
        qw.setEntity(BeanUtils.instantiateClass(clazz));

        SqlKeyword.buildCondition(queryConditions, qw);

        return qw;
    }

    /**
     * 构建动态查询条件，查询条件带限制返回条数
     *
     * @param limit 返回的记录条数
     */
    public static <T> QueryWrapper<T> createQueryWithLimited(Class<T> clazz, QueryConditions queryConditions, int limit) {
        if (queryConditions == null) {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            queryWrapper.setEntityClass(clazz);
            return queryWrapper.last("limit " + limit);
        }

        return createDynamicQuery(queryConditions, clazz).last("limit " + limit);
    }

    /**
     * 构建动态查询条件
     *
     * @param clazz        待查询entity类型
     * @param queryConditions 查询条件
     * @param <T>          待查询entity类型
     * @return mybatis 查询条件
     */
    public static <T> QueryWrapper<T> createQuery(Class<T> clazz, QueryConditions queryConditions) {
        if (queryConditions == null) {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            queryWrapper.setEntityClass(clazz);
            return queryWrapper;
        }

        return createDynamicQuery(queryConditions, clazz);
    }

    /**
     * 从分页查询中构建动态查询条件
     *
     * @param clazz     待查询entity类型
     * @param pageQuery 分页查询
     * @param <T>       待查询entity类型
     * @return mybatis 查询条件
     */
    public static <T> QueryWrapper<T> createQuery(Class<T> clazz, PageQuery pageQuery) {
        if (pageQuery == null || pageQuery.getFilter() == null) {
            return createQuery(clazz, (QueryConditions) null);
        }

        return createQuery(clazz, pageQuery.getFilter());
    }

    /**
     * 根据实体对象构建查询条件
     *
     * @param entity 实体
     * @param <T>    类型
     * @return QueryWrapper
     */
    public static <T> QueryWrapper<T> createQuery(T entity) {
        return new QueryWrapper<>(entity);
    }

    /**
     * 获取空的mybatis plus中的QueryWrapper
     *
     * @param <T> 类型
     * @return QueryWrapper
     */
    public static <T> QueryWrapper<T> emptyQuery(Class<T> tClass) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntityClass(tClass);

        return queryWrapper;
    }

    /**
     * 重写 mpb QueryWrapper 中的查询字段
     *
     * @param wrapper           原查询
     * @param searchResultClass 覆盖后的实体类型
     * @param <T>               原查询实体(PO)类型
     * @param <S>               重写后的查询实体类型
     */
    public static <S, T> QueryWrapper<S> rewriteSelect(QueryWrapper<S> wrapper, Class<T> searchResultClass) {
        List<Field> allFields = TableInfoHelper.getAllFields(searchResultClass);
        if (ObjectUtils.isNotEmpty(allFields)) {
            return wrapper.select(allFields.stream().map(f -> StrUtil.camelCaseToUnderline(sqlKeywordHacker(f.getName()))).toArray(String[]::new));
        }

        return wrapper;
    }

    private static String sqlKeywordHacker(String fieldName) {

        if (SQL_KEYWORDS.containsKey(fieldName.toLowerCase())) {
            return String.format("`%s`", fieldName);
        }

        return fieldName;
    }
}
