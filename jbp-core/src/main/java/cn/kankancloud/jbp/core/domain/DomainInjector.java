package cn.kankancloud.jbp.core.domain;

import java.lang.annotation.*;

/**
 * 标识是Domain组件，目的是为了保持domain的纯净，不依赖于spring框架
 * 类似于 @Service, @Component, @Repository 的功能
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DomainInjector {
}
