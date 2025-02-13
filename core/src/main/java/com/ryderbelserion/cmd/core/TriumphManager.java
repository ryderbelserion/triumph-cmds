package com.ryderbelserion.cmd.core;

import dev.triumphteam.cmd.core.enums.Mode;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public interface TriumphManager {

    void registerNodes(@NotNull final List<String> nodes, @NotNull final String description, @NotNull final Mode mode);

    boolean hasPermission(@NotNull final Audience audience, @NotNull final String permission);

}