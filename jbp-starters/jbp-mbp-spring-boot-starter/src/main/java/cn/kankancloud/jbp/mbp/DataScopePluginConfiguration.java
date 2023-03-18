package cn.kankancloud.jbp.mbp;

import cn.kankancloud.jbp.mbp.scope.DataScopeProperties;
import cn.kankancloud.jbp.mbp.scope.DataScopeProvider;
import cn.kankancloud.jbp.mbp.scope.DefaultDataScopeInterceptor;
import cn.kankancloud.jbp.mbp.scope.DefaultDataScopeProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ConditionalOnProperty(prefix = "mybatis-plus.plugin.scope.data", name = "enable", havingValue = "true")
@EnableConfigurationProperties(DataScopeProperties.class)
public class DataScopePluginConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DataScopeProvider dataScopeProvider() {
        return new DefaultDataScopeProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultDataScopeInterceptor dataScopeInterceptor(
            DataScopeProperties dataScopeProperties,
            DataScopeProvider dataScopeProvider
    ) {
        return new DefaultDataScopeInterceptor(dataScopeProperties, dataScopeProvider);
    }

}
