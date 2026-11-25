package dev.triumphteam.cmd.core.argument.keyed;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

class SimpleArgumentValue implements ArgumentValue {

    private final String rawValue;
    private final Object value;

    public SimpleArgumentValue(@NonNull final String rawValue, @NonNull final Object value) {
        this.rawValue = rawValue;
        this.value = value;
    }

    public @Nullable Object getValue() {
        return this.value;
    }

    public @NonNull String getAsString() {
        return this.rawValue;
    }

    @Override
    public String toString() {
        return "Simple{" +
                "rawValue='" + this.rawValue + '\'' +
                ", value=" + this.value +
                '}';
    }
}