package dev.triumphteam.cmd.core.argument.keyed;

import org.jspecify.annotations.NonNull;
import java.util.Optional;
import java.util.Set;

/**
 * Contains all the command flags typed by the user in an easy-to-access way.
 */
public interface Flags extends Keyed {

    /**
     * Checks if the flag key is present or not.
     * Useful for simple flags like <code>-l</code>.
     * Where you just want to check if the flag was added or not.
     * For flag with values recommended {@link Flags#getFlagValue(String, Class)}.
     *
     * @param flag The flag to check.
     * @return Whether the flag is present in the command or not.
     */
    boolean hasFlag(@NonNull final String flag);

    /**
     * Gets the flag value.
     * If the value is not present OR if the value is not the correct type, optional will be empty.
     *
     * @param flag The flag to get the value from.
     * @param type The {@link Class} of the value to get.
     * @param <T>  The value type, based on the class from before.
     * @return The flag's value.
     */
    <T> @NonNull Optional<T> getFlagValue(@NonNull final String flag, @NonNull final Class<? super T> type);

    /**
     * Instead of converting the value to the desired type, get it as string.
     * If a flag is not present, then optional will be empty.
     *
     * @param flag The flag to get the value from.
     * @return The flag's value.
     */
    @NonNull Optional<String> getFlagValue(@NonNull final String flag);

    /**
     * @return A {@link Set} with the present Flags.
     */
    @NonNull Set<String> getAllFlags();

    /**
     * Check if flags were typed.
     *
     * @return true if any flag was typed in the command.
     */
    boolean hasFlags();
}