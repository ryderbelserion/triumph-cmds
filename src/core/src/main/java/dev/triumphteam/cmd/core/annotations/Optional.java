package dev.triumphteam.cmd.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the argument as optional.
 * <p>
 * Attention: If {@link Optional#defaultValue()} is not set, the argument will be nullable.
 * If it is set, the argument will be non-nullable.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Optional {

    /**
     * Specifies the default value for the annotated argument.
     *
     * @return The default value as a string. If not specified, return an empty string which will be treated as null.
     */
    String defaultValue() default "";
}