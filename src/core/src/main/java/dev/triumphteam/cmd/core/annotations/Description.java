package dev.triumphteam.cmd.core.annotations;

import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The description of an element.
 * Can be used for class, method, or parameter. Aka, command, sub command, or argument.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Inherited
public @interface Description {

    /**
     * @return The description of the element.
     */
    @NonNull String value();
}