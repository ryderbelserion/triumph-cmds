package dev.triumphteam.cmd.core.annotations;

import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to target a method and set which {@link Arguments} list will be used.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NamedArguments {

    /**
     * @return The {@link ArgumentKey} value to be used internally.
     */
    @NonNull String value();
}