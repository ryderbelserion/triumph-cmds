package dev.triumphteam.cmd.core.util;

import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import org.jspecify.annotations.NonNull;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class EnumUtils {

    private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> ENUM_CONSTANT_CACHE = new WeakHashMap<>();

    private EnumUtils() {
        throw new AssertionError("Util must not be initialized");
    }

    /**
     * Slightly modified version from Guava's.
     * Modifications done are to allow capture `?` instead of generic type.
     *
     * @param enumClass A non-generic Enum class.
     * @return A map with enum values that was previously cached.
     */
    public static @NonNull Map<String, @NonNull WeakReference<? extends Enum<?>>> getEnumConstants(@NonNull final Class<? extends Enum<?>> enumClass) {
        synchronized (ENUM_CONSTANT_CACHE) {
            Map<String, WeakReference<? extends Enum<?>>> constants = ENUM_CONSTANT_CACHE.get(enumClass);

            if (constants == null) constants = populateCache(enumClass);

            return constants;
        }
    }

    /**
     * Slightly modified version from Guava's.
     * Modifications done are to allow capture `?` instead of generic type.
     *
     * @param enumClass A non-generic Enum class.
     * @return A map with enum values that was just populated to the cache.
     */
    public static @NonNull Map<String, WeakReference<? extends Enum<?>>> populateCache(@NonNull final Class<? extends Enum<?>> enumClass) {
        final Map<String, WeakReference<? extends Enum<?>>> result = new HashMap<>();

        for (Enum<?> enumInstance : enumClass.getEnumConstants()) {
            final String name = enumInstance.name().toUpperCase();

            if (result.containsKey(name)) {
                throw new CommandRegistrationException("Provided enum \"" + enumClass.getSimpleName() + "\" has multiple values with the name \"" + name + "\"");
            }

            result.put(name, new WeakReference<Enum<?>>(enumInstance));
        }

        ENUM_CONSTANT_CACHE.put(enumClass, result);

        return result;
    }
}
