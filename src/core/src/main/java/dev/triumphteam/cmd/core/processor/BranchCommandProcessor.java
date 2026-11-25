package dev.triumphteam.cmd.core.processor;

import dev.triumphteam.cmd.core.command.InternalCommand;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.meta.MetaKey;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import org.jspecify.annotations.NonNull;
import java.lang.reflect.AnnotatedElement;

/**
 * Abstracts most of the "extracting" from sub command annotations, allows for extending.
 * <br/>
 * I know this could be done better, but couldn't think of a better way.
 * If you do please PR or let me know on my discord!
 *
 * @param <S> The sender type.
 */
public final class BranchCommandProcessor<D, S, ST> extends AbstractCommandProcessor<D, S, ST> {

    private final Class<?> klass;

    BranchCommandProcessor(
            @NonNull final Object invocationInstance,
            @NonNull final Class<?> klass,
            @NonNull final RegistryContainer<D, S, ST> registryContainer,
            @NonNull final CommandOptions<?, ?, D, S, ST> commandOptions,
            @NonNull final CommandMeta parentMeta
    ) {
        super(invocationInstance, klass, registryContainer, commandOptions, parentMeta);

        this.klass = klass;
    }

    @Override
    protected String defaultCommandName() {
        return InternalCommand.PARENT_CMD_WITH_ARGS_NAME;
    }

    @Override
    public @NonNull AnnotatedElement getAnnotatedElement() {
        return this.klass;
    }

    @Override
    public @NonNull CommandMeta createMeta(final Settings.@NonNull Builder<D, S> settingsBuilder) {
        final CommandMeta.Builder meta = new CommandMeta.Builder(getParentMeta());

        // Defaults
        meta.add(MetaKey.NAME, getName());
        meta.add(MetaKey.DESCRIPTION, descriptionOf());

        // Process all the class annotations
        processAnnotations(getCommandOptions().getCommandExtensions(), this.klass, ProcessorTarget.PARENT_COMMAND, meta);
        processCommandMeta(
                getCommandOptions().getCommandExtensions(),
                this.klass,
                ProcessorTarget.PARENT_COMMAND,
                meta,
                settingsBuilder
        );

        // Return modified meta
        return meta.build();
    }
}
