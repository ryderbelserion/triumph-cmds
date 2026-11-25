package dev.triumphteam.cmd.core.processor;

import dev.triumphteam.cmd.core.annotations.Requirement;
import dev.triumphteam.cmd.core.annotations.Requirements;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extension.CommandExtensions;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extension.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extension.registry.RequirementRegistry;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.requirement.InternalRequirement;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.requirement.RequirementResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface CommandProcessor<D, S, ST> {

    /**
     * Create a new meta and handle some processing before it's fully created.
     *
     * @return The immutable {@link CommandMeta} instance.
     */
    @NonNull CommandMeta createMeta(final Settings.@NonNull Builder<D, S> settingsBuilder);

    @NonNull CommandOptions<?, ?, D, S, ST> getCommandOptions();

    @NonNull RegistryContainer<D, S, ST> getRegistryContainer();

    @NonNull AnnotatedElement getAnnotatedElement();

    @Nullable Syntax getSyntaxAnnotation();

    /**
     * Process-all annotations for the specific {@link AnnotatedElement}.
     *
     * @param extensions The main extensions to get the processors from.
     * @param element    The annotated element to process its annotations.
     * @param target     The target of the annotation.
     * @param meta       The meta-builder that'll be passed to processors.
     */
    @SuppressWarnings("unchecked")
    default void processAnnotations(
            @NonNull final CommandExtensions<D, S, ST> extensions,
            @NonNull final AnnotatedElement element,
            @NonNull final ProcessorTarget target,
            final CommandMeta.@NonNull Builder meta
    ) {
        final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> processors = extensions.getAnnotationProcessors();

        for (final Annotation annotation : element.getAnnotations()) {
            //noinspection rawtypes
            final AnnotationProcessor annotationProcessor = processors.get(annotation.annotationType());

            // No processors available
            if (annotationProcessor == null) continue;

            annotationProcessor.process(annotation, target, element, meta);
        }
    }

    default void captureRequirements(final Settings.@NonNull Builder<D, S> settingsBuilder) {
        final RequirementRegistry<D, S> requirementRegistry = getRegistryContainer().getRequirementRegistry();

        for (final dev.triumphteam.cmd.core.annotations.Requirement requirementAnnotation : getRequirementsFromAnnotations()) {
            final RequirementKey requirementKey = RequirementKey.of(requirementAnnotation.value());
            final String messageKeyValue = requirementAnnotation.messageKey();

            final MessageKey<MessageContext> messageKey = MessageKey.of(messageKeyValue, MessageContext.class);

            final RequirementResolver<D, S> resolver = requirementRegistry.getRequirement(requirementKey);

            if (resolver == null) {
                // TODO EXCEPTION CHECK
                throw new CommandRegistrationException("Could not find Requirement Key \"" + requirementKey.getKey() + "\"");
            }

            settingsBuilder.addRequirement(new InternalRequirement<>(resolver, messageKey, requirementAnnotation.invert()));
        }
    }

    default void processCommandMeta(
            @NonNull final CommandExtensions<D, S, ST> extensions,
            @NonNull final AnnotatedElement element,
            @NonNull final ProcessorTarget target,
            final CommandMeta.@NonNull Builder meta,
            final Settings.@NonNull Builder<D, S> settingsBuilder
    ) {
        extensions.getProcessors().forEach(it -> it.process(element, target, meta, settingsBuilder));
    }

    default @NonNull List<@NonNull Requirement> getRequirementsFromAnnotations() {
        final AnnotatedElement element = getAnnotatedElement();

        final Requirements requirements = element.getAnnotation(Requirements.class);

        if (requirements != null) return Arrays.asList(requirements.value());

        final Requirement requirement = element.getAnnotation(Requirement.class);

        if (requirement == null) return Collections.emptyList();

        return Collections.singletonList(requirement);
    }
}