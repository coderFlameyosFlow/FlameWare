package io.github.flameware.common.exceptions;

import org.jetbrains.annotations.Nullable;

public class SenderNotConsoleException  extends Exception {
    public SenderNotConsoleException(@Nullable String message) {
        super(message == null ? "Sender expected to be player but found console" : message);
    }
}

