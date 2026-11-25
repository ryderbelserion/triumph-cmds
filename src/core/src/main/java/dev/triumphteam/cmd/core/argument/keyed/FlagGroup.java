package dev.triumphteam.cmd.core.argument.keyed;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basically a holder that contains all the necessary flags for the command.
 */
final class FlagGroup implements ArgumentGroup<Flag> {

    private final Map<String, Flag> flags = new HashMap<>();
    private final Map<String, Flag> longFlags = new HashMap<>();

    private final Map<String, Flag> allFlags = new HashMap<>();

    public FlagGroup(@NonNull final List<Flag> flags) {
        flags.forEach(this::addArgument);
    }

    public void addArgument(@NonNull final Flag argument) {
        final String key = argument.getKey();

        final String longFlag = argument.getLongFlag();

        if (longFlag != null) {
            this.allFlags.put("--" + longFlag, argument);
            this.longFlags.put(longFlag, argument);
        }

        this.allFlags.put("-" + key, argument);
        this.flags.put(key, argument);
    }

    @Override
    public @NonNull Set<String> getAllNames() {
        return this.allFlags.keySet();
    }

    @Override
    public boolean isEmpty() {
        return this.flags.isEmpty() && this.longFlags.isEmpty();
    }

    @Override
    public @Nullable Flag matchExact(@NonNull final String token) {
        final String stripped = stripLeadingHyphens(token);

        final Flag flag = this.flags.get(stripped);

        return flag != null ? flag : this.longFlags.get(stripped);
    }

    @Override
    public @Nullable Flag matchPartialSingle(@NonNull final String token) {
        return null;
    }

    @Override
    public @NonNull Set<Flag> getAll() {
        return new HashSet<>(this.flags.values());
    }

    /**
     * Strips the hyphens from the token.
     *
     * @param token The flag token.
     * @return The flag token without hyphens.
     */
    private @NonNull String stripLeadingHyphens(@NonNull final String token) {
        if (token.startsWith("--")) return token.substring(2);

        if (token.startsWith("-")) return token.substring(1);

        return token;
    }

    @Override
    public String toString() {
        return "FlagGroup{" +
                "allFlags=" + this.allFlags +
                '}';
    }
}