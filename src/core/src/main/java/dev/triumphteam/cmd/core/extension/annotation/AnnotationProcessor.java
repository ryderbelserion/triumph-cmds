package dev.triumphteam.cmd.core.extension.annotation;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public interface AnnotationProcessor<A extends Annotation> {

    void process(
            @NonNull final A annotation,
            @NonNull final ProcessorTarget target,
            @NonNull final AnnotatedElement element,
            final CommandMeta.@NonNull Builder meta
    );
}