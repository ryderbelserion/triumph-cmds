package dev.triumphteam.cmd.core.argument.keyed;

import org.jspecify.annotations.NonNull;

public interface Keyed {

    /**
     * Gets the arguments typed without the flags, joined to string.
     *
     * @return The arguments joined to string.
     */
    @NonNull String getText();

    /**
     * Gets the arguments typed without the flags, joined to string.
     *
     * @param delimiter The delimiter of the joining.
     * @return The arguments joined to string with a delimiter.
     */
    @NonNull String getText(@NonNull final String delimiter);
}