package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Splitting argument takes a string and splits it into a collection.
 *
 * @param <S> The sender type.
 */
public final class SplitStringInternalArgument<S, ST> extends StringInternalArgument<S, ST> {

    private final String regex;
    private final InternalArgument<S, ST> internalArgument;
    private final Class<?> collectionType;

    public SplitStringInternalArgument(
            @NonNull final CommandMeta meta,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final String regex,
            @NonNull final InternalArgument<S, ST> internalArgument,
            @NonNull final Class<?> collectionType,
            @NonNull final InternalSuggestion<S, ST> suggestion,
            @Nullable final String defaultValue,
            final boolean optional
    ) {
        super(meta, name, description, String.class, suggestion, defaultValue, optional);

        this.regex = regex;
        this.internalArgument = internalArgument;
        this.collectionType = collectionType;
    }

    @Override
    public @NonNull InternalArgumentResult resolve(@NonNull final S sender, @NonNull final ArgumentInput input) {
        return CollectionInternalArgument.resolveCollection(sender, this.internalArgument, Arrays.asList(input.input().split(this.regex)), this.collectionType);
    }

    public @NonNull List<String> suggestions(
            @NonNull final S sender,
            @NonNull final Deque<String> arguments
    ) {
        final String peek = arguments.peekLast();
        final String last = peek == null ? "" : peek;

        final List<String> split = Arrays.asList(last.split(this.regex));

        if (split.isEmpty()) return Collections.emptyList();

        final String current = last.endsWith(this.regex) ? "" : split.getLast();
        final String joined = String.join(this.regex, current.isEmpty() ? split : split.subList(0, split.size() - 1));
        final String map = joined.isEmpty() ? "" : joined + this.regex;

        final InternalSuggestion<S, ST> suggestion = getSuggestion();

        if (!(suggestion instanceof InternalSuggestion.Simple)) return Collections.emptyList();

        return ((InternalSuggestion.Simple<S, ST>) suggestion).getSuggestions(sender, current, new ArrayList<>(arguments), Collections.emptyMap())
                .stream()
                .map(it -> map + it)
                .collect(Collectors.toList());
    }

    @Override
    public @NonNull String toString() {
        return "SplitArgument{super=" + super.toString() + "}";
    }
}