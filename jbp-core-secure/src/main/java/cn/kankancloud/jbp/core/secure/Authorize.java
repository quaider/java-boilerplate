package cn.kankancloud.jbp.core.secure;

import java.lang.annotation.*;

/**
 * eg: @Authorize("hasRole('Admin')")
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Authorize {

    /**
     * Spring el
     */
    String value();
}
