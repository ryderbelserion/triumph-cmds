package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import org.jspecify.annotations.NonNull;
import java.lang.reflect.Method;
import java.util.List;

public interface SimpleSuggestionHolder<S, ST> {

    @NonNull List<ST> getSuggestions(@NonNull final SuggestionContext<S> context);

    interface Static<ST> extends SimpleSuggestionHolder<Object, ST> {

        boolean contains(@NonNull final String suggestion);

        @NonNull List<ST> getSuggestions();
    }

    class RichResolver<S, ST> implements SimpleSuggestionHolder<S, ST> {

        private final SuggestionResolver<S, ST> resolver;

        public RichResolver(@NonNull final SuggestionResolver<S, ST> resolver) {
            this.resolver = resolver;
        }

        @Override
        public @NonNull List<ST> getSuggestions(@NonNull final SuggestionContext<S> context) {
            return resolver.resolve(context);
        }
    }

    class SimpleResolver<S, ST> implements SimpleSuggestionHolder<S, ST> {

        private final SuggestionResolver.Simple<S> resolver;
        private final SuggestionMapper<ST> mapper;

        public SimpleResolver(
                final SuggestionResolver.@NonNull Simple<S> resolver,
                @NonNull final SuggestionMapper<ST> mapper
        ) {
            this.resolver = resolver;
            this.mapper = mapper;
        }

        @Override
        public @NonNull List<ST> getSuggestions(@NonNull final SuggestionContext<S> context) {
            return this.mapper.map(this.resolver.resolve(context));
        }
    }

    abstract class AbstractLocal<S, ST> implements SimpleSuggestionHolder<S, ST> {

        private final Object invocationInstance;
        private final Method method;
        private final boolean needsContext;

        public AbstractLocal(
                @NonNull final Object invocationInstance,
                @NonNull final Method method,
                final boolean needsContext
        ) {
            this.invocationInstance = invocationInstance;
            this.method = method;
            this.needsContext = needsContext;
        }

        protected @NonNull Object invoke(@NonNull final SuggestionContext<S> context) {
            try {
                if (this.needsContext) {
                    return this.method.invoke(this.invocationInstance, context);
                }

                return this.method.invoke(this.invocationInstance);
            } catch (final Exception e) {
                throw new CommandExecutionException("Failed to create suggestions for method '" + this.method + "'.");
            }
        }
    }

    class SimpleLocal<S, ST> extends AbstractLocal<S, ST> {

        private final SuggestionMapper<ST> mapper;

        public SimpleLocal(
                @NonNull final SuggestionMapper<ST> mapper,
                @NonNull final Object invocationInstance,
                @NonNull final Method method,
                final boolean needsContext
        ) {
            super(invocationInstance, method, needsContext);

            this.mapper = mapper;
        }

        @Override
        public @NonNull List<ST> getSuggestions(@NonNull final SuggestionContext<S> context) {
            // We can make guaranteed assumptions here.
            // Object is List<String> at this point.
            //noinspection unchecked
            return this.mapper.map((List<String>) invoke(context));
        }
    }

    class RichLocal<S, ST> extends AbstractLocal<S, ST> {

        public RichLocal(
                @NonNull final Object invocationInstance,
                @NonNull final Method method,
                final boolean needsContext
        ) {
            super(invocationInstance, method, needsContext);
        }

        @Override
        public @NonNull List<ST> getSuggestions(@NonNull final SuggestionContext<S> context) {
            // We can make guaranteed assumptions here.
            // Object is List<ST> at this point.
            //noinspection unchecked
            return (List<ST>) invoke(context);
        }
    }

    class SimpleStatic<ST> implements Static<ST> {

        private final List<String> suggestions;
        private final List<ST> mapped;

        public SimpleStatic(
                @NonNull final List<String> suggestions,
                @NonNull final List<ST> mapped
        ) {
            this.suggestions = suggestions;
            this.mapped = mapped;
        }

        @Override
        public @NonNull List<ST> getSuggestions(@NonNull final SuggestionContext<Object> context) {
            return getSuggestions();
        }

        @Override
        public boolean contains(@NonNull final String suggestion) {
            return this.suggestions.contains(suggestion);
        }

        @Override
        public @NonNull List<ST> getSuggestions() {
            return this.mapped;
        }
    }

    class RichStatic<ST> implements Static<ST> {

        private final List<ST> suggestions;
        private final List<String> backwardsMapped;

        public RichStatic(
                @NonNull final List<ST> suggestions,
                @NonNull final List<String> backwardsMapped
        ) {
            this.suggestions = suggestions;
            this.backwardsMapped = backwardsMapped;
        }

        @Override
        public @NonNull List<ST> getSuggestions(@NonNull final SuggestionContext<Object> context) {
            return getSuggestions();
        }

        @Override
        public boolean contains(@NonNull final String suggestion) {
            return this.backwardsMapped.contains(suggestion);
        }

        @Override
        public @NonNull List<ST> getSuggestions() {
            return this.suggestions;
        }
    }
}