package dev.triumphteam.cmd.core.argument.keyed;

import org.jspecify.annotations.NonNull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

abstract class FlagsContainer implements Arguments {

    private final Map<String, ArgumentValue> flags;

    public FlagsContainer(@NonNull final Map<String, ArgumentValue> flags) {
        this.flags = flags;
    }

    @Override
    public boolean hasFlag(@NonNull final String flag) {
        return this.flags.containsKey(flag);
    }

    @Override
    public @NonNull <T> Optional<T> getFlagValue(@NonNull final String flag, @NonNull final Class<? super T> type) {
        final ArgumentValue flagValue = this.flags.get(flag);

        if (flagValue == null) return Optional.empty();

        if (!(flagValue instanceof SimpleArgumentValue argFlagValue)) return Optional.empty();

        return Optional.ofNullable((T) argFlagValue.getValue());
    }

    @Override
    public @NonNull Optional<String> getFlagValue(@NonNull final String flag) {
        final ArgumentValue flagValue = this.flags.get(flag);

        if (flagValue == null) return Optional.empty();

        if (!(flagValue instanceof SimpleArgumentValue argFlagValue)) return Optional.empty();

        return Optional.of(argFlagValue.getAsString());
    }

    @Override
    public @NonNull Set<String> getAllFlags() {
        return this.flags.keySet();
    }

    @Override
    public boolean hasFlags() {
        return !this.flags.isEmpty();
    }

    @Override
    public String toString() {
        return "FlagsContainer{" +
                "flags=" + this.flags +
                '}';
    }
}