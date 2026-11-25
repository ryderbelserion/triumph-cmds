package dev.triumphteam.cmd.core.message;

import dev.triumphteam.cmd.core.extension.StringKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Objects;

/**
 * Registry key, for more organized way of registering and getting things from the registries.
 */
public abstract class ContextualKey<C extends MessageContext> extends StringKey {

    private final Class<C> type;

    protected ContextualKey(@NonNull final String key, @NonNull final Class<C> type) {
        super(key);

        this.type = type;
    }

    /**
     * Getter for the Context type, in case it's needed.
     *
     * @return The Context type.
     */
    public @NonNull Class<C> getType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable final Object object) {
        if (this == object) return true;

        if (object == null || getClass() != object.getClass()) return false;

        if (!super.equals(object)) return false;

        final ContextualKey<?> that = (ContextualKey<?>) object;

        return this.type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.type);
    }

    @Override
    public @NonNull String toString() {
        return "ContextualKey{" +
                "type=" + this.type +
                ", super=" + super.toString() + "}";
    }
}