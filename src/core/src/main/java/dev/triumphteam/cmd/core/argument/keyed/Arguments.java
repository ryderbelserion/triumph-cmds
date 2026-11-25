package dev.triumphteam.cmd.core.argument.keyed;

import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An argument container for using "Named Arguments" and "Flag Arguments".
 * Using the syntax: name:value or --flag.
 */
public interface Arguments extends Flags {

    /**
     * Gets an argument by name.
     * The argument will be an empty {@link Optional} if it does not exist.
     *
     * @param name The name of the argument.
     * @param type The class of the type of the argument.
     * @param <T>  The generic type of the argument.
     * @return An {@link Optional} argument.
     */
    <T> @NonNull Optional<T> getArgument(@NonNull final String name, @NonNull final Class<T> type);

    /**
     * Gets a {@link List} argument by name.
     * The argument will be an empty {@link Optional} if it does not exist.
     *
     * @param name The name of the argument.
     * @param type The class of the type of the argument.
     * @param <T>  The generic type of the argument.
     * @return An {@link Optional} argument.
     */
    <T> @NonNull Optional<List<T>> getListArgument(@NonNull final String name, @NonNull final Class<T> type);

    /**
     * Gets a {@link Set} argument by name.
     * The argument will be an empty {@link Optional} if it does not exist.
     *
     * @param name The name of the argument.
     * @param type The class of the type of the argument.
     * @param <T>  The generic type of the argument.
     * @return An {@link Optional} argument.
     */
    <T> @NonNull Optional<Set<T>> getSetArgument(@NonNull final String name, @NonNull final Class<T> type);

    /**
     * Get all arguments passed to this command.
     *
     * @return a {@link Map} of all arguments.
     */
    @NonNull Map<String, Object> getAllArguments();

    /**
     * Check if arguments were typed.
     *
     * @return true if any argument was typed in the command.
     */
    boolean hasArguments();
}