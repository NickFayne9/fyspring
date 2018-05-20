package com.faynely.framework.annotation;

import java.lang.annotation.*;

/**
 * @author NickFayne 2018-05-14 22:58
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    String value() default "";
}
