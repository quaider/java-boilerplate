package cn.kankancloud.jbp.web.springdoc;

import org.springdoc.core.OperationService;
import org.springdoc.core.PropertyResolverUtils;
import org.springdoc.core.ReturnTypeParser;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 自定义返回值解析（统一追加格式）
 */
@Configuration
@ConditionalOnProperty(name = "springdoc.api-docs.common-result", havingValue = "true", matchIfMissing = true)
public class CommonApiResultConfiguration {
    /**
     * 自定义返回值解析（统一追加格式）
     * <a href="https://blog.csdn.net/catcher92/article/details/118926032">自定义返回值解析</a>
     */
    @Bean
    public ReturnTypeParser customReturnTypeParser() {
        return new CommonReturnTypeParser();
    }

    @Bean
    public CommonGenericResponseService exampleGenericResponseService(OperationService operationService, List<ReturnTypeParser> returnTypeParsers, SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils) {
        return new CommonGenericResponseService(operationService, returnTypeParsers, springDocConfigProperties, propertyResolverUtils);
    }
}
