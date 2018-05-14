package com.faynely.annotation;

import java.lang.annotation.*;

/**
 * @author NickFayne 2018-05-14 22:59
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String value() default "";
}
