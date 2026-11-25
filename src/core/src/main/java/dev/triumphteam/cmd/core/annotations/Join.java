package dev.triumphteam.cmd.core.annotations;

import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Join annotation is to be used in a String parameter to join all the arguments into one continuous string.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Join {

    /**
     * Sets the delimiter of the join.
     * By default, it uses space.
     *
     * @return The delimiter of to be used by the join method.
     */
    @NonNull String value() default " ";
}