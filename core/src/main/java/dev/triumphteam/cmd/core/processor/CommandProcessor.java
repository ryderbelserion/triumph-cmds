/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.processor;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface CommandProcessor<D, S> {

    /**
     * Create a new meta and handle some processing before it's fully created.
     *
     * @return the immutable {@link CommandMeta} instance.
     */
    @NotNull CommandMeta createMeta(@NotNull final Settings.Builder<D, S> settingsBuilder);

    @NotNull CommandOptions<D, S> getCommandOptions();

    @NotNull RegistryContainer<D, S> getRegistryContainer();

    @NotNull AnnotatedElement getAnnotatedElement();

    @Nullable Syntax getSyntaxAnnotation();

    /**
     * Process all annotations for the specific {@link AnnotatedElement}.
     *
     * @param extensions the main extensions to get the processors from.
     * @param element the annotated element to process its annotations.
     * @param target the target of the annotation.
     * @param meta the meta builder that'll be passed to processors.
     */
    @SuppressWarnings("unchecked")
    default void processAnnotations(
            @NotNull final CommandExtensions<?, ?> extensions,
            @NotNull final AnnotatedElement element,
            @NotNull final ProcessorTarget target,
            @NotNull final CommandMeta.Builder meta
    ) {
        final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> processors
                = extensions.getAnnotationProcessors();

        for (final Annotation annotation : element.getAnnotations()) {
            final AnnotationProcessor annotationProcessor = processors.get(annotation.annotationType());

            // No processors available
            if (annotationProcessor == null) continue;

            annotationProcessor.process(annotation, target, element, meta);
        }
    }

    default void captureRequirements(@NotNull final Settings.Builder<D, S> settingsBuilder) {
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
            @NotNull final CommandExtensions<D, S> extensions,
            @NotNull final AnnotatedElement element,
            @NotNull final ProcessorTarget target,
            @NotNull final CommandMeta.Builder meta,
            @NotNull final Settings.Builder<D, S> settingsBuilder
    ) {
        extensions.getProcessors().forEach(it -> it.process(element, target, meta, settingsBuilder));
    }

    default @NotNull List<dev.triumphteam.cmd.core.annotations.@NotNull Requirement> getRequirementsFromAnnotations() {
        final AnnotatedElement element = getAnnotatedElement();

        final Requirements requirements = element.getAnnotation(Requirements.class);

        if (requirements != null) return Arrays.asList(requirements.value());

        final dev.triumphteam.cmd.core.annotations.Requirement requirement = element.getAnnotation(dev.triumphteam.cmd.core.annotations.Requirement.class);

        if (requirement == null) return Collections.emptyList();

        return Collections.singletonList(requirement);
    }
}