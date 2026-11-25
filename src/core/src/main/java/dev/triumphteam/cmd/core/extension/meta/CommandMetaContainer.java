package dev.triumphteam.cmd.core.extension.meta;

import org.jspecify.annotations.NonNull;

/**
 * Something that can hold a {@link CommandMeta} instance.
 */
public interface CommandMetaContainer {

    /**
     * @return the {@link CommandMeta} of the container.
     */
    @NonNull CommandMeta getMeta();
}