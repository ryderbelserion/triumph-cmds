/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderMapper;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.BasicMessageContext;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Contains the data for the requirement.
 *
 * @param <S> the sender type.
 */
public final class InternalRequirement<D, S> implements Requirement<D, S> {

    private final RequirementResolver<D, S> resolver;
    private final MessageKey<MessageContext> messageKey;
    private final boolean invert;

    public InternalRequirement(
            @NotNull final RequirementResolver<D, S> resolver,
            @NotNull final MessageKey<MessageContext> messageKey,
            final boolean invert
    ) {
        this.resolver = resolver;
        this.messageKey = messageKey;
        this.invert = invert;
    }

    @Override
    public boolean test(
            @NotNull final S sender,
            @NotNull final CommandMeta meta,
            @NotNull final SenderMapper<D, S> senderMapper
    ) {
        return this.resolver.resolve(sender, new SimpleRequirementContext<>(meta, senderMapper)) != invert;
    }

    @Override
    public void onDeny(
            @NotNull final S sender,
            @NotNull final MessageRegistry<S> messageRegistry,
            @NotNull final CommandMeta meta
    ) {
        messageRegistry.sendMessage(this.messageKey, sender, new BasicMessageContext(meta));
    }

    @Override
    public @NotNull String toString() {
        return "InternalRequirement{" +
                "resolver=" + this.resolver +
                '}';
    }
}