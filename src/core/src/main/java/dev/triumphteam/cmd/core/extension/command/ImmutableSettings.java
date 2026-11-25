package dev.triumphteam.cmd.core.extension.command;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderMapper;
import dev.triumphteam.cmd.core.requirement.Requirement;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class ImmutableSettings<D, S> implements Settings<D, S> {

    private final List<Requirement<D, S>> requirements;

    public ImmutableSettings(@NonNull final List<Requirement<D, S>> requirements) {
        this.requirements = requirements;
    }

    @Override
    public boolean testRequirements(
            @NonNull final MessageRegistry<S> messageRegistry,
            @NonNull final S sender,
            @NonNull final CommandMeta meta,
            @NonNull final SenderMapper<D, S> senderMapper
    ) {
        final Requirement<D, S> requirement = getFailedRequirement(sender, meta, senderMapper);
        if (requirement == null) return true;

        requirement.onDeny(sender, messageRegistry, meta);
        return false;
    }

    @Override
    public boolean testRequirements(
            @NonNull final S sender,
            @NonNull final CommandMeta meta,
            @NonNull final SenderMapper<D, S> senderMapper
    ) {
        return getFailedRequirement(sender, meta, senderMapper) == null;
    }

    private @Nullable Requirement<D, S> getFailedRequirement(
            @NonNull final S sender,
            @NonNull final CommandMeta meta,
            @NonNull final SenderMapper<D, S> senderMapper
    ) {
        for (final Requirement<D, S> requirement : requirements) {
            if (!requirement.test(sender, meta, senderMapper)) return requirement;
        }

        return null;
    }

    @Override
    public @NonNull List<Requirement<D, S>> getRequirements() {
        return requirements;
    }
}
