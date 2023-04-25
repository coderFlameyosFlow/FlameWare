package io.github.flameware.common.exceptions;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class OutOfRangeException extends Exception {
    public OutOfRangeException(@Nullable String message) {
        super(message == null ? "Sender expected to put a number that is in range but did otherwise." : message);
    }

    public OutOfRangeException(Parameter parameter, @Nullable String message) {
        super(message == null ? "Sender expected to put a number that is in range but did otherwise in parameter " + parameter.getName() : message);
    }

    public OutOfRangeException(Method method, Parameter parameter, @Nullable String message) {
        super(message == null ? "Sender expected to put a number that is in range but did otherwise in parameter: " + parameter.getName() + "\n at method: " + method.getName() : message);
    }
}
