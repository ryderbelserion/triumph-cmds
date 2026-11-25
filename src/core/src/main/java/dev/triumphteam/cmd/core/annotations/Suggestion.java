package dev.triumphteam.cmd.core.annotations;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets which suggestion to use for specific arguments.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Repeatable(Suggestions.class)
public @interface Suggestion {

    /**
     * @return The {@link SuggestionKey} value to be used internally.
     */
    @NonNull String value();

    /**
     * Provides additional optional data or metadata associated with the suggestion.
     *
     * @return An additional string to complement the suggestion.
     */
    @NonNull String extra() default "";

    /**
     * Specifies which suggestion method should be used for argument suggestions.
     *
     * @return The default suggestion method, which is {@link SuggestionMethod#STARTS_WITH}.
     */
    @NonNull SuggestionMethod method() default SuggestionMethod.STARTS_WITH;
}