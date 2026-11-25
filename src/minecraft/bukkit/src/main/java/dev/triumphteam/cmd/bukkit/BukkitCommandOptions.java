package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.defaults.DefaultArgumentValidator;
import dev.triumphteam.cmd.core.extension.defaults.DefaultCommandExecutor;
import dev.triumphteam.cmd.core.extension.defaults.DefaultSuggestionMapper;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jspecify.annotations.NonNull;
import java.util.List;

public final class BukkitCommandOptions<S> extends CommandOptions<BukkitCommandOptions<S>, BukkitCommandManager<S>, CommandSender, S, String> {

    public BukkitCommandOptions(
            @NonNull final SenderExtension<CommandSender, S> senderExtension,
            @NonNull final Builder<S> builder
    ) {
        super(senderExtension, builder);
    }

    public static final class Builder<S> extends CommandOptions.Builder<Builder<S>, BukkitCommandManager<S>, BukkitCommandOptions<S>, CommandSender, S, String> {

        private CommandPermission globalPermission = null;

        public Builder() {
            // Setters have to be done first thing, so they can be overridden.
            extensions(extension -> {
                extension.setArgumentValidator(new DefaultArgumentValidator<>());
                extension.setCommandExecutor(new DefaultCommandExecutor<>());
                extension.setSuggestionMapper(new DefaultSuggestionMapper());
            });
        }

        @Override
        protected @NonNull Builder<S> getThis() {
            return this;
        }

        /**
         * Set a {@link CommandPermission} that'll apply to all commands registered by this manager.
         *
         * @param commandPermission The permission to be globally used.
         * @return This {@link Builder}.
         */
        public Builder<S> setGlobalPermission(@NonNull final CommandPermission commandPermission) {
            this.globalPermission = commandPermission;

            return this;
        }

        /**
         * Set a {@link CommandPermission} that'll apply to all commands registered by this manager.
         *
         * @param nodes             Permission nodes to be used.
         * @param description       A description for the command.
         * @param permissionDefault The {@link PermissionDefault} to be used when registering the permission.
         * @return This {@link Builder}.
         */
        public Builder<S> setGlobalPermission(
                @NonNull final List<String> nodes,
                @NonNull final String description,
                @NonNull final PermissionDefault permissionDefault
        ) {
            return setGlobalPermission(new CommandPermission(nodes, description, permissionDefault));
        }

        @NonNull BukkitCommandOptions<S> build(@NonNull final SenderExtension<CommandSender, S> senderExtension) {
            // Add permissions
            extensions(extension -> extension.addProcessor(new PermissionProcessor<>(this.globalPermission)));

            return new BukkitCommandOptions<>(senderExtension, this);
        }
    }
}