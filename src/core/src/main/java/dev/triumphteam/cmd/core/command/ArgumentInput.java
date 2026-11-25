package dev.triumphteam.cmd.core.command;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record ArgumentInput(String input, Object provided) {

    public ArgumentInput(@NonNull final String input) {
        this(input, null);
    }

    public ArgumentInput(@NonNull final String input, @Nullable final Object provided) {
        this.input = input;
        this.provided = provided;
    }

    @Override
    public @NonNull String toString() {
        return "SuppliedArgument{" +
                "input='" + this.input + '\'' +
                ", provided=" + this.provided +
                '}';
    }
}