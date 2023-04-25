package io.github.flameware.common.sender;

import org.jetbrains.annotations.NotNull;

public interface CommandSender {
    Class<?> getSenderClass();

    Class<?> getWrapperClass();

    String getName();

    @NotNull
    Object getSender();

    void reply(String message);
}
