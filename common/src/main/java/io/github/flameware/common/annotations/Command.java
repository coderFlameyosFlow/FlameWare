package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The main command annotation which annotates the class OR annotates the method indicating it is the default command.
 * <p>
 * The following note is very important: <strong>DO NOT ANNOTATE THE CLASS USING {@link Command @Command} ON THE CLASS, SPECIFY THE {@link Command#name()}, THAT IS ONLY MEANT FOR THE METHOD</strong>
 * @author FlameyosFlow
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Command {
    String name() default "<CLASS_COMMAND_DEFAULT>";
    String desc() default "";
    String usage() default "";
    String perm() default "";
    String[] aliases() default {};
}
