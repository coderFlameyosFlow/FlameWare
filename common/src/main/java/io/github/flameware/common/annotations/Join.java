package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Join {
    String delimiter() default " ";

    /**
     * The maximum characters the message can have, defaults to empty string (infinite)
     * <p>
     * This is parsed into an integer.
     * @return the max characters a message can have, or infinite aka empty string
     */
    String maxChars() default "";
}
