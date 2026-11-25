package dev.triumphteam.cmd.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows specifying the name of the argument instead of just using the parameter name.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ArgName {

    /**
     * The name of the argument.
     * This overrides the parameter name.
     *
     * @return The name of the argument
     */
    String value();
}