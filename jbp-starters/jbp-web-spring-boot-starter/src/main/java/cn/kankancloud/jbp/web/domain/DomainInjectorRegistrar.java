package cn.kankancloud.jbp.web.domain;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * 加载指定包路径下的自定义注解实例
 */
public class DomainInjectorRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获得注解中的value （获取到指定路径）
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableDomainInjector.class.getName()));

        String[] basePackages = null;
        if (annotationAttributes != null) {
            basePackages = (String[]) annotationAttributes.get("basePackages");
        }

        if (basePackages == null || basePackages.length == 0) {
            basePackages = new String[]{ClassUtils.getPackageName(importingClassMetadata.getClassName())};
        }

        DomainInjectorBeanDefinitionScanner scanner = new DomainInjectorBeanDefinitionScanner(registry, false);
        scanner.setResourceLoader(resourceLoader);

        // 此处过滤的为: 仅注册增加了 @DomainInjector 注解的类，如想实现扫描包下的所有类，则无需注册过滤器
        scanner.registerFilters();
        scanner.doScan(basePackages);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
