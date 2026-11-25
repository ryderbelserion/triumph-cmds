package dev.triumphteam.cmd.core.argument.keyed;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basically a holder that contains all the needed arguments for the command.
 */
final class NamedGroup implements ArgumentGroup<Argument> {

    private final Map<String, Argument> arguments = new HashMap<>();
    private final Map<String, Argument> longArguments = new HashMap<>();

    NamedGroup(@NonNull final List<Argument> arguments) {
        arguments.forEach(this::addArgument);
    }

    public void addArgument(@NonNull final Argument argument) {
        this.arguments.put(argument.getName(), argument);
        this.longArguments.put(argument.getLongName(), argument.asLongNameArgument());
    }

    @Override
    public @NonNull Set<String> getAllNames() {
        return this.arguments.keySet();
    }

    @Override
    public boolean isEmpty() {
        return this.arguments.isEmpty();
    }

    @Override
    public @Nullable Argument matchExact(@NonNull final String token) {
        final Argument argument = this.arguments.get(token);

        if (argument != null) {
            return argument;
        }

        return this.longArguments.get(token);
    }

    @Override
    public @Nullable Argument matchPartialSingle(@NonNull final String token) {
        final List<Argument> arguments = this.arguments.entrySet()
                .stream()
                .filter(it -> it.getKey().startsWith(token))
                .map(Map.Entry::getValue)
                .toList();

        if (arguments.size() != 1) {
            return null;
        }

        return arguments.getFirst();
    }

    @Override
    public @NonNull Set<Argument> getAll() {
        return new HashSet<>(this.arguments.values());
    }
}