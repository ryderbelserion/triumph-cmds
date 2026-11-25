package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.lang.ref.WeakReference;
import static dev.triumphteam.cmd.core.util.EnumUtils.getEnumConstants;
import static dev.triumphteam.cmd.core.util.EnumUtils.populateCache;

/**
 * An argument type for {@link Enum}s.
 * This is needed instead of the normal {@link ResolverInternalArgument} because of different types of enums, which requires the class.
 *
 * @param <S> The sender type.
 */
public final class EnumInternalArgument<S, ST> extends StringInternalArgument<S, ST> {

    private final Class<? extends Enum<?>> enumType;

    public EnumInternalArgument(
            @NonNull final CommandMeta meta,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final Class<? extends Enum<?>> type,
            @NonNull final InternalSuggestion<S, ST> suggestion,
            @Nullable final String defaultValue,
            final boolean optional
    ) {
        super(meta, name, description, type, suggestion, defaultValue, optional);

        this.enumType = type;

        // Populates on creation to reduce runtime of the first run for certain enums, like Bukkit's Material.
        populateCache(type);
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param input  The {@link String} argument input.
     * @return An {@link Enum} value of the correct type.
     */
    @Override
    public @NonNull InternalArgumentResult resolve(@NonNull final S sender, @NonNull final ArgumentInput input) {
        final String value = input.input();
        final WeakReference<? extends Enum<?>> reference = getEnumConstants(this.enumType).get(value.toUpperCase());

        if (reference == null) {
            return InternalArgument.invalid((meta, syntax) -> new InvalidArgumentContext(meta, syntax, value, getName(), getType()));
        }

        final Enum<?> enumValue = reference.get();

        if (enumValue == null) {
            return InternalArgument.invalid((commands, arguments) -> new InvalidArgumentContext(commands, arguments, value, getName(), getType()));
        }

        return InternalArgument.valid(enumValue);
    }

    @Override
    public @NonNull String toString() {
        return "EnumArgument{" +
                "enumType=" + this.enumType +
                ", super=" + super.toString() + "}";
    }
}