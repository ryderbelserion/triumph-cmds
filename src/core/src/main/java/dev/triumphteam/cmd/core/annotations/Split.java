package dev.triumphteam.cmd.core.annotations;

import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Split annotation allows you to split a string into a list.
 * For example, diamond;stone;iron;gold, into [diamond, stone, iron, gold].
 * The splitting type is safe.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Split {

    /**
     * The delimiter to split the string by.
     *
     * @return The delimiter.
     */
    @NonNull String value() default ",";
}