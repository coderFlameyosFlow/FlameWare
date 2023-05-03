package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Command {
    String name() default "<CLASS_COMMAND_DEFAULT>";
    String desc() default "";
    String usage() default "/";
    String perm() default "";
    String[] aliases() default {};
}
