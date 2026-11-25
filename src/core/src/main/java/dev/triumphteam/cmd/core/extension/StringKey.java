package dev.triumphteam.cmd.core.extension;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Objects;

/**
 * Registry key, for more organized way of registering and getting things from the registries.
 */
public abstract class StringKey {

    private final String key;

    public StringKey(@NonNull final String key) {
        this.key = key;
    }

    /**
     * Gets the key value.
     *
     * @return The key value.
     */
    public @NonNull String getKey() {
        return key;
    }

    @Override
    public boolean equals(@Nullable final Object object) {
        if (this == object) return true;

        if (object == null || getClass() != object.getClass()) return false;

        final StringKey that = (StringKey) object;

        return this.key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key);
    }

    @Override
    public @NonNull String toString() {
        return "RegistryKey{" +
                "key='" + this.key + '\'' +
                '}';
    }
}
