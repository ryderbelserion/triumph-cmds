package dev.triumphteam.cmd.core.extension.registry;

import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NamedArgumentRegistry implements Registry {

    private final Map<ArgumentKey, List<Argument>> namedArguments = new HashMap<>();

    public void register(@NonNull final ArgumentKey key, @NonNull final List<Argument> arguments) {
        this.namedArguments.put(key, arguments);
    }

    public @Nullable List<Argument> getArguments(@NonNull final ArgumentKey key) {
        return this.namedArguments.get(key);
    }
}