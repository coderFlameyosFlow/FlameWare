package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The range of a numeric value.
 * <p>
 * The minimum value is non-negative and the maximum value is the maximum double value.
 * <p>
 * It is likely non-negative is the best choice (for e.g: economy) but of course you can change it to any value.
 * @apiNote Use {@link #min()} and {@link #max()} for usage of "intermediate" ranging
 * @author FlameyosFlow
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Range {
    /**
     * The minimum value of the range.
     * @return the minimum value or 0.0d
     */
    double min() default 0.0d;
    /**
     * The maximum value of the range.
     * @return the maximum value or Double.MAX_VALUE
     */
    double max() default Double.MAX_VALUE;
}
