package cn.kankancloud.jbp.mbp.query;

import cn.kankancloud.jbp.core.PagedData;
import cn.kankancloud.jbp.core.exception.BizException;
import cn.kankancloud.jbp.core.query.PageQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class PageQueryBuilder<E, R> {

    private PageQuery pageQuery;
    private final Class<E> entityClass;
    private final Class<R> resultClass;

    private BaseMapper<E> mapper;

    private Function<E, R> projector;
    private Consumer<QueryWrapper<E>> queryCustomizer;

    private PageQueryBuilder(Class<E> eClass, Class<R> rClass) {
        this.entityClass = eClass;
        this.resultClass = rClass;
    }

    public static <E> PageQueryBuilder<E, E> builder(@NonNull Class<E> entityClass) {
        return new PageQueryBuilder<>(entityClass, entityClass);
    }

    public static <E, R> PageQueryBuilder<E, R> builder(@NonNull Class<E> entityClass, @NonNull Class<R> resultClass) {
        return new PageQueryBuilder<>(entityClass, resultClass);
    }

    public PageQueryBuilder<E, R> pageQuery(PageQuery pageQuery) {
        this.pageQuery = pageQuery;
        return this;
    }

    public PageQueryBuilder<E, R> mapper(BaseMapper<E> mapper) {
        this.mapper = mapper;
        return this;
    }

    public PageQueryBuilder<E, R> project(Function<E, R> converter) {
        this.projector = converter;
        return this;
    }

    public PageQueryBuilder<E, R> customizeQuery(Consumer<QueryWrapper<E>> queryCustomizer) {
        this.queryCustomizer = queryCustomizer;
        return this;
    }

    public PagedData<R> selectPage(BiFunction<IPage<E>, QueryWrapper<E>, IPage<R>> pager) {
        IPage<E> pageCondition = QueryUtil.buildPage(pageQuery);
        QueryWrapper<E> query = QueryUtil.createQuery(entityClass, pageQuery);
        IPage<R> apply = pager.apply(pageCondition, query);

        return new PagedData<>(apply.getRecords(), apply.getTotal(), apply.getCurrent());
    }

    public PagedData<R> selectPage() {
        IPage<E> pageCondition = QueryUtil.buildPage(pageQuery);
        QueryWrapper<E> query = QueryUtil.createQuery(entityClass, pageQuery);

        if (queryCustomizer != null) {
            queryCustomizer.accept(query);
        }

        // 重写查询的select部分
        if (resultClass != null && resultClass != entityClass) {
            query = QueryUtil.rewriteSelect(query, resultClass);
        }

        pageCondition = mapper.selectPage(pageCondition, query);

        List<R> projectedRecords = pageCondition.getRecords().stream()
                .map(f -> {

                    if (entityClass == null || resultClass == null) {
                        throw new IllegalArgumentException("entityClass or resultClass is invalid");
                    }

                    if (resultClass == entityClass) {
                        return (R) f;
                    }

                    if (projector != null) {
                        return projector.apply(f);
                    }

                    throw new BizException("can not cast {} to {}", entityClass.getSimpleName(), resultClass.getSimpleName());
                }).toList();

        return new PagedData<>(projectedRecords, pageCondition.getTotal(), pageCondition.getCurrent());
    }

}
