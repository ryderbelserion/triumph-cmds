package dev.triumphteam.cmd.core.annotations;

import dev.triumphteam.cmd.core.extension.meta.MetaKey;
import org.jspecify.annotations.NonNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the sub-command to be executed asynchronously.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Async {

    // Async's annotation meta key
    @NonNull MetaKey<Boolean> META_KEY = MetaKey.of("async", boolean.class);
}