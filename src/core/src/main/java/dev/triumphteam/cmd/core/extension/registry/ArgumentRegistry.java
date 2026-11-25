package dev.triumphteam.cmd.core.extension.registry;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * The argument registry holds simple types of all common argument types.
 * Also allows for registration of custom ones.
 * Each platform will introduce their own new ones by default.
 *
 * @param <S> The sender type.
 */
public final class ArgumentRegistry<S, ST> implements Registry {

    private final Map<Class<?>, ArgumentResolver<S>> arguments = new HashMap<>();
    private final Map<Class<?>, InternalArgument.Factory<S, ST>> internals = new HashMap<>();

    public ArgumentRegistry() {
        register(short.class, (_, arg) -> Ints.tryParse(arg));
        register(Short.class, (_, arg) -> Ints.tryParse(arg));

        register(int.class, (_, arg) -> Ints.tryParse(arg));
        register(Integer.class, (_, arg) -> Ints.tryParse(arg));

        register(long.class, (_, arg) -> Longs.tryParse(arg));
        register(Long.class, (_, arg) -> Longs.tryParse(arg));

        register(float.class, (_, arg) -> Floats.tryParse(arg));
        register(Float.class, (_, arg) -> Floats.tryParse(arg));

        register(double.class, (_, arg) -> Doubles.tryParse(arg));
        register(Double.class, (_, arg) -> Doubles.tryParse(arg));

        register(boolean.class, (_, arg) -> Boolean.valueOf(arg));
        register(Boolean.class, (_, arg) -> Boolean.valueOf(arg));

        register(String.class, (_, arg) -> arg);
    }

    /**
     * Registers a new argument type.
     *
     * @param clazz    The {@link Class} type the argument should be.
     * @param argument The {@link ArgumentResolver} with the resolution of the argument.
     */
    public void register(@NonNull final Class<?> clazz, @NonNull final ArgumentResolver<S> argument) {
        this.arguments.put(clazz, argument);
    }

    public void register(@NonNull final Class<?> clazz, final InternalArgument.@NonNull Factory<S, ST> factory) {
        this.internals.put(clazz, factory);
    }

    public @Nullable ArgumentResolver<S> getResolver(@NonNull final Class<?> clazz) {
        return this.arguments.get(clazz);
    }

    public InternalArgument.@Nullable Factory<S, ST> getFactory(@NonNull final Class<?> clazz) {
        return this.internals.get(clazz);
    }
}