package dev.triumphteam.cmd.core.extension.sender;

import dev.triumphteam.cmd.core.extension.ValidationResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jspecify.annotations.NonNull;
import java.util.Set;

public interface SenderExtension<D, S> extends SenderMapper<D, S> {

    @NonNull Set<Class<? extends S>> getAllowedSenders();

    @NonNull ValidationResult<@NonNull MessageKey<@NonNull MessageContext>> validate(
            @NonNull final CommandMeta meta,
            @NonNull final Class<?> allowedSender,
            @NonNull final S sender
    );

    interface Default<S> extends SenderExtension<S, S> {

        @Override
        default @NonNull ValidationResult<@NonNull MessageKey<@NonNull MessageContext>> validate(
                @NonNull final CommandMeta meta,
                @NonNull final Class<?> allowedSender,
                @NonNull final S sender
        ) {
            return valid();
        }

        @Override
        default @NonNull S map(@NonNull final S defaultSender) {
            return defaultSender;
        }

        @Override
        default @NonNull S mapBackwards(@NonNull final S sender) {
            return sender;
        }
    }

    default ValidationResult<@NonNull MessageKey<@NonNull MessageContext>> valid() {
        return new ValidationResult.Valid<>();
    }

    default ValidationResult<@NonNull MessageKey<@NonNull MessageContext>> invalid(
            @NonNull final MessageKey<@NonNull MessageContext> messageKey
    ) {
        return new ValidationResult.Invalid<>(messageKey);
    }
}