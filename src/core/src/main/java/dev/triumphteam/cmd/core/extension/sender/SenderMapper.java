package dev.triumphteam.cmd.core.extension.sender;

import org.jspecify.annotations.NonNull;

/**
 * Map a sender into a new type of sender that can be used on the commands.
 * This is useful if you want to have a custom type for your project.
 * For example a "User" sender with more data than the platform's sender provides.
 *
 * @param <D> The default sender, aka the platforms default.
 * @param <S> The mapped/final sender.
 */
public interface SenderMapper<D, S> {

    /**
     * Map from a default sender into your custom sender.
     *
     * @param defaultSender The platform provided sender.
     * @return A final usable version of the sender.
     */
    @NonNull S map(@NonNull final D defaultSender);

    /**
     * Return back to the original sender.
     *
     * @param sender The mapped/final sender.
     * @return The platform provided sender, that was used to map into the current sender.
     */
    @NonNull D mapBackwards(@NonNull final S sender);
}