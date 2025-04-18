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
package dev.triumphteam.cmd.core.processor;

import com.google.common.base.CaseFormat;
import dev.triumphteam.cmd.core.AnnotatedCommand;
import dev.triumphteam.cmd.core.annotations.Description;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentGroup;
import dev.triumphteam.cmd.core.command.Command;
import dev.triumphteam.cmd.core.command.ParentSubCommand;
import dev.triumphteam.cmd.core.command.SubCommand;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.meta.MetaKey;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@SuppressWarnings("unchecked")
public class RootCommandProcessor<D, S> implements CommandProcessor<D, S> {

    private final Object invocationInstance;

    private final String name;
    private final Syntax syntax;
    private final List<String> aliases;
    private final String description;

    private final CommandOptions<D, S> commandOptions;
    private final RegistryContainer<D, S> registryContainer;

    public RootCommandProcessor(
            @NotNull final Object invocationInstance,
            @NotNull final RegistryContainer<D, S> registryContainer,
            @NotNull final CommandOptions<D, S> commandOptions
    ) {
        this.invocationInstance = invocationInstance;

        this.name = nameOf();
        this.aliases = aliasesOf();
        this.description = descriptionOf();

        this.registryContainer = registryContainer;
        this.commandOptions = commandOptions;

        this.syntax = invocationInstance.getClass().getAnnotation(Syntax.class);
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull List<String> getAliases() {
        return this.aliases;
    }

    public @NotNull String getDescription() {
        return this.description;
    }

    @Override
    public @NotNull CommandOptions<D, S> getCommandOptions() {
        return this.commandOptions;
    }

    @Override
    public @NotNull RegistryContainer<D, S> getRegistryContainer() {
        return this.registryContainer;
    }

    @Override
    public @Nullable Syntax getSyntaxAnnotation() {
        return this.syntax;
    }

    @Override
    public @NotNull AnnotatedElement getAnnotatedElement() {
        return this.invocationInstance.getClass();
    }

    @Override
    public @NotNull CommandMeta createMeta(@NotNull final Settings.@NotNull Builder<D, S> settingsBuilder) {
        final CommandMeta.Builder meta = new CommandMeta.Builder(null);

        // Defaults
        meta.add(MetaKey.NAME, getName());
        meta.add(MetaKey.DESCRIPTION, getDescription());

        // Process all the class annotations
        final Class<?> klass = this.invocationInstance.getClass();

        processAnnotations(this.commandOptions.getCommandExtensions(), klass, ProcessorTarget.ROOT_COMMAND, meta);

        processCommandMeta(
                this.commandOptions.getCommandExtensions(),
                klass,
                ProcessorTarget.PARENT_COMMAND,
                meta,
                settingsBuilder
        );

        // Return modified meta
        return meta.build();
    }

    public @NotNull List<Command<D, S>> commands(@NotNull final Command<D, S> parentCommand) {
        final Class<?> klass = this.invocationInstance.getClass();

        final List<Command<D, S>> subCommands = new ArrayList<>();

        subCommands.addAll(methodCommands(parentCommand, klass.getDeclaredMethods()));
        subCommands.addAll(classCommands(parentCommand, klass.getDeclaredClasses()));

        return subCommands;
    }

    private @NotNull List<Command<D, S>> methodCommands(
            @NotNull final Command<D, S> parentCommand,
            @NotNull final Method[] methods
    ) {
        final List<Command<D, S>> commands = new ArrayList<>();
        for (final Method method : methods) {
            // Ignore non-public methods
            if (!Modifier.isPublic(method.getModifiers())) continue;

            final SubCommandProcessor<D, S> processor = new SubCommandProcessor<>(
                    this.invocationInstance,
                    method,
                    this.registryContainer,
                    this.commandOptions,
                    parentCommand.getMeta()
            );

            // Not a command, ignore the method
            if (processor.getName() == null) continue;

            // Add new command
            commands.add(new SubCommand<>(this.invocationInstance, method, processor, parentCommand));
        }

        return commands;
    }

    private @NotNull List<Command<D, S>> classCommands(
            @NotNull final Command<D, S> parentCommand,
            @NotNull final Class<?>[] classes
    ) {
        final List<Command<D, S>> commands = new ArrayList<>();

        for (final Class<?> klass : classes) {
            // Ignore non-public methods
            if (!Modifier.isPublic(klass.getModifiers())) continue;

            final ParentCommandProcessor<D, S> processor = new ParentCommandProcessor<>(
                    this.invocationInstance,
                    klass,
                    this.registryContainer,
                    this.commandOptions,
                    parentCommand.getMeta()
            );

            // Not a command, ignore the method
            if (processor.getName() == null) continue;

            // Validation for allowed constructor
            final Constructor<?>[] constructors = klass.getConstructors();

            if (constructors.length != 1) {
                throw new CommandRegistrationException("Inner command class can only have a single constructor, " + constructors.length + " found", klass);
            }

            // Validation for allowed arguments
            final Constructor<?> constructor = constructors[0];
            final Parameter[] parameters = constructor.getParameters();

            final boolean isStatic = Modifier.isStatic(klass.getModifiers());
            final int arguments = (isStatic ? parameters.length : parameters.length - 1);
            final boolean hasArgument = arguments != 0;

            if (arguments > 1) {
                throw new CommandRegistrationException("Inner command class can only have a maximum of 1 argument, " + arguments + " found", klass);
            }

            final InternalArgument<S, ?> argument;

            if (!hasArgument) {
                argument = null;
            } else {
                if (!dev.triumphteam.cmd.core.annotations.Command.DEFAULT_CMD_NAME.equals(processor.getName())) {
                    throw new CommandRegistrationException("Inner command class with argument must not have a name", klass);
                }

                final Parameter parameter = isStatic ? parameters[0] : parameters[1];

                final CommandMeta.Builder meta = new CommandMeta.Builder(null);

                processAnnotations(getCommandOptions().getCommandExtensions(), parameter, ProcessorTarget.ARGUMENT, meta);

                argument = processor.argumentFromParameter(
                        meta.build(),
                        parameter,
                        emptyList(),
                        emptyMap(),
                        ArgumentGroup.flags(emptyList()),
                        ArgumentGroup.named(emptyList()),
                        0
                );

                if (!(argument instanceof StringInternalArgument)) {
                    throw new CommandRegistrationException("Inner command class with argument must not be limitless, only single string argument is allowed", klass);
                }
            }

            final ParentSubCommand<D, S> parent = new ParentSubCommand<>(
                    this.invocationInstance,
                    constructor,
                    isStatic,
                    (StringInternalArgument<S>) argument,
                    processor,
                    parentCommand
            );

            // Add children commands to parent
            parent.addCommands(this.invocationInstance, methodCommands(parent, klass.getDeclaredMethods()));
            parent.addCommands(this.invocationInstance, classCommands(parentCommand, klass.getDeclaredClasses()));

            // Add parent command to main list
            commands.add(parent);
        }

        return commands;
    }

    private @NotNull String nameOf() {
        final Class<?> commandClass = this.invocationInstance.getClass();
        final dev.triumphteam.cmd.core.annotations.Command commandAnnotation = commandClass.getAnnotation(dev.triumphteam.cmd.core.annotations.Command.class);

        String name = null;

        if (commandAnnotation != null) {
            name = commandAnnotation.value();
        } else if (AnnotatedCommand.class.isAssignableFrom(commandClass)) {
            name = ((AnnotatedCommand) this.invocationInstance).getCommand();
        }

        if (name == null) {
            throw new CommandRegistrationException("No \"@" + Command.class.getSimpleName() + "\" annotation found or class doesn't extend \"" + AnnotatedCommand.class.getSimpleName() + "\"", invocationInstance.getClass());
        }

        if (name.isEmpty() || name.equals(dev.triumphteam.cmd.core.annotations.Command.DEFAULT_CMD_NAME)) {
            throw new CommandRegistrationException("Command name must not be empty", this.invocationInstance.getClass());
        }

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name);
    }

    private @NotNull List<String> aliasesOf() {
        final Class<?> commandClass = this.invocationInstance.getClass();
        final dev.triumphteam.cmd.core.annotations.Command commandAnnotation = commandClass.getAnnotation(dev.triumphteam.cmd.core.annotations.Command.class);

        List<String> aliases = null;

        if (commandAnnotation != null) {
            aliases = Arrays.asList(commandAnnotation.alias());
        } else if (AnnotatedCommand.class.isAssignableFrom(commandClass)) {
            aliases = ((AnnotatedCommand) this.invocationInstance).getAlias();
        }

        return aliases == null ? emptyList() : aliases.stream()
                .map(name -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name))
                .collect(Collectors.toList());
    }

    private @NotNull String descriptionOf() {
        final Class<?> commandClass = this.invocationInstance.getClass();
        final Description descriptionAnnotation = commandClass.getAnnotation(Description.class);

        String description = "";

        if (descriptionAnnotation != null) {
            description = descriptionAnnotation.value();
        } else if (AnnotatedCommand.class.isAssignableFrom(commandClass)) {
            description = ((AnnotatedCommand) this.invocationInstance).getDescription();
        }

        return description;
    }
}
