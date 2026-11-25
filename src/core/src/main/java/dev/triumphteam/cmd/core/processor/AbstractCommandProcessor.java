package dev.triumphteam.cmd.core.processor;

import com.google.common.base.CaseFormat;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import dev.triumphteam.cmd.core.annotations.Join;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Split;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.CollectionInternalArgument;
import dev.triumphteam.cmd.core.argument.EnumInternalArgument;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.JoinedStringInternalArgument;
import dev.triumphteam.cmd.core.argument.ResolverInternalArgument;
import dev.triumphteam.cmd.core.argument.SplitStringInternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.argument.UnknownInternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentGroup;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import dev.triumphteam.cmd.core.argument.keyed.Keyed;
import dev.triumphteam.cmd.core.argument.keyed.KeyedInternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.ListArgument;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.ArgumentRegistry;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.EnumSuggestion;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import dev.triumphteam.cmd.core.suggestion.SimpleSuggestion;
import dev.triumphteam.cmd.core.suggestion.SimpleSuggestionHolder;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstracts most of the "extracting" from sub command annotations, allows for extending.
 * <br/>
 * I know this could be done better, but couldn't think of a better way.
 * If you do please PR or let me know on my discord!
 *
 * @param <S> The sender type.
 */
@SuppressWarnings("unchecked")
abstract class AbstractCommandProcessor<D, S, ST> implements CommandProcessor<D, S, ST> {

    private static final Set<Class<?>> SUPPORTED_COLLECTIONS = new HashSet<>(Arrays.asList(List.class, Set.class));

    private final Object invocationInstance;
    private final String name;
    private final List<String> aliases;
    private final Syntax syntax;
    private final AnnotatedElement annotatedElement;

    private final RegistryContainer<D, S, ST> registryContainer;

    private final SuggestionRegistry<S, ST> suggestionRegistry;
    private final ArgumentRegistry<S, ST> argumentRegistry;

    private final CommandOptions<?, ?, D, S, ST> commandOptions;
    private final CommandMeta parentMeta;

    private final Map<SuggestionKey, InternalSuggestion<S, ST>> localSuggestions;
    private final SuggestionMapper<ST> suggestionMapper;

    AbstractCommandProcessor(
            @NonNull final Object invocationInstance,
            @NonNull final AnnotatedElement annotatedElement,
            @NonNull final RegistryContainer<D, S, ST> registryContainer,
            @NonNull final CommandOptions<?, ?, D, S, ST> commandOptions,
            @NonNull final CommandMeta parentMeta
    ) {
        this.invocationInstance = invocationInstance;
        this.annotatedElement = annotatedElement;
        this.name = nameOf();
        this.aliases = aliasesOf();
        this.parentMeta = parentMeta;

        this.commandOptions = commandOptions;
        this.registryContainer = registryContainer;
        this.suggestionRegistry = registryContainer.getSuggestionRegistry();
        this.argumentRegistry = registryContainer.getArgumentRegistry();
        this.suggestionMapper = commandOptions.getCommandExtensions().getSuggestionMapper();

        this.syntax = annotatedElement.getAnnotation(Syntax.class);

        this.localSuggestions = collectLocalSuggestions();
    }

    protected abstract String defaultCommandName();

    @Override
    public @NonNull RegistryContainer<D, S, ST> getRegistryContainer() {
        return this.registryContainer;
    }

    @Override
    public @NonNull CommandOptions<?, ?, D, S, ST> getCommandOptions() {
        return this.commandOptions;
    }

    protected @NonNull CommandMeta getParentMeta() {
        return this.parentMeta;
    }

    @Override
    public @Nullable Syntax getSyntaxAnnotation() {
        return this.syntax;
    }

    protected @NonNull CommandRegistrationException createException(@NonNull final String message) {
        return new CommandRegistrationException(message, this.annotatedElement, this.invocationInstance.getClass());
    }

