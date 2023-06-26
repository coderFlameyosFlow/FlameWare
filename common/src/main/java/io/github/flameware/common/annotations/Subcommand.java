package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Subcommand of the main {@link Command}.
 * <p>
 * There can be multiple subcommands, of course, each with its own name, optional usage, description, aliases and permission.
 * <p>
 * Note that the name of the subcommand must be unique and not empty or have the same name as the parent command or another subcommand.
 * @author FlameyosFlow
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subcommand {
    String name();
    String desc() default "";
    String usage() default "/";
    String perm() default "";
    String[] aliases() default {};
}
