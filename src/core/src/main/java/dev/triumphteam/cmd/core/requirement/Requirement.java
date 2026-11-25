package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderMapper;
import org.jspecify.annotations.NonNull;

public interface Requirement<D, S> {

    boolean test(
            @NonNull final S sender,
            @NonNull final CommandMeta meta,
            @NonNull final SenderMapper<D, S> senderMapper
    );

    void onDeny(
            @NonNull final S sender,
            @NonNull final MessageRegistry<S> messageRegistry,
            @NonNull final CommandMeta meta
    );
}
