package dev.triumphteam.cmd.core.extension;

import org.jspecify.annotations.NonNull;

public interface ValidationResult<E> {

    class Valid<E> implements ValidationResult<E> { }

    class Ignore<E> implements ValidationResult<E> { }

    record Invalid<E>(E message) implements ValidationResult<E> {
        public Invalid(@NonNull final E message) {
            this.message = message;
        }
    }
}