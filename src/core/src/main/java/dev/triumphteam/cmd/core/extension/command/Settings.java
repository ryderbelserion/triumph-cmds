package dev.triumphteam.cmd.core.extension.command;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderMapper;
import dev.triumphteam.cmd.core.requirement.Requirement;
import org.jspecify.annotations.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Settings<D, S> {

    /**
     * Tests all the requirements present in the setting.
     * If any fails, handle the return and returns false.
     * Only truly succeeds if all requirements pass.
     *
     * @param sender          The sender to test the requirements to.
     * @param meta            The {@link CommandMeta} which is used by some requirements.
     * @param senderMapper    The {@link SenderMapper} which is used by some requirements.
     * @return True only if all requirements pass.
     */
    boolean testRequirements(
            @NonNull final S sender,
            @NonNull final CommandMeta meta,
            @NonNull final SenderMapper<D, S> senderMapper
    );

    /**
     * Tests all the requirements present in the setting.
     * If any fails, handle the return and returns false.
     * Only truly succeeds if all requirements pass.
     *
     * @param messageRegistry The {@link MessageRegistry} to send message to the sender.
     * @param sender          The sender to test the requirements to.
     * @param meta            The {@link CommandMeta} which is used by some requirements.
     * @param senderMapper    The {@link SenderMapper} which is used by some requirements.
     * @return True only if all requirements pass.
     */
    boolean testRequirements(
            @NonNull final MessageRegistry<S> messageRegistry,
            @NonNull final S sender,
            @NonNull final CommandMeta meta,
            @NonNull final SenderMapper<D, S> senderMapper
    );

    @NonNull List<Requirement<D, S>> getRequirements();

    class Builder<D, S> {

        private final List<Requirement<D, S>> requirements = new ArrayList<>();

        public @NonNull Builder<D, S> addRequirement(@NonNull final Requirement<D, S> requirement) {
            this.requirements.add(requirement);

            return this;
        }

        // TODO add more things to the settings

        public Settings<D, S> build() {
            return new ImmutableSettings<>(Collections.unmodifiableList(this.requirements));
        }
    }
}