package dev.triumphteam.cmd.core.argument.keyed;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.Set;

/**
 * A group of argument data.
 * Example implementations are, flags and named arguments.
 *
 * @param <T> The type of argument of the group.
 */
public interface ArgumentGroup<T> {

    /**
     * Static factory for creating a new flag {@link ArgumentGroup} of type {@link Flag}.
     *
     * @param flags The {@link List} of {@link Flag}s.
     * @return A {@link FlagGroup} instance.
     */
    static ArgumentGroup<Flag> flags(@NonNull final List<Flag> flags) {
        return new FlagGroup(flags);
    }

    /**
     * Static factory for creating a new arguments {@link ArgumentGroup} of type {@link Argument}.
     *
     * @param arguments The {@link List} of {@link Argument}s.
     * @return A {@link NamedGroup} instance.
     */
    static ArgumentGroup<Argument> named(@NonNull final List<Argument> arguments) {
        return new NamedGroup(arguments);
    }

    /**
     * Gets the argument that matches the current token.
     *
     * @param token The current token, an argument name or not.
     * @return The argument if found or null if not a valid argument name.
     */
    @Nullable T matchExact(@NonNull final String token);

    /**
     * Get the argument that partially matches a single {@link T} element.
     *
     * @param token The token to verify.
     * @return A partially matched T or null.
     */
    @Nullable T matchPartialSingle(@NonNull final String token);

    /**
     * Gets a list with all possible argument names.
     *
     * @return A {@link Set} of names.
     */
    @NonNull Set<String> getAllNames();

    /**
     * @return Whether the group is empty.
     */
    boolean isEmpty();

    /**
     * @return Gets a set with all the arguments of type {@link T}.
     */
    @NonNull Set<T> getAll();
}