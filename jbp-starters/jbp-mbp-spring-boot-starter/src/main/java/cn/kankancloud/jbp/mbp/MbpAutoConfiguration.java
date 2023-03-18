package cn.kankancloud.jbp.mbp;

import cn.kankancloud.jbp.mbp.audit.AuditMetaObjectHandler;
import cn.kankancloud.jbp.mbp.query.PaginationInterceptor;
import cn.kankancloud.jbp.mbp.query.QueryInterceptor;
import cn.kankancloud.jbp.mbp.scope.DataScopeInterceptor;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.apache.commons.lang3.ObjectUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Objects;

@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@MapperScans(
        value = {
                @MapperScan(basePackages = {"cn.kankancloud.jbp.mbp.persistence"})
        }
)
@Import(DataScopePluginConfiguration.class)
public class MbpAutoConfiguration {

    @Bean
    public AuditMetaObjectHandler auditMetaObjectHandler() {
        return new AuditMetaObjectHandler();
    }

    /**
     * mybatis-plus 拦截器集合
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(
            ObjectProvider<QueryInterceptor[]> queryInterceptors,
            ObjectProvider<DataScopeInterceptor[]> dataScopeInterceptors,
            ObjectProvider<TenantLineInnerInterceptor> tenantLineInnerInterceptor) {

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 配置租户拦截器
        if (tenantLineInnerInterceptor.getIfAvailable() != null) {
            interceptor.addInnerInterceptor(tenantLineInnerInterceptor.getIfAvailable());
        }

        // 数据范围拦截器
        DataScopeInterceptor[] dataScopeInterceptorArray = dataScopeInterceptors.getIfAvailable();
        if (Objects.nonNull(dataScopeInterceptorArray) && ObjectUtils.isNotEmpty(dataScopeInterceptorArray)) {
            for (DataScopeInterceptor dataScopeInterceptor : dataScopeInterceptorArray) {
                interceptor.addInnerInterceptor(dataScopeInterceptor);
            }
        }

        // 配置分页拦截器
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        // 配置自定义查询拦截器
        QueryInterceptor[] queryInterceptorArray = queryInterceptors.getIfAvailable();

        if (Objects.nonNull(queryInterceptorArray) && ObjectUtils.isNotEmpty(queryInterceptorArray)) {
            AnnotationAwareOrderComparator.sort(queryInterceptorArray);
            paginationInterceptor.setQueryInterceptors(queryInterceptorArray);
        }

        // 最大每页500条
        paginationInterceptor.setMaxLimit(500L);
        paginationInterceptor.setOverflow(false);

        interceptor.addInnerInterceptor(paginationInterceptor);

        return interceptor;
    }

}
