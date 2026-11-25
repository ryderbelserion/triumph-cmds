package dev.triumphteam.cmd.core.annotations;

import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to hold all the command requirements.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Requirements {

    /**
     * A list with all the {@link Requirement} annotations.
     *
     * @return An array of {@link Requirement}s.
     */
    @NonNull Requirement[] value();
}