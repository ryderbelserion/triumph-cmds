package dev.triumphteam.cmd.core;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class can be extended to allow customizing the command without using annotations.
 */
public abstract class AnnotatedCommand {

    private final List<String> alias = new ArrayList<>();
    private final String description;
    private final String command;

    public AnnotatedCommand() {
        this(null, null, null);
    }

    public AnnotatedCommand(@Nullable final List<String> alias) {
        this(null, alias, null);
    }

    public AnnotatedCommand(@Nullable final String command) {
        this(command, null, null);
    }

    public AnnotatedCommand(@Nullable final String command, @Nullable final String description) {
        this(command, null, description);
    }

    public AnnotatedCommand(
            @Nullable final String command,
            @Nullable final List<String> alias,
            @Nullable final String description
    ) {
        this.command = command;
        this.description = description;

        if (alias != null) {
            this.alias.addAll(alias);
        }
    }

    /**
     * Gets the command name.
     *
     * @return The {@link #command}.
     */
    public @Nullable String getCommand() {
        return this.command;
    }

    /**
     * Gets the list with the aliases for the command.
     *
     * @return The {@link #alias}.
     */
    public @NonNull List<String> getAlias() {
        return this.alias;
    }

    /**
     * Gets the description of the command.
     *
     * @return The {@link #description}.
     */
    public @NonNull String getDescription() {
        return this.description == null ? "" : this.description;
    }
}