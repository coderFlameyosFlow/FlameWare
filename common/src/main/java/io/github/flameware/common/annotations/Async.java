package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Asynchronously run a single command every time it is executed.
 * <p>
 * If you are looking to run every command asynchronously, look into CommandExecutionType for more readability.
 * @since 1.0.0
 * @author FlameyosFlow
 * @see io.github.flameware.common.CommandExecutionType CommandExecutionType
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Async {
}