package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subcommand {
    String name();
    String desc() default "";
    String usage() default "/";
    String perm() default "";
    String[] aliases() default {};
}
