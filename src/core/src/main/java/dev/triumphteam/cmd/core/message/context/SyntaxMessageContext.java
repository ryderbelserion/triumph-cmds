package dev.triumphteam.cmd.core.message.context;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;

/**
 * The default most keys will use, only contains the most basic data.
 */
public class SyntaxMessageContext extends BasicMessageContext {

    private final String syntax;

    public SyntaxMessageContext(
            @NonNull final CommandMeta meta,
            @NonNull final String syntax
    ) {
        super(meta);

        this.syntax = syntax;
    }

    public @NonNull String getSyntax() {
        return this.syntax;
    }
}