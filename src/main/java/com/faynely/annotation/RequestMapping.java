package com.faynely.annotation;

import java.lang.annotation.*;

/**
 * @author NickFayne 2018-05-14 22:56
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value() default "";
}
