package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.MetaKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.processor.BranchCommandProcessor;
import dev.triumphteam.cmd.core.processor.CommandProcessor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.function.Supplier;

/**
 * A parent sub command is basically a holder of other sub commands.
 * This normally represents an inner class of a main command.
 * It can contain arguments which will turn it into an argument-as-subcommand type.
 *
 * @param <S> The sender type to be used.
 */
public class InternalBranchCommand<D, S, ST> extends InternalParentCommand<D, S, ST> {

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String syntax;

    private final Object invocationInstance;
    private final Constructor<?> constructor;
    private final boolean isStatic;
    private final StringInternalArgument<S, ST> argument;
    private final boolean hasArgument;

    public InternalBranchCommand(
            @NonNull final Object invocationInstance,
            @NonNull final Constructor<?> constructor,
            final boolean isStatic,
            @Nullable final StringInternalArgument<S, ST> argument,
            @NonNull final BranchCommandProcessor<D, S, ST> processor,
            @NonNull final InternalCommand<D, S, ST> parentCommand
    ) {
        super(processor);

        this.invocationInstance = invocationInstance;
        this.constructor = constructor;
        this.isStatic = isStatic;
        this.argument = argument;
        this.hasArgument = argument != null;

        this.name = processor.getName();
        this.description = getMeta().getOrDefault(MetaKey.DESCRIPTION, "");
        this.aliases = processor.getAliases();
        this.syntax = createSyntax(parentCommand, processor);
    }

    public void execute(
            @NonNull final S sender,
            @Nullable final Supplier<Object> instanceSupplier,
            @NonNull final Deque<String> arguments
    ) throws Throwable {
        // Test all requirements before continuing.
        if (!getSettings().testRequirements(getMessageRegistry(), sender, getMeta(), getSenderExtension())) return;

        // First, we handle the argument if there is any.
        final Object instance;

        if (this.hasArgument) {
            final String argumentName = arguments.peek() == null ? "" : arguments.pop();

            final InternalArgumentResult result = this.argument.resolve(sender, new ArgumentInput(argumentName));

            if (result instanceof InternalArgumentResult.Invalid) {
                getMessageRegistry().sendMessage(
                        MessageKey.INVALID_ARGUMENT,
                        sender,
                        ((InternalArgumentResult.Invalid) result).fail().apply(getMeta(), syntax)
                );

                return;
            }

            if (!(result instanceof InternalArgumentResult.Valid(Object value))) {
                throw new CommandExecutionException("An error occurred resolving arguments", "", name);
            }

            instance = createInstanceWithArgument(instanceSupplier, value);
        } else {
            instance = createInstance(instanceSupplier);
        }

        // Execute the command with the given instance.
        findAndExecute(sender, () -> instance, arguments);
    }

    @Override
    public @NonNull List<ST> suggestions(@NonNull final S sender, @NonNull final Deque<String> arguments) {
        // If we're dealing with only 1 argument, it means it's the argument suggestion
        if (arguments.size() == 1 && this.hasArgument) {
            return this.argument.suggestions(sender, arguments.peekLast(), new ArrayList<>(arguments), Collections.emptyMap());
        }

        // If we do have arguments, we need to pop them out before continuing
        if (this.hasArgument) arguments.pop();

        return super.suggestions(sender, arguments);
    }

    /**
     * Creates a new instance to be passed down to the child commands.
     *
     * @param instanceSupplier The instance supplier from parents.
     * @return An instance of this command for execution.
     */
    public @NonNull Object createInstance(@Nullable final Supplier<Object> instanceSupplier) {
        try {
            // Non-static classes required parent instance
            if (!this.isStatic) {
                return this.constructor.newInstance(instanceSupplier == null ? this.invocationInstance : instanceSupplier.get());
            }

            return this.constructor.newInstance();
        } catch (final InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            throw new CommandExecutionException("An error occurred while creating the command instance")
                    .initCause(exception instanceof InvocationTargetException ? exception.getCause() : exception);
        }
    }

    /**
     * Creates a new instance to be passed down to the child commands.
     *
     * @param instanceSupplier The instance supplier from parents.
     * @param argumentValue    The argument value.
     * @return An instance of this command for execution.
     */
    private @NonNull Object createInstanceWithArgument(
            @Nullable final Supplier<Object> instanceSupplier,
            @Nullable final Object argumentValue
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // Non-static classes required parent instance
        if (!this.isStatic) {
            return this.constructor.newInstance(instanceSupplier == null ? this.invocationInstance : instanceSupplier.get(), argumentValue);
        }

        return this.constructor.newInstance(argumentValue);
    }

    private @NonNull String createSyntax(@NonNull final InternalCommand<D, S, ST> parentCommand,
            @NonNull final CommandProcessor<D, S, ST> processor) {
        final Syntax syntaxAnnotation = processor.getSyntaxAnnotation();

        if (syntaxAnnotation != null) return syntaxAnnotation.value();

        final StringBuilder builder = new StringBuilder();

        builder.append(parentCommand.getSyntax()).append(" ");

        if (this.hasArgument) {
            builder.append("<").append(this.argument.getName()).append(">");
        } else {
            builder.append(this.name);
        }

        return builder.toString();
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

    @Override
    public boolean hasArguments() {
        return this.argument != null;
    }

    @Override
    public boolean isHidden() {
        return this.argument != null;
    }
}