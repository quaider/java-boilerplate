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

    AuthorizeStrategy strategy() default AuthorizeStrategy.URL;

    /**
     * Spring el
     */
    String value() default "";
}
