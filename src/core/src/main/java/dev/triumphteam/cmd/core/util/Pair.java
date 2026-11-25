package dev.triumphteam.cmd.core.util;

import org.jspecify.annotations.NonNull;

public record Pair<A, B>(A first, B second) {

    @Override
    public @NonNull String toString() {
        return "(" + this.first + "," + this.second + ")";
    }
}