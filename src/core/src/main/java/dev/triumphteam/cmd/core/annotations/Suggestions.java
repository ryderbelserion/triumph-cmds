package dev.triumphteam.cmd.core.annotations;

import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to hold all the command suggestions.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Suggestions {

    /**
     * @return An array of {@link Suggestion}s.
     */
    @NonNull Suggestion[] value();
}