package dev.triumphteam.cmd.core.annotations;

import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requirement annotation. Holds all the requirement's data.
 * To be used inside {@link Requirements}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Repeatable(Requirements.class)
public @interface Requirement {

    /**
     * The requirement key.
     * Required to be registered before used.
     *
     * @return The requirement key.
     */
    @NonNull String value();

    /**
     * The message key will be used to send a custom message if the specified requirement is denied.
     *
     * @return The message key.
     */
    @NonNull String messageKey();

    /**
     * @return If the requirement should be inverted.
     */
    boolean invert() default false;
}