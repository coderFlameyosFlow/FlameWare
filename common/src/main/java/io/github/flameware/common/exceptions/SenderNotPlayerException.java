package io.github.flameware.common.exceptions;

import org.jetbrains.annotations.Nullable;

public class SenderNotPlayerException extends Exception {
    public SenderNotPlayerException(@Nullable String message) {
        super(message == null ? "Sender expected to be player but found console" : message);
    }
}
