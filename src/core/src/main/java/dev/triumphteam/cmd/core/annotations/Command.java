package dev.triumphteam.cmd.core.annotations;

import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command annotation, marks the class or method as a command.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface Command {

    /**
     * Command's name.
     * Must not have spaces.
     *
     * @return The command name.
     */
    @NonNull String value() default "";

    /**
     * List with all command aliases.
     * Can be empty but can't have spaces.
     *
     * @return The alias array.
     */
    @NonNull String[] alias() default {};
}