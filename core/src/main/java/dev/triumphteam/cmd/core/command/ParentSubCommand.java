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
package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extension.Result;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.processor.CommandProcessor;
import dev.triumphteam.cmd.core.processor.ParentCommandProcessor;
import dev.triumphteam.cmd.core.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A parent sub command is basically a holder of other sub commands.
 * This normally represents an inner class of a main command.
 * It can contain arguments which will turn it into an argument-as-subcommand type.
 *
 * @param <S> the sender type to be used.
 */
public class ParentSubCommand<D, S> extends ParentCommand<D, S> {

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String syntax;

    private final Object invocationInstance;
    private final Constructor<?> constructor;
    private final boolean isStatic;
    private final StringInternalArgument<S> argument;
    private final boolean hasArgument;

    public ParentSubCommand(
            @NotNull final Object invocationInstance,
            @NotNull final Constructor<?> constructor,
            final boolean isStatic,
            @Nullable final StringInternalArgument<S> argument,
            @NotNull final ParentCommandProcessor<D, S> processor,
            @NotNull final Command<D, S> parentCommand
    ) {
        super(processor);

        this.invocationInstance = invocationInstance;
        this.constructor = constructor;
        this.isStatic = isStatic;
        this.argument = argument;
        this.hasArgument = argument != null;

        this.name = processor.getName();
        this.description = processor.getDescription();
        this.aliases = processor.getAliases();
        this.syntax = createSyntax(parentCommand, processor);
    }

    @Override
    public void execute(
            @NotNull final S sender,
            @Nullable final Supplier<Object> instanceSupplier,
            @NotNull final Deque<String> arguments
    ) throws Throwable {
        // Test all requirements before continuing
        if (!getSettings().testRequirements(getMessageRegistry(), sender, getMeta(), getSenderExtension())) return;

        // First we handle the argument if there is any
        final Object instance;

        if (this.hasArgument) {
            final String argumentName = arguments.peek() == null ? "" : arguments.pop();

            @NotNull final Result<Object, BiFunction<CommandMeta, String, InvalidArgumentContext>> result = this.argument.resolve(sender, argumentName);

            if (result instanceof Result.Failure) {
                getMessageRegistry().sendMessage(
                        MessageKey.INVALID_ARGUMENT,
                        sender,
                        ((Result.Failure<Object, BiFunction<CommandMeta, String, InvalidArgumentContext>>) result)
                                .getFail()
                                .apply(getMeta(), this.syntax)
                );

                return;
            }

            if (!(result instanceof Result.Success)) {
                throw new CommandExecutionException("An error occurred resolving arguments", "", this.name);
            }

            instance = createInstanceWithArgument(instanceSupplier, ((Result.Success<Object, BiFunction<CommandMeta, String, InvalidArgumentContext>>) result).getValue());
        } else {
            instance = createInstance(instanceSupplier);
        }

        final Command<D, S> command = findCommand(sender, arguments, true);

        if (command == null) return;

        // Simply execute the command with the given instance
        command.execute(
                sender,
                () -> instance,
                arguments
        );
    }

    @Override
    public void executeNonLinear(
            @NotNull final S sender,
            @Nullable final Supplier<Object> instanceSupplier,
            @NotNull final Deque<String> commands,
            @NotNull final Map<String, Pair<String, Object>> arguments
    ) throws Throwable {
        // Test all requirements before continuing
        if (!getSettings().testRequirements(getMessageRegistry(), sender, getMeta(), getSenderExtension())) return;

        final Command<D, S> command = findCommand(sender, commands, true);

        if (command == null) return;

        final Object instance = createInstance(instanceSupplier);

        // Simply execute the command with the given instance
        command.executeNonLinear(
                sender,
                () -> instance,
                commands,
                arguments
        );
    }

    @Override
    public @NotNull List<String> suggestions(@NotNull final S sender, @NotNull final Deque<String> arguments) {
        // If we're dealing with only 1 argument it means it's the argument suggestion
        if (arguments.size() == 1 && this.hasArgument) {
            return this.argument.suggestions(sender, arguments);
        }

        // If we do have arguments we need to pop them out before continuing
        if (this.hasArgument) arguments.pop();

        return super.suggestions(sender, arguments);
    }

    /**
     * Creates a new instance to be passed down to the child commands.
     *
     * @param instanceSupplier the instance supplier from parents.
     * @return an instance of this command for execution.
     */
    private @NotNull Object createInstance(@Nullable final Supplier<Object> instanceSupplier) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // Non-static classes required parent instance
        if (!this.isStatic) {
            return this.constructor.newInstance(instanceSupplier == null ? this.invocationInstance : instanceSupplier.get());
        }

        return this.constructor.newInstance();
    }

    /**
     * Creates a new instance to be passed down to the child commands.
     *
     * @param instanceSupplier the instance supplier from parents.
     * @param argumentValue the argument value.
     * @return an instance of this command for execution.
     */
    private @NotNull Object createInstanceWithArgument(@Nullable final Supplier<Object> instanceSupplier, @Nullable final Object argumentValue) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // Non-static classes required parent instance
        if (!this.isStatic) {
            return this.constructor.newInstance(instanceSupplier == null ? this.invocationInstance : instanceSupplier.get(), argumentValue);
        }

        return this.constructor.newInstance(argumentValue);
    }

    private @NotNull String createSyntax(@NotNull final Command parentCommand, @NotNull final CommandProcessor<D, S> processor) {
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
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getDescription() {
        return this.description;
    }

    @Override
    public @NotNull List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public @NotNull String getSyntax() {
        return this.syntax;
    }

    @Override
    public boolean hasArguments() {
        return this.argument != null;
    }
}