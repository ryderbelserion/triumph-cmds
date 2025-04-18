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
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.enums.Mode;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.defaults.DefaultArgumentValidator;
import dev.triumphteam.cmd.core.extension.defaults.DefaultCommandExecutor;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.CommandPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public final class BukkitCommandOptions<S> extends CommandOptions<CommandSender, S> {

    public BukkitCommandOptions(
            @NotNull final SenderExtension<CommandSender, S> senderExtension,
            @NotNull final Builder<S> builder
    ) {
        super(senderExtension, builder);
    }

    public static final class Setup<S> extends CommandOptions.Setup<CommandSender, S, Setup<S>> {
        public Setup(@NotNull final RegistryContainer<CommandSender, S> registryContainer) {
            super(registryContainer);
        }
    }

    public static final class Builder<S> extends CommandOptions.Builder<CommandSender, S, BukkitCommandOptions<S>, Setup<S>, Builder<S>> {

        private CommandPermission globalPermission = null;

        public Builder(@NotNull final RegistryContainer<CommandSender, S> registryContainer) {
            super(new Setup<>(registryContainer));

            // Setters have to be done first thing, so they can be overridden.
            extensions(extension -> {
                extension.setArgumentValidator(new DefaultArgumentValidator<>());
                extension.setCommandExecutor(new DefaultCommandExecutor());
            });
        }

        /**
         * Set a {@link CommandPermission} that'll apply to all commands registered by this manager.
         *
         * @param commandPermission The permission to be globally used.
         * @return This {@link Builder}.
         */
        public Builder<S> setGlobalPermission(@NotNull final CommandPermission commandPermission) {
            this.globalPermission = commandPermission;

            return this;
        }

        /**
         * Set a {@link CommandPermission} that'll apply to all commands registered by this manager.
         *
         * @param nodes permission nodes to be used.
         * @param description a description for the command.
         * @param permissionDefault the {@link PermissionDefault} to be used when registering the permission.
         * @return this {@link Builder}.
         */
        public Builder<S> setGlobalPermission(
                @NotNull final List<String> nodes,
                @NotNull final String description,
                @NotNull final Mode permissionDefault
        ) {
            return setGlobalPermission(new CommandPermission(nodes, description, permissionDefault));
        }

        @Override
        public @NotNull BukkitCommandOptions<S> build(@NotNull final SenderExtension<CommandSender, S> senderExtension) {
            // Add permissions
            extensions(extension -> extension.addProcessor(new PermissionProcessor<>(globalPermission)));

            return new BukkitCommandOptions<>(senderExtension, this);
        }
    }
}