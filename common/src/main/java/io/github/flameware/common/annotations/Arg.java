package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Arg {
    String value();
    boolean join() default false;
    boolean optional() default false;

    /**
     * This is only used if optional() is true
     * <p>
     * if the argument is optional and the argument is absent then this is returned.
     * @return the default value if the argument is optional
     */
    String defaultValue() default "";
}
