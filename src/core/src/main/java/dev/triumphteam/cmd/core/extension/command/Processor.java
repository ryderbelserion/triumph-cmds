package dev.triumphteam.cmd.core.extension.command;

import dev.triumphteam.cmd.core.extension.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;
import java.lang.reflect.AnnotatedElement;

public interface Processor<D, S> {

    void process(
            @NonNull final AnnotatedElement element,
            @NonNull final ProcessorTarget target,
            final CommandMeta.@NonNull Builder meta,
            final Settings.@NonNull Builder<D, S> settingsBuilder
    );
}