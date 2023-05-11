package io.github.flameware.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a parameter to be auto-completed when the user is typing.
 * <p>
 * Players and OfflinePlayers are auto-completed by default.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AutoComplete {
    String[] value();
}
