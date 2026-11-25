package dev.triumphteam.cmd.core.suggestion;

import org.jspecify.annotations.NonNull;
import java.util.List;

/**
 * Functional interface to allow simple requirement suggestion without the use of any hard coded data.e command sender type.
 */
@FunctionalInterface
public interface SuggestionResolver<S, ST> {

    @NonNull List<ST> resolve(@NonNull final SuggestionContext<S> context);

    @FunctionalInterface
    interface Simple<S> extends SuggestionResolver<S, String> {

        @Override
        @NonNull List<String> resolve(@NonNull final SuggestionContext<S> context);
    }
}