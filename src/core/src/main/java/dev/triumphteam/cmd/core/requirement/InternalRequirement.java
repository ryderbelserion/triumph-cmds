package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderMapper;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.BasicMessageContext;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jspecify.annotations.NonNull;

/**
 * Contains the data for the requirement.
 *
 * @param <S> The sender type.
 */
public final class InternalRequirement<D, S> implements Requirement<D, S> {

    private final RequirementResolver<D, S> resolver;
    private final MessageKey<MessageContext> messageKey;
    private final boolean invert;

    public InternalRequirement(
            @NonNull final RequirementResolver<D, S> resolver,
            @NonNull final MessageKey<MessageContext> messageKey,
            final boolean invert
    ) {
        this.resolver = resolver;
        this.messageKey = messageKey;
        this.invert = invert;
    }

    @Override
    public boolean test(
            @NonNull final S sender,
            @NonNull final CommandMeta meta,
            @NonNull final SenderMapper<D, S> senderMapper
    ) {
        return this.resolver.resolve(sender, new SimpleRequirementContext<>(meta, senderMapper)) != this.invert;
    }

    @Override
    public void onDeny(
            @NonNull final S sender,
            @NonNull final MessageRegistry<S> messageRegistry,
            @NonNull final CommandMeta meta
    ) {
        messageRegistry.sendMessage(this.messageKey, sender, new BasicMessageContext(meta));
    }

    @Override
    public @NonNull String toString() {
        return "InternalRequirement{" +
                "resolver=" + this.resolver +
                '}';
    }
}