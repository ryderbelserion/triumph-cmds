package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Collection argument, a {@link LimitlessInternalArgument} but returns a {@link List} instead.
 * Currently, only supports {@link List} and {@link Set}.
 *
 * @param <S> The sender type.
 */
public final class CollectionInternalArgument<S, ST> extends LimitlessInternalArgument<S, ST> {

    private final InternalArgument<S, ST> internalArgument;
    private final Class<?> collectionType;

    public CollectionInternalArgument(
            @NonNull final CommandMeta meta,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final InternalArgument<S, ST> internalArgument,
            @NonNull final Class<?> collectionType,
            @NonNull final InternalSuggestion<S, ST> suggestion,
            @Nullable final String defaultValue,
            final boolean optional
    ) {
        super(meta, name, description, String.class, suggestion, defaultValue, optional);

        this.internalArgument = internalArgument;
        this.collectionType = collectionType;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @return A {@link java.util.Collection} type as the resolved value.
     */
    @Override
    public @NonNull InternalArgumentResult resolve(@NonNull final S sender, @NonNull final ArgumentInput input
    ) {
        return resolveCollection(sender, this.internalArgument, Arrays.asList(input.input().split(" ")), this.collectionType);
    }

    public static <S, ST> @NonNull InternalArgumentResult resolveCollection(
            @NonNull final S sender,
            @NonNull final InternalArgument<S, ST> internalArgument,
            @NonNull final Collection<String> value,
            @NonNull final Class<?> collectionType
    ) {
        // Create a collection based on the type.
        final Collection<Object> collection = createCollection(collectionType);

        for (final String arg : value) {
            final InternalArgumentResult resolved = internalArgument.resolve(sender, new ArgumentInput(arg));

            // If an error occurs, it needs to be delegated back to the caller.
            if (!(resolved instanceof InternalArgumentResult.Valid(Object result))) return resolved;

            // If success, then we collect the result's value.
            collection.add(result);
        }

        // Return the collection as a success
        return InternalArgument.valid(collection);
    }

    private static Collection<Object> createCollection(final Class<?> collectionType) {
        if (collectionType == Set.class) return new HashSet<>();
        return new ArrayList<>();
    }

    @Override
    public @NonNull String toString() {
        return "CollectionArgument{" +
                "collectionType=" + this.collectionType +
                ", super=" + super.toString() + "}";
    }
}