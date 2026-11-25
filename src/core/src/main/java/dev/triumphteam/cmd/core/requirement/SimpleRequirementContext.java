package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.sender.SenderMapper;
import org.jspecify.annotations.NonNull;

record SimpleRequirementContext<D, S>(CommandMeta meta, SenderMapper<D, S> senderMapper) implements RequirementContext<D, S> {

    SimpleRequirementContext(
            @NonNull final CommandMeta meta,
            @NonNull final SenderMapper<D, S> senderMapper
    ) {
        this.meta = meta;
        this.senderMapper = senderMapper;
    }
}