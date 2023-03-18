package cn.kankancloud.jbp.mbp;

import cn.kankancloud.jbp.mbp.tenant.TenantHandler;
import cn.kankancloud.jbp.mbp.tenant.TenantInterceptor;
import cn.kankancloud.jbp.mbp.tenant.TenantProperties;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import cn.kankancloud.jbp.core.abstraction.ITenantIdGen;
import cn.kankancloud.jbp.core.util.StrUtil;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ConditionalOnProperty(prefix = "mybatis-plus.plugin.tenant", name = "enable", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({TenantProperties.class})
public class TenantPluginConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public ITenantIdGen tenantIdGen() {
        return () -> StrUtil.random(6, StrUtil.RandomType.ALL);
    }

    /**
     * 自定义多租户处理器
     *
     * @param tenantProperties 多租户配置类
     * @return TenantHandler
     */
    @Bean
    @Primary
    public TenantLineHandler tenantHandler(TenantProperties tenantProperties) {
        return new TenantHandler(tenantProperties);
    }

    /**
     * 自定义租户拦截器
     *
     * @param tenantHandler 多租户处理器
     * @return BladeTenantInterceptor
     */
    @Bean
    @Primary
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantLineHandler tenantHandler) {
        TenantInterceptor tenantInterceptor = new TenantInterceptor();
        tenantInterceptor.setTenantLineHandler(tenantHandler);

        return tenantInterceptor;
    }
}
