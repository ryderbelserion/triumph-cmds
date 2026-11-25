package dev.triumphteam.cmd.core.extension.defaults;

import dev.triumphteam.cmd.core.annotations.Async;
import dev.triumphteam.cmd.core.extension.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extension.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;
import java.lang.reflect.AnnotatedElement;

public final class AsyncAnnotationProcessor implements AnnotationProcessor<Async> {

    @Override
    public void process(
            @NonNull final Async annotation,
            @NonNull final ProcessorTarget target,
            @NonNull final AnnotatedElement element,
            final CommandMeta.@NonNull Builder meta
    ) {
        if (target != ProcessorTarget.COMMAND) return;

        meta.add(Async.META_KEY);
    }
}