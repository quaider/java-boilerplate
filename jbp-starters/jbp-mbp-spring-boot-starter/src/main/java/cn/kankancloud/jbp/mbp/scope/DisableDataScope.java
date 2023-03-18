package cn.kankancloud.jbp.mbp.scope;

import java.lang.annotation.*;

/**
 * Mapper方法上加上该注解可禁用数据权限
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DisableDataScope {
    DataScopeType value() default DataScopeType.ALL;
}
