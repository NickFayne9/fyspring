package com.faynely.annotation;

import java.lang.annotation.*;

/**
 * @author NickFayne 2018-05-14 22:59
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String value() default "";
}