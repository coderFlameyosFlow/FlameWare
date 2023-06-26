package io.github.flameware.common.sender;

public interface SenderFactory<T> {
    T createSenderFactory(String input);
}
