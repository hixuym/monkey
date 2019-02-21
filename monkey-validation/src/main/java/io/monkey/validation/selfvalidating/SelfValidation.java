package io.monkey.validation.selfvalidating;

import java.lang.annotation.*;

/**
 * This method, if used in conjunction with
 * {@link io.monkey.validation.selfvalidating.SelfValidating},
 * will be executed to check if the object itself is valid.
 * For that it requires the signature <code>public void methodName(ViolationCollector)</code>.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited //used by classmate reflection
public @interface SelfValidation {
}
