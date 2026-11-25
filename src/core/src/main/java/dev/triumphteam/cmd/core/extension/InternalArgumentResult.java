package dev.triumphteam.cmd.core.extension;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import org.jspecify.annotations.NonNull;
import java.util.function.BiFunction;

public interface InternalArgumentResult {

    record Valid(Object value) implements InternalArgumentResult {
        public Valid(@NonNull final Object value) {
            this.value = value;
        }
    }

    record Invalid(BiFunction<@NonNull CommandMeta, @NonNull String, @NonNull InvalidArgumentContext> fail) implements InternalArgumentResult {
        public Invalid(@NonNull final BiFunction<@NonNull CommandMeta, @NonNull String, @NonNull InvalidArgumentContext> fail) {
            this.fail = fail;
        }
    }
}