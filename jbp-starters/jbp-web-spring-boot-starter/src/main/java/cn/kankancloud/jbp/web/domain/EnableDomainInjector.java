package cn.kankancloud.jbp.web.domain;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({DomainInjectorRegistrar.class})
public @interface EnableDomainInjector {
    String[] basePackages() default {};
}
