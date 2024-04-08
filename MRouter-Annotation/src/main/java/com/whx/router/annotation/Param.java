package com.whx.router.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(CLASS)
public @interface Param {
    /**
     * Mark param name
     */
    String name() default "";

    /**
     * remark of the field
     */
    String remark() default "";

    /**
     * If marked true, injectCheck() is called to check if the value is empty,
     * which throws a ParamException() type exception.
     */
    boolean required() default false;
}