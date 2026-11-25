package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.meta.CommandMetaContainer;
import org.jspecify.annotations.NonNull;
import java.util.List;

/**
 * Representation of a command.
 * Commands can either be command holders and commands themselves.
 * Some holders are {@link InternalParentCommand}s.
 * And actual commands can be for example {@link InternalLeafCommand}.
 */
public interface InternalCommand<D, S, ST> extends CommandMetaContainer {

    String DEFAULT_CMD_NAME = "th-default";
    String PARENT_CMD_WITH_ARGS_NAME = "th-args-cmd";

    @NonNull Settings<D, S> getCommandSettings();

    /**
     * @return The name of the command.
     */
    @NonNull String getName();

    /**
     * @return The command's description.
     */
    @NonNull String getDescription();

    /**
     * @return A list with all of its aliases.
     */
    @NonNull List<String> getAliases();

    /**
     * @return Whether this is a "default" command, meaning it represents the class itself and is not separate.
     */
    boolean isDefault();

    boolean isHidden();

    /**
     * @return Whether the command has arguments.
     */
    boolean hasArguments();

    /**
     * @return The command's syntax.
     */
    @NonNull String getSyntax();
}