    private @Nullable String nameOf() {
        final Command commandAnnotation = this.annotatedElement.getAnnotation(Command.class);

        // Not a command element
        if (commandAnnotation == null) return null;

        final String name = commandAnnotation.value();

        if (name.isEmpty()) return defaultCommandName();

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, commandAnnotation.value());
    }

    private @Nullable List<String> aliasesOf() {
        final Command commandAnnotation = this.annotatedElement.getAnnotation(Command.class);

        // Not a command element
        if (commandAnnotation == null) return null;

        return Arrays.stream(commandAnnotation.alias())
                .map(it -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, it))
                .collect(Collectors.toList());
    }

    public @Nullable String getName() {
        return name;
    }

    public @NonNull List<String> getAliases() {
        return aliases;
    }

    protected @NonNull InternalArgument<S, ST> argumentFromParameter(
            @NonNull final CommandMeta meta,
            @NonNull final Parameter parameter,
            @NonNull final List<String> argDescriptions,
            @NonNull final Map<Integer, InternalSuggestion<S, ST>> suggestions,
            @NonNull final ArgumentGroup<Flag> flagGroup,
            @NonNull final ArgumentGroup<Argument> argumentGroup,
            final int position
    ) {
        final Class<?> type = parameter.getType();
        final String argumentName = getArgName(parameter);
        final String argumentDescription = getArgumentDescription(argDescriptions, parameter, position);

        final Optional optionalAnnotation = parameter.getAnnotation(Optional.class);
        final boolean isOptional = optionalAnnotation != null;

        // Grab the default value of the annotation.
        final String defaultValue;
        if (optionalAnnotation == null) {
            defaultValue = null;
        } else {
            final String value = optionalAnnotation.defaultValue();
            if (value.isEmpty()) {
                defaultValue = null;
            } else {
                defaultValue = value;
            }
        }

        // Handles collection internalArgument.
        if (SUPPORTED_COLLECTIONS.stream().anyMatch(it -> it.isAssignableFrom(type))) {
            final Class<?> collectionType = getGenericType(parameter);
            final InternalArgument<S, ST> argument = createSimpleArgument(
                    meta,
                    collectionType,
                    argumentName,
                    argumentDescription,
                    suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                    null,
                    true
            );

            // Throw exception on unknown arguments for a collection parameter type
            if (argument instanceof UnknownInternalArgument) {
                throw createException("No internalArgument of type \"" + argument.getType().getName() + "\" registered");
            }

            final Split splitAnnotation = parameter.getAnnotation(Split.class);

            if (splitAnnotation != null) {
                return new SplitStringInternalArgument<>(
                        meta,
                        argumentName,
                        argumentDescription,
                        splitAnnotation.value(),
                        argument,
                        type,
                        suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                        defaultValue,
                        isOptional
                );
            }

            return new CollectionInternalArgument<>(
                    meta,
                    argumentName,
                    argumentDescription,
                    argument,
                    type,
                    suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                    defaultValue,
                    isOptional
            );
        }

        // Handler for using String with `@Join`.
        final Join joinAnnotation = parameter.getAnnotation(Join.class);

        if (type == String.class && joinAnnotation != null) {
            return new JoinedStringInternalArgument<>(
                    meta,
                    argumentName,
                    argumentDescription,
                    joinAnnotation.value(),
                    suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                    defaultValue,
                    isOptional
            );
        }

        // Handle flags and named arguments
        if (Keyed.class.isAssignableFrom(type)) {
            if (type == Arguments.class) {
                if (argumentGroup.isEmpty()) {
                    throw createException("No named arguments found, if you want only Flags use the \"" + Flags.class.getSimpleName() + "\" argument instead");
                }
            } else if (type == Flags.class) {
                if (flagGroup.isEmpty()) {
                    throw createException("No declared flags found, make sure you have registered or declared some, or make sure the key is correct.");
                }
            }

            return new KeyedInternalArgument<>(
                    meta,
                    argumentName,
                    argumentDescription,
                    createFlagInternals(meta, flagGroup),
                    createNamedArgumentInternals(meta, argumentGroup),
                    flagGroup,
                    argumentGroup,
                    this.suggestionMapper
            );
        }

        // No more exceptions found so now create a simple argument
        return createSimpleArgument(
                meta,
                type,
                argumentName,
                argumentDescription,
                suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                defaultValue,
                isOptional
        );
    }

    protected @NonNull StringInternalArgument<S, ST> createSimpleArgument(
            @NonNull final CommandMeta meta,
            @NonNull final Class<?> type,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final InternalSuggestion<S, ST> suggestion,
            @Nullable final String defaultValue,
            final boolean optional
    ) {
        // All other types default to the resolver.
        final ArgumentResolver<S> resolver = this.argumentRegistry.getResolver(type);

        if (resolver == null) {
            // Handler for using any Enum.
            if (Enum.class.isAssignableFrom(type)) {
                //noinspection unchecked
                return new EnumInternalArgument<>(
                        meta,
                        name,
                        description,
                        (Class<? extends Enum<?>>) type,
                        suggestion,
                        defaultValue,
                        optional
                );
            }

            final InternalArgument.Factory<S, ST> factory = this.argumentRegistry.getFactory(type);

            if (factory != null) {
                return factory.create(meta, name, description, type, suggestion, optional);
            }

            return new UnknownInternalArgument<>(type);
        }
        return new ResolverInternalArgument<>(
                meta,
                name,
                description,
                type,
                resolver,
                suggestion,
                defaultValue,
                optional
        );
    }

    private Map<Flag, StringInternalArgument<S, ST>> createFlagInternals(
            @NonNull final CommandMeta meta,
            @NonNull final ArgumentGroup<Flag> group
    ) {
        final Map<Flag, StringInternalArgument<S, ST>> internalArguments = new HashMap<>();

        for (final Flag flag : group.getAll()) {
            final Class<?> argType = flag.getArgument();
            final InternalSuggestion<S, ST> suggestion = createSuggestion(flag.getSuggestion(), argType, SuggestionMethod.STARTS_WITH, "");

            internalArguments.put(
                    flag,
                    createSimpleArgument(
                            meta,
                            argType,
                            "",
                            flag.getDescription(),
                            suggestion,
                            null,
                            true
                    )
            );
        }

        return internalArguments;
    }

    private Map<Argument, StringInternalArgument<S, ST>> createNamedArgumentInternals(
            @NonNull final CommandMeta meta,
            @NonNull final ArgumentGroup<Argument> group
    ) {
        final Map<Argument, StringInternalArgument<S, ST>> internalArguments = new HashMap<>();

        for (final Argument argument : group.getAll()) {
            final Class<?> argType = argument.getType();

            final InternalSuggestion<S, ST> suggestion = createSuggestion(argument.getSuggestion(), argType, SuggestionMethod.STARTS_WITH, "");

            if (argument instanceof ListArgument listArgument) {
                final InternalArgument<S, ST> internalArgument = createSimpleArgument(
                        meta,
                        listArgument.getType(),
                        listArgument.getName(),
                        listArgument.getDescription(),
                        suggestion,
                        null,
                        true
                );

                internalArguments.put(
                        argument,
                        new SplitStringInternalArgument<>(
                                meta,
                                listArgument.getName(),
                                listArgument.getDescription(),
                                listArgument.getSeparator(),
                                internalArgument,
                                listArgument.getType(),
                                suggestion,
                                null,
                                true
                        )
                );

                continue;
            }

            internalArguments.put(
                    argument,
                    createSimpleArgument(
                            meta,
                            argType,
                            argument.getName(),
                            argument.getDescription(),
                            suggestion,
                            null,
                            true
                    )
            );
        }

        return internalArguments;
    }

    /**
     * Gets the internalArgument name, either from the parameter or from the annotation.
     * If the parameter is not annotated, turn the name from Camel Case to "lower-hyphen".
     *
     * @param parameter The parameter to get data from.
     * @return The final internalArgument name.
     */
    private @NonNull String getArgName(@NonNull final Parameter parameter) {
        final ArgName argNameAnnotation = parameter.getAnnotation(ArgName.class);

        if (argNameAnnotation != null) {
            return argNameAnnotation.value();
        }

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, parameter.getName());
    }

    /**
     * Gets the internalArgument description.
     *
     * @param argDescriptions List with collected method annotation instead of argument annotation.
     * @param parameter       The parameter to get data from.
     * @param index           The index of the internalArgument.
     * @return The final internalArgument description.
     */
    private @NonNull String getArgumentDescription(
            final List<String> argDescriptions,
            @NonNull final Parameter parameter,
            final int index
    ) {
        final Description description = parameter.getAnnotation(Description.class);

        if (description != null) {
            return description.value();
        }

        if (index < argDescriptions.size()) return argDescriptions.get(index);

        return "";
    }

    protected @NonNull String descriptionOf() {
        final Class<?> commandClass = this.invocationInstance.getClass();

        final Description descriptionAnnotation = commandClass.getAnnotation(Description.class);

        if (descriptionAnnotation != null) return descriptionAnnotation.value();

        return "";
    }

    private @NonNull Class<?> getGenericType(@NonNull final Parameter parameter) {
        final Class<?> type = parameter.getType();

        if (SUPPORTED_COLLECTIONS.stream().anyMatch(it -> it.isAssignableFrom(type))) {
            final ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
            final Type[] types = parameterizedType.getActualTypeArguments();

            if (types.length != 1) {
                throw createException("Unsupported collection type \"" + type + "\"");
            }

            final Type genericType = types[0];
            return (Class<?>) (genericType instanceof WildcardType ? ((WildcardType) genericType).getUpperBounds()[0] : genericType);
        }

        return type;
    }

    private Map<SuggestionKey, InternalSuggestion<S, ST>> collectLocalSuggestions() {
        final Map<SuggestionKey, InternalSuggestion<S, ST>> suggestions = new HashMap<>();

        for (final Method method : this.invocationInstance.getClass().getDeclaredMethods()) {
            final Suggestion suggestionAnnotation = method.getAnnotation(Suggestion.class);

            if (suggestionAnnotation == null) continue;

            final Type returnType = method.getGenericReturnType();

            if (!(returnType instanceof ParameterizedType parameterizedType)) {
                throw createException("Suggestion method must return a List");
            }

            final Parameter[] parameters = method.getParameters();

            if (parameters.length > 1) {
                throw createException("Suggestion method must have either context as first parameter or no parameters at all");
            }

            final Parameter parameter = parameters.length == 1 ? parameters[0] : null;
            final boolean needsContext;

            if (parameter == null) {
                needsContext = false;
            } else {
                if (parameter.getType() != SuggestionContext.class) {
                    throw createException("Suggestion method must have either context as first parameter or no parameters at all");
                }

                needsContext = true;
            }

            if (parameterizedType.getRawType() != List.class) {
                throw createException("Suggestion method must return a List of suggestions");
            }

            final SuggestionKey key = SuggestionKey.of(suggestionAnnotation.value());

            final Type listType = parameterizedType.getActualTypeArguments()[0];

            if (listType.equals(String.class)) {
                final SimpleSuggestionHolder<S, ST> holder = new SimpleSuggestionHolder.SimpleLocal<>(this.suggestionMapper, this.invocationInstance, method, needsContext);

                suggestions.put(key, new SimpleSuggestion<>(holder, this.suggestionMapper, suggestionAnnotation.method()));

                continue;
            }

            if (!(listType.equals(this.suggestionMapper.getType()))) {
                throw createException("Suggestion method must return a List of strings or a List of '" + this.suggestionMapper.getType().getSimpleName() + "'");
            }

            final SimpleSuggestionHolder<S, ST> holder = new SimpleSuggestionHolder.RichLocal<>(this.invocationInstance, method, needsContext);

            suggestions.put(key, new SimpleSuggestion<>(holder, this.suggestionMapper, suggestionAnnotation.method()));
        }

        return suggestions;
    }

    private @NonNull InternalSuggestion<S, ST> suggestionFromParam(@NonNull final Parameter parameter) {
        final Suggestion annotation = parameter.getAnnotation(Suggestion.class);
        final SuggestionKey suggestionKey = annotation == null ? null : SuggestionKey.of(annotation.value());

        final Class<?> type = getGenericType(parameter);

        final SuggestionMethod method = annotation == null ? SuggestionMethod.STARTS_WITH : annotation.method();
        final String extra = annotation == null ? "" : annotation.extra();

        return createSuggestion(suggestionKey, type, method, extra);
    }

    protected @NonNull InternalSuggestion<S, ST> createSuggestion(
            @Nullable final SuggestionKey suggestionKey,
            @NonNull final Class<?> type,
            @NonNull final SuggestionMethod method,
            @NonNull final String extra
    ) {
        if (suggestionKey == null || suggestionKey.getKey().isEmpty()) {
            if (Enum.class.isAssignableFrom(type)) {
                return new EnumSuggestion<>((Class<? extends Enum<?>>) type, this.suggestionMapper, method, this.commandOptions.suggestLowercaseEnum());
            }

            final InternalSuggestion<S, ST> suggestion = this.suggestionRegistry.getSuggestion(type);

            if (suggestion != null) return suggestion;

            return new EmptySuggestion<>();
        }

        final InternalSuggestion<S, ST> suggestion = this.suggestionRegistry.getSuggestion(suggestionKey);

        if (suggestion == null) {
            final InternalSuggestion<S, ST> localSuggestion = this.localSuggestions.get(suggestionKey);

            if (localSuggestion != null) return localSuggestion.copy(method, extra);

            throw createException("Cannot find the suggestion key `" + suggestionKey + "`");
        }

        // TODO(important): INHERIT BY DEFAULT
        return suggestion.copy(method, extra);
    }
}