package dev.triumphteam.cmd.core.suggestion;

import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record SuggestionContext<S>(String input, S sender, List<String> arguments, Map<String, String> argumentsMap, String extra) {

    public SuggestionContext(
            @NonNull final String input,
            @NonNull final S sender,
            @NonNull final List<String> arguments,
            @NonNull final Map<String, String> argumentsMap,
            @NonNull final String extra
    ) {
        this.input = input;
        this.sender = sender;
        this.arguments = arguments;
        this.argumentsMap = argumentsMap;
        this.extra = extra;
    }

    public static <S> @NonNull SuggestionContext<S> of(
            @NonNull final String input,
            @NonNull final S sender,
            @NonNull final List<String> arguments,
            @NonNull final Map<String, String> argumentsMap,
            @NonNull final String extra
    ) {
        return new SuggestionContext<>(input, sender, arguments, argumentsMap, extra);
    }

    public static <S> @NonNull SuggestionContext<S> of(
            @NonNull final String input,
            @NonNull final S sender,
            @NonNull final List<String> arguments,
            @NonNull final Map<String, String> argumentsMap
    ) {
        return new SuggestionContext<>(input, sender, arguments, argumentsMap, "");
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        final SuggestionContext<?> that = (SuggestionContext<?>) object;

        return Objects.equals(this.input, that.input) && Objects.equals(this.sender, that.sender) && Objects.equals(this.arguments, that.arguments) && Objects.equals(this.extra, that.extra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.input, this.sender, this.arguments, this.extra);
    }

    @Override
    public @NonNull String toString() {
        return "SuggestionContext{" +
                "input='" + this.input + '\'' +
                ", sender=" + this.sender +
                ", arguments=" + this.arguments +
                ", extra='" + this.extra + '\'' +
                '}';
    }
}