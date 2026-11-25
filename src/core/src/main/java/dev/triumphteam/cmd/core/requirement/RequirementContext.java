package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.command.InternalCommand;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.sender.SenderMapper;
import org.jspecify.annotations.NonNull;

/**
 * Holder for some context data to pass over to the {@link RequirementResolver}.
 *
 * @param <D> The default sender.
 * @param <S> The final sender.
 */
public interface RequirementContext<D, S> {

    /**
     * @return The {@link CommandMeta} of the current {@link InternalCommand}.
     */
    @NonNull CommandMeta meta();

    /**
     * @return The {@link SenderMapper} for reverse mapping if needed.
     */
    @NonNull SenderMapper<D, S> senderMapper();
}