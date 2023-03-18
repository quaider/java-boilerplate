package cn.kankancloud.jbp.mbp.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.kankancloud.jbp.core.PagedData;
import cn.kankancloud.jbp.core.exception.BizException;
import cn.kankancloud.jbp.core.query.PageQuery;
import lombok.NonNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageQueryBuilder<E, R> {

    private PageQuery pageQuery;
    private final Class<E> clazz;
    private final Class<R> selectClass;

    private BaseMapper<E> mapper;

    private Function<E, R> converter;

    private Consumer<QueryWrapper<E>> queryCustomizer;

    private PageQueryBuilder(Class<E> eClass, Class<R> rClass) {
        this.clazz = eClass;
        this.selectClass = rClass;
    }

    public static <E> PageQueryBuilder<E, E> builder(@NonNull Class<E> eClass) {
        return new PageQueryBuilder<>(eClass, eClass);
    }

    public static <E, R> PageQueryBuilder<E, R> builder(@NonNull Class<E> eClass, @NonNull Class<R> rClass) {
        return new PageQueryBuilder<>(eClass, rClass);
    }

    public PageQueryBuilder<E, R> pageQuery(PageQuery pageQuery) {
        this.pageQuery = pageQuery;
        return this;
    }

    public PageQueryBuilder<E, R> mapper(BaseMapper<E> mapper) {
        this.mapper = mapper;
        return this;
    }

    public PageQueryBuilder<E, R> projectTo(Function<E, R> converter) {
        this.converter = converter;
        return this;
    }

    public PageQueryBuilder<E, R> customizeQuery(Consumer<QueryWrapper<E>> queryCustomizer) {
        this.queryCustomizer = queryCustomizer;
        return this;
    }

    public PagedData<R> selectPage() {
        IPage<E> pageCondition = QueryUtil.buildPage(pageQuery, clazz);
        QueryWrapper<E> query = QueryUtil.createQuery(clazz, pageQuery);

        if (queryCustomizer != null) {
            queryCustomizer.accept(query);
        }

        // 重写查询的select部分
        if (selectClass != null && selectClass != clazz) {
            query = QueryUtil.rewriteSelect(query, selectClass);
        }

        pageCondition = mapper.selectPage(pageCondition, query);

        List<R> projectedRecords = pageCondition.getRecords().stream()
                .map(f -> {

                    if (clazz == null || selectClass == null) {
                        throw new IllegalArgumentException("clazz or selectClass is invalid");
                    }

                    if (selectClass == clazz) {
                        return (R) f;
                    }

                    if (converter != null) {
                        return converter.apply(f);
                    }

                    throw new BizException("can not cast {} to {}", clazz.getSimpleName(), selectClass.getSimpleName());
                }).collect(Collectors.toList());

        return new PagedData<>(projectedRecords, pageCondition.getTotal(), pageCondition.getCurrent());
    }

}
