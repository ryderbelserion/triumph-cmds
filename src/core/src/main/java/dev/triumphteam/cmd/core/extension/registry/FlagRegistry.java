package dev.triumphteam.cmd.core.extension.registry;

import dev.triumphteam.cmd.core.argument.keyed.FlagKey;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FlagRegistry implements Registry {

    private final Map<FlagKey, List<Flag>> suggestions = new HashMap<>();

    public void register(@NonNull final FlagKey key, @NonNull final List<Flag> flags) {
        this.suggestions.put(key, flags);
    }

    public @Nullable List<Flag> getFlags(@NonNull final FlagKey key) {
        return this.suggestions.get(key);
    }
}