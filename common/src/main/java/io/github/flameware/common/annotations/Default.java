package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Make the command argument an Optional argument.
 * <p>
 * This usually returns null if it is absent <strong>UNLESS</strong> you give it a default value using {@link Default#def() Default#def()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Default {
    /**
     * The default value to use, or null.
     * <p>
     * if absent, this is parsed into 1, true and vice versa, depending on the type of the parameter.
     * @return default value or empty (if absent)
     */
    String def() default "";
}
