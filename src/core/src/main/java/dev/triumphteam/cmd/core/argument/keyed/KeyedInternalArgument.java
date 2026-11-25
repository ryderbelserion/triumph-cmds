package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.util.Pair;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class KeyedInternalArgument<S, ST> extends LimitlessInternalArgument<S, ST> {

    private final Map<Flag, StringInternalArgument<S, ST>> flagInternalArguments;
    private final Map<Argument, StringInternalArgument<S, ST>> argumentInternalArguments;
    private final SuggestionMapper<ST> mapper;

    private final ArgumentParser argumentParser;

    public KeyedInternalArgument(
            @NonNull final CommandMeta meta,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final Map<Flag, StringInternalArgument<S, ST>> flagInternalArguments,
            @NonNull final Map<Argument, StringInternalArgument<S, ST>> argumentInternalArguments,
            @NonNull final ArgumentGroup<Flag> flagGroup,
            @NonNull final ArgumentGroup<Argument> argumentGroup,
            @NonNull final SuggestionMapper<ST> mapper
    ) {
        super(meta, name, description, Flags.class, new EmptySuggestion<>(), null, true);

        this.flagInternalArguments = flagInternalArguments;
        this.argumentInternalArguments = argumentInternalArguments;
        this.mapper = mapper;
        this.argumentParser = new ArgumentParser(flagGroup, argumentGroup);
    }

    @Override
    public @NonNull InternalArgumentResult resolve(@NonNull final S sender, @NonNull final ArgumentInput input) {
        final ArgumentParser.Result result = this.argumentParser.parse(Arrays.asList(input.input().split(" ")));

        // Parsing and validating named arguments
        final Map<String, ArgumentValue> arguments = new HashMap<>();

        for (final Map.Entry<Argument, String> entry : result.getNamedArguments().entrySet()) {
            final Argument argument = entry.getKey();
            final String raw = entry.getValue();

            final StringInternalArgument<S, ST> internalArgument = this.argumentInternalArguments.get(argument);

            if (internalArgument == null) continue;

            final InternalArgumentResult resolved = internalArgument.resolve(sender, new ArgumentInput(entry.getValue()));

            if (resolved instanceof InternalArgumentResult.Invalid) {
                return resolved;
            }

            if (resolved instanceof InternalArgumentResult.Valid(Object resolvedValue)) {
                arguments.put(argument.getName(), new SimpleArgumentValue(raw, resolvedValue));
            }
        }

        // Parsing and validating flags
        final Map<String, ArgumentValue> flags = new HashMap<>();

        for (final Map.Entry<Flag, String> entry : result.getFlags().entrySet()) {
            final Flag flag = entry.getKey();
            final String raw = entry.getValue();

            if (!flag.hasArgument()) {
                flags.put(flag.getFlag(), EmptyArgumentValue.INSTANCE);
                flags.put(flag.getLongFlag(), EmptyArgumentValue.INSTANCE);

                continue;
            }

            final StringInternalArgument<S, ST> internalArgument = this.flagInternalArguments.get(flag);

            if (internalArgument == null) continue;

            final InternalArgumentResult resolved = internalArgument.resolve(sender, new ArgumentInput(entry.getValue()));

            if (resolved instanceof InternalArgumentResult.Invalid) {
                return resolved;
            }

            if (resolved instanceof InternalArgumentResult.Valid(Object resolvedValue)) {
                final ArgumentValue argumentValue = new SimpleArgumentValue(raw, resolvedValue);

                flags.put(flag.getFlag(), argumentValue);
                flags.put(flag.getLongFlag(), argumentValue);
            }
        }

        return InternalArgument.valid(new KeyedArguments(arguments, flags, result.getNonTokens()));
    }

    @Override
    public @NonNull List<ST> suggestions(
            @NonNull final S sender,
            @NonNull final String current,
            @NonNull final List<String> arguments,
            @NonNull final Map<String, String> argumentsMap
    ) {
        final ArgumentParser.Result result = argumentParser.parse(arguments);
        final String resultCurrent = result.getCurrent();

        // Checking if we're waiting for a flag argument
        final List<String> waitingFlagArguments = handleFlagArgument(resultCurrent, result, sender);
        if (waitingFlagArguments != null) return map(waitingFlagArguments);

        // Checking if we're waiting for an argument
        final List<String> waitingArguments = handleNamedArgument(resultCurrent, result, sender);
        if (waitingArguments != null) return map(waitingArguments);

        // Handle flags only when they are typed
        if (current.startsWith("--")) return map(longFlags(resultCurrent, result.getFlags()));
        if (current.startsWith("-")) return map(flags(resultCurrent, result.getFlags()));

        // If we're not dealing with flags or arguments, we return a list of named arguments that haven't been used yet
        return map(namedArguments(resultCurrent, result.getNamedArguments()));
    }

    private @NonNull List<ST> map(@NonNull final List<String> suggestions) {
        return this.mapper.map(suggestions, getType());
    }

    private @NonNull List<String> longFlags(
            @NonNull final String current,
            @NonNull final Map<Flag, String> parsed
    ) {
        return this.flagInternalArguments.keySet()
                .stream()
                .filter(it -> !parsed.containsKey(it))
                .map(Flag::getLongFlag)
                .filter(Objects::nonNull)
                .map(it -> "--" + it)
                .filter(it -> it.startsWith(current))
                .collect(Collectors.toList());
    }

    private @NonNull List<String> flags(
            @NonNull final String current,
            @NonNull final Map<Flag, String> parsed
    ) {
        return this.flagInternalArguments.keySet()
                .stream()
                .filter(it -> !parsed.containsKey(it))
                .map(Flag::getFlag)
                .filter(Objects::nonNull)
                .map(it -> "-" + it)
                .filter(it -> it.startsWith(current))
                .collect(Collectors.toList());
    }

    private @NonNull List<String> namedArguments(
            @NonNull final String current,
            @NonNull final Map<Argument, String> parsed
    ) {
        return this.argumentInternalArguments.keySet()
                .stream()
                .filter(it -> !parsed.containsKey(it))
                .flatMap(it -> Stream.of(it.getName(), it.getLongName()))
                .filter(Objects::nonNull)
                .filter(it -> it.startsWith(current))
                .map(it -> it + ":")
                .collect(Collectors.toList());
    }

    private @Nullable List<String> handleNamedArgument(
            @NonNull final String current,
            final ArgumentParser.@NonNull Result result,
            @NonNull final S sender
    ) {
        // Checking if we're waiting for an argument
        final Argument waiting = result.getArgumentWaiting();

        if (waiting == null) return null;

        // If so we get the internal version of the argument, this will likely never be null
        final InternalArgument<S, ST> internalArgument = this.argumentInternalArguments.get(waiting);

        if (internalArgument == null) return null;

        final String raw = (waiting.isLongNameArgument() ? waiting.getLongName() : waiting.getName()) + ":";
        // Get a suggestion from the internal argument and map it to the "raw" argument
        final List<String> suggestions = internalArgument.suggestions(sender, current, Collections.singletonList(current), Collections.emptyMap())
                .stream()
                .map(it -> raw + it)
                .collect(Collectors.toList());

        // In case the suggestion returns nothing, we just return the raw type as a suggestion
        if (suggestions.isEmpty()) return Collections.singletonList(raw);

        // If there are suggestions, we return them
        return suggestions;
    }

    private @Nullable List<String> handleFlagArgument(
            @NonNull final String current,
            final ArgumentParser.@NonNull Result result,
            @NonNull final S sender
    ) {
        final Pair<Flag, ArgumentParser.Result.FlagType> waitingFlag = result.getFlagWaiting();

        if (waitingFlag == null) return null;

        final Flag flag = waitingFlag.first();
        final ArgumentParser.Result.FlagType type = waitingFlag.second();

        if (flag == null || type == null) return null;

        final InternalArgument<S, ST> internalArgument = this.flagInternalArguments.get(flag);

        if (internalArgument == null) return null;

        return this.mapper.mapBackwards(internalArgument.suggestions(sender, current, Collections.singletonList(current), Collections.emptyMap()))
                .stream()
                .map(it -> {
                    if (!type.hasEquals()) return it; // No equals, so we just suggest the argument
                    final String prefix = type.isLong() ? "--" + flag.getLongFlag() : "-" + flag.getFlag();
                    return prefix + "=" + it;
                }).collect(Collectors.toList());
    }
}