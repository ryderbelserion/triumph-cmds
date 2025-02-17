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

import com.ryderbelserion.cmd.core.TriumphProvider;
import dev.triumphteam.cmd.core.enums.Mode;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data holder for the command's permission.
 * Including its default state and a description.
 */
public final class CommandPermission {

    private final List<String> nodes;
    private final Mode permissionDefault;
    private final String description;

    public CommandPermission(
            final @NotNull List<String> nodes,
            final @NotNull String description,
            final @NotNull Mode mode
    ) {
        this.nodes = nodes;
        this.description = description;

        this.permissionDefault = mode;

        register();
    }

    /**
     * Checks whether the {@link Audience} has the (nullable) {@link CommandPermission}.
     * <p>
     * The method simply checks if any of the following two things are {@code true}:
     * <ul>
     *     <li>The permission is {@code null}</li>
     *     <li>The sender has the permission</li>
     * </ul>
     *
     * @param sender the main command sender.
     * @param permission the permission.
     * @return Whether the sender has permission to run the command.
     */
    public static boolean hasPermission(
            final @NotNull Audience sender,
            final @Nullable CommandPermission permission
    ) {
        return permission == null || permission.hasPermission(sender);
    }

    public @NotNull CommandPermission child(
            final @NotNull List<String> nodes,
            final @NotNull String description,
            final @NotNull Mode permissionDefault
    ) {
        final List<String> newNodes = this.nodes.stream()
                .flatMap(parent -> nodes.stream().map(node -> parent + "." + node))
                .collect(Collectors.toList());

        return new CommandPermission(newNodes, description, permissionDefault);
    }

    /**
     * Register the permission to the server.
     */
    public void register() {
        TriumphProvider.getInstance().registerNodes(this.nodes, this.description, this.permissionDefault);
    }

    /**
     * Gets the permission nodes.
     *
     * @return the permission nodes.
     */
    public @NotNull List<String> getNodes() {
        return this.nodes;
    }

    /**
     * Checks if the {@link Audience} has the permission to run the command.
     *
     * @param audience the main command sender.
     * @return whether the sender has permission to run the command.
     */
    public boolean hasPermission(@NotNull final Audience audience) {
        return this.nodes.stream().anyMatch(permission -> TriumphProvider.getInstance().hasPermission(audience, permission));
    }

    @Override
    public String toString() {
        return "CommandPermission{" +
                "nodes=" + this.nodes +
                '}';
    }
}