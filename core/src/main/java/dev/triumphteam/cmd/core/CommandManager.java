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
package dev.triumphteam.cmd.core;

import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import dev.triumphteam.cmd.core.argument.keyed.FlagKey;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.message.ContextualKey;
import dev.triumphteam.cmd.core.message.MessageResolver;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.requirement.RequirementResolver;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * Base command manager for all platforms.
 *
 * @param <D> the default sender type.
 * @param <S> the sender type.
 */
public abstract class CommandManager<D, S, O extends CommandOptions<D, S>> {

    private final O commandOptions;

    public CommandManager(@NotNull final O commandOptions) {
        this.commandOptions = commandOptions;
    }

    /**
     * Registers a command into the manager.
     *
     * @param command the instance of the command to be registered.
     */
    public abstract void registerCommand(@NotNull final Object command);

    /**
     * Registers commands.
     *
     * @param commands a list of commands to be registered.
     */
    public final void registerCommand(@NotNull final Object @NotNull ... commands) {
        for (final Object command : commands) {
            registerCommand(command);
        }
    }

    /**
     * Main method for unregistering commands to be implemented in other platform command managers.
     *
     * @param command the command to be unregistered.
     */
    public void unregisterCommand(@NotNull final Object command) {
        // note, not possible on paper servers when using lifecycle event manager, the registry is frozen.
    }

    /**
     * Method to unregister commands with vararg.
     *
     * @param commands a list of commands to be unregistered.
     */
    public final void unregisterCommands(@NotNull final Object @NotNull ... commands) {
        for (final Object command : commands) {
            unregisterCommand(command);
        }
    }

    /**
     * Registers a custom argument.
     *
     * @param clazz the class of the argument to be registered.
     * @param resolver the {@link ArgumentResolver} with the argument resolution.
     */
    public final void registerArgument(@NotNull final Class<?> clazz, @NotNull final ArgumentResolver<S> resolver) {
        getRegistryContainer().getArgumentRegistry().register(clazz, resolver);
    }

    public final void registerArgument(@NotNull final Class<?> clazz, @NotNull final InternalArgument.Factory<S> factory) {
        getRegistryContainer().getArgumentRegistry().register(clazz, factory);
    }

    /**
     * Registers a suggestion to be used for specific arguments.
     *
     * @param key the {@link SuggestionKey} that identifies the registered suggestion.
     * @param resolver the {@link SuggestionResolver} with the suggestion resolution.
     */
    public void registerSuggestion(@NotNull final SuggestionKey key, @NotNull final SuggestionResolver<S> resolver) {
        registerSuggestion(key, SuggestionMethod.STARTS_WITH, resolver);
    }

    /**
     * Registers a suggestion to be used for specific arguments.
     *
     * @param key the {@link SuggestionKey} that identifies the registered suggestion.
     * @param method the resolution method to use for suggestions.
     * @param resolver the {@link SuggestionResolver} with the suggestion resolution.
     */
    public void registerSuggestion(
            @NotNull final SuggestionKey key,
            @NotNull final SuggestionMethod method,
            @NotNull final SuggestionResolver<S> resolver
    ) {
        getRegistryContainer().getSuggestionRegistry().register(key, resolver, method);
    }

    /**
     * Registers a suggestion to be used for all arguments of a specific type.
     *
     * @param type using specific {@link Class} types as target for suggestions instead of keys.
     * @param resolver the {@link SuggestionResolver} with the suggestion resolution.
     */
    public void registerSuggestion(@NotNull final Class<?> type, @NotNull final SuggestionResolver<S> resolver) {
        registerSuggestion(type, SuggestionMethod.STARTS_WITH, resolver);
    }

    /**
     * Registers a suggestion to be used for all arguments of a specific type.
     *
     * @param type using specific {@link Class} types as target for suggestions instead of keys.
     * @param method the resolution method to use for suggestions.
     * @param resolver the {@link SuggestionResolver} with the suggestion resolution.
     */
    public void registerSuggestion(
            @NotNull final Class<?> type,
            @NotNull final SuggestionMethod method,
            @NotNull final SuggestionResolver<S> resolver
    ) {
        getRegistryContainer().getSuggestionRegistry().register(type, resolver, method);
    }

    /**
     * Registers a list of arguments to be used as named arguments in a command.
     *
     * @param key the {@link ArgumentKey} to represent the list.
     * @param arguments the list of arguments.
     */
    public final void registerNamedArguments(@NotNull final ArgumentKey key, @NotNull final Argument @NotNull ... arguments) {
        registerNamedArguments(key, Arrays.asList(arguments));
    }

    /**
     * Registers a list of arguments to be used on a {@link Arguments} argument in a command.
     *
     * @param key the {@link ArgumentKey} to represent the list.
     * @param arguments the {@link List} of arguments.
     */
    public final void registerNamedArguments(@NotNull final ArgumentKey key, @NotNull final List<Argument> arguments) {
        getRegistryContainer().getNamedArgumentRegistry().register(key, arguments);
    }

    /**
     * Registers a list of flags to be used on a {@link Flags} argument or {@link Arguments} argument, in a command.
     *
     * @param key   the {@link FlagKey} to represent the list.
     * @param flags the list of flags.
     */
    public final void registerFlags(@NotNull final FlagKey key, @NotNull final Flag @NotNull ... flags) {
        registerFlags(key, Arrays.asList(flags));
    }

    /**
     * Registers a list of flags to be used on a {@link Flags} argument or {@link Arguments} argument, in a command.
     *
     * @param key the {@link FlagKey} to represent the list.
     * @param flags the {@link List} of flags.
     */
    public final void registerFlags(@NotNull final FlagKey key, @NotNull final List<Flag> flags) {
        getRegistryContainer().getFlagRegistry().register(key, flags);
    }

    /**
     * Registers a custom message.
     *
     * @param key the {@link ContextualKey} of the message to be registered.
     * @param resolver the {@link ArgumentResolver} with the message sending resolution.
     */
    public final <C extends MessageContext> void registerMessage(
            @NotNull final ContextualKey<C> key,
            @NotNull final MessageResolver<S, C> resolver
    ) {
        getRegistryContainer().getMessageRegistry().register(key, resolver);
    }

    /**
     * Registers a requirement.
     *
     * @param key the {@link RequirementKey} of the requirement to be registered.
     * @param resolver the {@link ArgumentResolver} with the requirement resolution.
     */
    public final void registerRequirement(
            @NotNull final RequirementKey key,
            @NotNull final RequirementResolver<D, S> resolver
    ) {
        getRegistryContainer().getRequirementRegistry().register(key, resolver);
    }

    protected abstract @NotNull RegistryContainer<D, S> getRegistryContainer();

    protected @NotNull O getCommandOptions() {
        return this.commandOptions;
    }
}