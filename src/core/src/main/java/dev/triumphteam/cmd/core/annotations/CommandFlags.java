package dev.triumphteam.cmd.core.annotations;

import dev.triumphteam.cmd.core.argument.keyed.FlagKey;
import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for declaring all the {@link Flag}s needed for the command.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandFlags {

    /**
     * List with all the flags that'll be needed by the command.
     *
     * @return Array of flags.
     */
    @NonNull Flag[] value() default {};

    /**
     * key to use flags from the registry instead of method declared.
     *
     * @return The {@link FlagKey} value.
     */
    @NonNull String key() default "";
}