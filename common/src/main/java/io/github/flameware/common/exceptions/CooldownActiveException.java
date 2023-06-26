package io.github.flameware.common.exceptions;

public final class CooldownActiveException extends Exception {
    public CooldownActiveException(String message) {
        super(message);
    }
}
