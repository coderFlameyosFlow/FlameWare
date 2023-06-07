package io.github.flameware.common.annotations;

import java.lang.annotation.*;

/**
 * Annotation to mark a parameter to be suggested when the user is typing.
 * <p>
 * Players, OfflinePlayers and Worlds are suggested by default.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Suggest {
    String[] value();
}
