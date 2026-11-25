package dev.triumphteam.cmd.core.extension.meta;

import dev.triumphteam.cmd.core.command.InternalCommand;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Storage for custom data stored in a {@link InternalCommand} or argument.
 * Meta is only mutable during its {@link Builder} state, once it's fully built it'll be fully immutable.
 */
public interface CommandMeta {

    /**
     * Get the value of the meta associated with the passed {@link MetaKey}.
     *
     * @param metaKey The {@link MetaKey} associated with the value.
     * @param <V>     The type of the value to cast to.
     * @return An {@link Optional} value as {@link V}.
     */
    <V> @NonNull Optional<V> get(@NonNull final MetaKey<V> metaKey);

    /**
     * Get the value of the meta associated with the passed {@link MetaKey}.
     *
     * @param metaKey The {@link MetaKey} associated with the value.
     * @param <V>     The type of the value to cast to.
     * @return A nullable value as {@link V}.
     */
    <V> @Nullable V getNullable(@NonNull final MetaKey<V> metaKey);

    /**
     * Get the value of the meta associated with the passed {@link MetaKey}.
     *
     * @param metaKey The {@link MetaKey} associated with the value.
     * @param def     The default value to return in case the stored value doesn't exist.
     * @param <V>     The type of the value to cast to.
     * @return The value as {@link V} or default {@link V} if it doesn't exist, the return is null if the default value is null.
     */
    <V> V getOrDefault(@NonNull final MetaKey<V> metaKey, @Nullable final V def);

    /**
     * Checks if there is any value associated with the passed {@link MetaKey}.
     *
     * @param metaKey The {@link MetaKey} associated with the value.
     * @param <V>     The type of the value to cast to.
     * @return True if the value exists.
     */
    <V> boolean isPresent(@NonNull final MetaKey<V> metaKey);

    /**
     * Get the immutable parent {@link CommandMeta} of this instance.
     *
     * @return The parent meta of this instance.
     */
    @Nullable CommandMeta getParentMeta();

    /**
     * Simple builder to add to or get from the original meta map because it becomes immutable.
     */
    @SuppressWarnings("unchecked")
    final class Builder implements CommandMeta {

        private final Map<MetaKey<?>, Object> metaMap = new HashMap<>();
        private final CommandMeta parentMeta;

        public Builder(@Nullable final CommandMeta parentMeta) {
            this.parentMeta = parentMeta;
        }

        /**
         * Add the value {@link V} to the map associated by the {@link MetaKey}.
         *
         * @param metaKey The {@link MetaKey} to be the key of the internal map.
         * @param value   The nullable value {@link V} to be stored.
         * @param <V>     The type of value that'll be stored.
         */
        public <V> @NonNull Builder add(@NonNull final MetaKey<V> metaKey, @Nullable final V value) {
            this.metaMap.put(metaKey, value);

            return this;
        }

        /**
         * Add the value {@link V} to the map associated by the {@link MetaKey}.
         * Defaults the value to null.
         *
         * @param metaKey The {@link MetaKey} to be the key of the internal map.
         * @param <V>     The type of value that'll be stored.
         */
        public <V> @NonNull Builder add(@NonNull final MetaKey<V> metaKey) {
            return add(metaKey, null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull <V> Optional<V> get(@NonNull final MetaKey<V> metaKey) {
            return Optional.ofNullable(getNullable(metaKey));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <V> @Nullable V getNullable(@NonNull final MetaKey<V> metaKey) {
            return (V) this.metaMap.get(metaKey);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <V> @Nullable V getOrDefault(@NonNull final MetaKey<V> metaKey, @Nullable final V def) {
            return (V) this.metaMap.getOrDefault(metaKey, def);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <V> boolean isPresent(@NonNull final MetaKey<V> metaKey) {
            return this.metaMap.containsKey(metaKey);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @Nullable CommandMeta getParentMeta() {
            return this.parentMeta;
        }

        /**
         * Creates the final immutable meta.
         * This should not be used externally, but if you need a copy of the current meta state, sure.
         *
         * @return A new {@link CommandMeta} with the current meta map immutable.
         */
        public @NonNull CommandMeta build() {
            return new ImmutableCommandMeta(this.parentMeta, Collections.unmodifiableMap(this.metaMap));
        }
    }
}