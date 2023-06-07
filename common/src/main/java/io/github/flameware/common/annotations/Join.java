package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that annotates a parameter of a method to indicate it is a greedy string which
 * <p>
 * is the same as <code>String.join(delimiter, args)</code>, it joins an array of strings with the given delimiter.
 * <p>
 * It is hidden as a normal "String" for the sake of developer-friendliness.
 * @author FlameyosFlow
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Join {
    String delimiter() default " ";
}
