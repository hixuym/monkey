package io.monkey.inject.advise;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @see ProvidesWithAdvice
 */
@Documented 
@Target(METHOD) 
@Retention(RUNTIME)
public @interface Advises {
    int order() default 1000;
}
