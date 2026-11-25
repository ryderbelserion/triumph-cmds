package dev.triumphteam.cmd.core.extension.meta;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
final class ImmutableCommandMeta implements CommandMeta {

    private final CommandMeta parentMeta;
    private final Map<MetaKey<?>, Object> meta;

    public ImmutableCommandMeta(
            @Nullable final CommandMeta parentMeta,
            @NonNull final Map<MetaKey<?>, Object> meta
    ) {
        this.parentMeta = parentMeta;
        this.meta = meta;
    }

    @Override
    public @NonNull <V> Optional<V> get(@NonNull final MetaKey<V> metaKey) {
        return Optional.ofNullable(getNullable(metaKey));
    }

    @Override
    public <V> @Nullable V getNullable(@NonNull final MetaKey<V> metaKey) {
        return (V) this.meta.get(metaKey);
    }

    @Override
    public <V> V getOrDefault(@NonNull final MetaKey<V> metaKey, @Nullable final V def) {
        return (V) this.meta.getOrDefault(metaKey, def);
    }

    @Override
    public <V> boolean isPresent(@NonNull final MetaKey<V> metaKey) {
        return this.meta.containsKey(metaKey);
    }

    @Override
    public @Nullable CommandMeta getParentMeta() {
        return this.parentMeta;
    }

    @Override
    public String toString() {
        return "ImmutableCommandMeta{" +
                "meta=" + this.meta +
                '}';
    }
}