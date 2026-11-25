package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.ValidationResult;
import dev.triumphteam.cmd.core.extension.command.CommandExecutor;
import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.meta.MetaKey;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.message.context.SyntaxMessageContext;
import dev.triumphteam.cmd.core.processor.CommandProcessor;
import dev.triumphteam.cmd.core.processor.LeafCommandProcessor;
import dev.triumphteam.cmd.core.util.Pair;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class InternalLeafCommand<D, S, ST> implements InternalCommand<D, S, ST> {

    private final Class<? extends S> senderType;

    private final List<InternalArgument<S, ST>> argumentList;
    private final Map<String, InternalArgument<S, ST>> argumentMap;

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String syntax;
    private final boolean containsLimitless;

    private final CommandMeta meta;
    private final Settings<D, S> settings;

    private final Object invocationInstance;
    private final Method method;
    private final CommandExecutor<S> commandExecutor;

    private final SenderExtension<D, S> senderExtension;
    private final MessageRegistry<S> messageRegistry;

    public InternalLeafCommand(
            @NonNull final Object invocationInstance,
            @NonNull final Method method,
            @NonNull final LeafCommandProcessor<D, S, ST> processor,
            @NonNull final InternalCommand<D, S, ST> parentCommand
    ) {
        // TODO: This should be moved into the processor so meta is always available.
        final Settings.Builder<D, S> settingsBuilder = new Settings.Builder<>();
        processor.captureRequirements(settingsBuilder);

        this.meta = processor.createMeta(settingsBuilder);

        this.invocationInstance = invocationInstance;
        this.method = method;
        this.name = processor.getName();
        this.aliases = processor.getAliases();
        this.description = this.meta.getOrDefault(MetaKey.DESCRIPTION, "");

        this.senderType = processor.senderType();
        this.argumentList = processor.arguments(this.meta);
        this.argumentMap = this.argumentList.stream()
                .map(argument -> new Pair<>(argument.getName(), argument))
                .collect(Collectors.toMap(Pair::first, Pair::second));

        this.containsLimitless = argumentList.stream().anyMatch(LimitlessInternalArgument.class::isInstance);

        final CommandOptions<?, ?, D, S, ST> commandOptions = processor.getCommandOptions();

        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();
        this.senderExtension = commandOptions.getCommandExtensions().getSenderExtension();
        this.commandExecutor = commandOptions.getCommandExtensions().getCommandExecutor();

        this.syntax = createSyntax(parentCommand, processor);

        this.settings = settingsBuilder.build();
    }

    public void execute(
            @NonNull final S sender,
            @Nullable final Supplier<Object> instanceSupplier,
            @NonNull final Map<String, ArgumentInput> arguments
    ) throws Throwable {
        final ValidationResult<MessageKey<MessageContext>> validationResult = senderExtension.validate(meta, senderType, sender);

        // If the result is invalid for a reason given by the validator, we stop the execution and use its key to send
        // a message to the sender
        if (validationResult instanceof ValidationResult.Invalid<MessageKey<MessageContext>>(MessageKey<MessageContext> message)) {
            this.messageRegistry.sendMessage(
                    message,
                    sender,
                    new SyntaxMessageContext(this.meta, this.syntax)
            );

            return;
        }

        // Testing if all requirements pass before we continue
        if (!this.settings.testRequirements(this.messageRegistry, sender, this.meta, this.senderExtension)) return;

        // Creates the invoking arguments list
        final List<Object> invokeArguments = new ArrayList<>();

        invokeArguments.add(sender);

        if ((!this.containsLimitless) && arguments.size() > this.argumentList.size()) {
            this.messageRegistry.sendMessage(MessageKey.TOO_MANY_ARGUMENTS, sender, new SyntaxMessageContext(this.meta, this.syntax));
            return;
        }

        for (final InternalArgument<S, ST> internalArgument : argumentList) {
            final ArgumentInput argumentInput = arguments.get(internalArgument.getName());

            final InternalArgumentResult result;
            if (internalArgument instanceof LimitlessInternalArgument<S, ST> limitlessArgument) {
                // From this point on [commandArgs] is treated as a simple Collection instead of Deque
                result = limitlessArgument.resolve(sender, argumentInput == null ? new ArgumentInput("") : argumentInput);
            } else if (internalArgument instanceof StringInternalArgument<S, ST> stringArgument) {
                ArgumentInput usableInput = argumentInput;

                if (argumentInput == null || argumentInput.input().isEmpty()) {
                    if (internalArgument.isOptional()) {
                        final String defaultValue = stringArgument.getDefaultValue();

                        // If nothing is provided, we just add null and move on.
                        if (defaultValue == null) {
                            invokeArguments.add(null);

                            continue;
                        }

                        // If not null, we provide the default as input.
                        usableInput = new ArgumentInput(defaultValue);
                    } else {
                        this.messageRegistry.sendMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, sender, new SyntaxMessageContext(this.meta, this.syntax));

                        return;
                    }
                }
                result = stringArgument.resolve(sender, usableInput);
            } else {
                // Should never happen, this should be a sealed type ... but hey, it's Java 8
                throw new CommandExecutionException("Found unsupported argument", "", this.name);
            }

            // In case of failure, we send the Sender a message.
            if (result instanceof InternalArgumentResult.Invalid) {
                this.messageRegistry.sendMessage(
                        MessageKey.INVALID_ARGUMENT,
                        sender,
                        ((InternalArgumentResult.Invalid) result).fail().apply(this.meta, this.syntax)
                );

                return;
            }

            // In case of success, we add the results.
            if (result instanceof InternalArgumentResult.Valid) {
                invokeArguments.add(((InternalArgumentResult.Valid) result).value());
            }
        }

        this.commandExecutor.execute(
                this.meta,
                this.messageRegistry,
                sender,
                instanceSupplier == null ? this.invocationInstance : instanceSupplier.get(),
                this.method,
                invokeArguments
        );
    }

    public @NonNull Map<String, ArgumentInput> mapArguments(@NonNull final Deque<String> arguments) {
        final Map<String, ArgumentInput> mappedArguments = new HashMap<>();

        int index = 0;

        while (!arguments.isEmpty()) {
            final String arg = arguments.peek();
            final InternalArgument<S, ST> internalArgument = getArgument(index);

            if (internalArgument == null || arg.isEmpty()) {
                mappedArguments.put(String.valueOf(index), new ArgumentInput(arg));

                arguments.pop(); // Pop before continuing.
                index++; // increment before continuing.

                continue;
            }

            index++; // Increment early so it doesn't need to repeat later.

            if (internalArgument instanceof LimitlessInternalArgument) {
                // Join all leftover arguments.
                mappedArguments.put(internalArgument.getName(), new ArgumentInput(String.join(" ", arguments)));

                break;
            }

            if (!(internalArgument instanceof StringInternalArgument)) {
                // Should never happen, this should be a sealed type ... but hey, it's Java 8.
                throw new CommandExecutionException("Found unsupported argument", "", this.name);
            }

            arguments.pop(); // Pop here so we don't remove the first argument of a "limitless".

            mappedArguments.put(internalArgument.getName(), new ArgumentInput(arg));
        }

        return mappedArguments;
    }

    public @NonNull List<ST> suggestions(
            @NonNull final S sender,
            @NonNull final List<String> arguments
    ) {
        if (arguments.isEmpty()) return emptyList();

        final int index = arguments.size() - 1;
        final InternalArgument<S, ST> currentArgument = getArgumentFromIndex(index);

        if (currentArgument == null) return emptyList();

        final Map<String, String> argumentsMap = new HashMap<>();

        for (int i = 0; i < this.argumentList.size(); i++) {
            final InternalArgument<S, ST> argument = this.argumentList.get(i);

            if (i < arguments.size()) {
                argumentsMap.put(argument.getName(), arguments.get(i));
            }
        }

        if (arguments.isEmpty()) {
            return currentArgument.suggestions(sender, "", arguments, argumentsMap);
        }

        final String current = arguments.get(index);

        return currentArgument.suggestions(sender, current, arguments, argumentsMap);
    }

    public @Nullable InternalArgument<S, ST> getArgumentFromIndex(final int index) {
        if (!hasArguments()) return null;

        final int size = this.argumentList.size();

        if (index >= size) {
            final InternalArgument<S, ST> last = this.argumentList.get(size - 1);

            if (last instanceof LimitlessInternalArgument) return last;

            return null;
        }

        return this.argumentList.get(index);
    }

    private @NonNull String createSyntax(
            @NonNull final InternalCommand<D, S, ST> parentCommand,
            @NonNull final CommandProcessor<D, S, ST> processor
    ) {
        final Syntax syntaxAnnotation = processor.getSyntaxAnnotation();

        if (syntaxAnnotation != null) return syntaxAnnotation.value();

        final StringBuilder builder = new StringBuilder(parentCommand.getSyntax());

        if (!InternalCommand.DEFAULT_CMD_NAME.equals(this.name)) {
            builder.append(" ").append(this.name);
        }

        this.argumentList.forEach(argument -> builder.append(" ").append("<").append(argument.getName()).append(">"));

        return builder.toString();
    }

    @Override
    public @NonNull CommandMeta getMeta() {
        return this.meta;
    }

    @Override
    public @NonNull Settings<D, S> getCommandSettings() {
        return this.settings;
    }

    @Override
    public @NonNull String getName() {
        return this.name;
    }

    @Override
    public @NonNull String getDescription() {
        return this.description;
    }

    @Override
    public @NonNull List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public @NonNull String getSyntax() {
        return this.syntax;
    }

    public @NonNull List<InternalArgument<S, ST>> getArgumentList() {
        return this.argumentList;
    }

    public @Nullable InternalArgument<S, ST> getArgument(final int index) {
        if (index >= this.argumentList.size()) return null;

        return this.argumentList.get(index);
    }

    public @Nullable InternalArgument<S, ST> getArgument(@NonNull final String name) {
        return this.argumentMap.get(name);
    }

    @Override
    public boolean isDefault() {
        return this.name.equals(InternalCommand.DEFAULT_CMD_NAME);
    }

    @Override
    public boolean isHidden() {
        return isDefault();
    }

    @Override
    public boolean hasArguments() {
        return !this.argumentList.isEmpty();
    }
}