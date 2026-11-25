package dev.triumphteam.cmd.core.annotations;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flag annotation. Contains all the "data" for the flag.
 * To be used inside the {@link CommandFlags} annotation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(CommandFlags.class)
public @interface Flag {

    /**
     * Small flag identifier. Isn't required, as long as either flag or long flag has values.
     * Flags must not have spaces.
     *
     * @return The flag's main identifier.
     */
    @NonNull String flag() default "";

    /**
     * Long flag identifier. Isn't required either, as long as either flag or long flag has values.
     * Flags must not have spaces.
     *
     * @return The flag's long-identifier.
     */
    @NonNull String longFlag() default "";

    /**
     * Define if the flag should have an argument, and it's type.
     * By default, it uses void, which means no argument is needed.
     *
     * @return The argument type.
     */
    @NonNull Class<?> argument() default void.class;

    /**
     * The value for the {@link SuggestionKey} of a registered suggestion.
     *
     * @return The string key of a suggestion.
     */
    @NonNull String suggestion() default "";

    /**
     * @return The flag's description.
     */
    @NonNull String description() default "";
}