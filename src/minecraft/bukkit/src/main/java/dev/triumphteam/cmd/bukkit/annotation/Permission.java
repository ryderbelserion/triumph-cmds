package dev.triumphteam.cmd.bukkit.annotation;

import dev.triumphteam.cmd.bukkit.CommandPermission;
import dev.triumphteam.cmd.core.extension.meta.MetaKey;
import org.bukkit.permissions.PermissionDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a method using this Annotation to add a required permission.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface Permission {

    /**
     * The permission.
     *
     * @return The permission's main node.
     */
    String[] value();

    /**
     * Represents the possible default values for permissions.
     *
     * @return The permission's {@link PermissionDefault}.
     * @see PermissionDefault
     */
    PermissionDefault def() default PermissionDefault.OP;

    /**
     * A brief description of the permission.
     *
     * @return The permission's description.
     * @see org.bukkit.permissions.Permission#getDescription()
     */
    String description() default "";

    MetaKey<CommandPermission> META_KEY = MetaKey.of("command.permission", CommandPermission.class);
}