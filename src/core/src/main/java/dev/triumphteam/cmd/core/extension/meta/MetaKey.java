package dev.triumphteam.cmd.core.extension.meta;

import dev.triumphteam.cmd.core.extension.StringKey;
import org.jspecify.annotations.NonNull;

/**
 * Identifier for a specific meta value.
 *
 * @param <V> The type of the meta value.
 */
public final class MetaKey<V> extends StringKey {

    public static final MetaKey<String> NAME = new MetaKey<>("command.name", String.class);
    public static final MetaKey<String> DESCRIPTION = new MetaKey<>("command.description", String.class);

    private final Class<V> valueType;

    private MetaKey(
            @NonNull final String key,
            @NonNull final Class<V> valueType
    ) {
        super(key);

        this.valueType = valueType;
    }

    /**
     * Factory method for creating a {@link MetaKey}.
     *
     * @param key The value of the key, normally separated by <code>.</code>.
     * @return A new {@link MetaKey}.
     */
    public static <V> @NonNull MetaKey<V> of(@NonNull final String key, @NonNull final Class<V> valueType) {
        return new MetaKey<>(key, valueType);
    }

    @Override
    public @NonNull String toString() {
        return "MetaKey{super=" + super.toString() + "}";
    }
}