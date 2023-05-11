package io.github.flameware.common.sender;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface CommandActor {
    void reply(String message);

    Class<?> getSenderClass();

    Class<?> getWrapperClass();

    String getName();

    Object getSender();

    @Nullable
    Object getPlayer();

    @Nullable
    Object getConsole();

    Object requirePlayer();

    Object requireConsole();

    UUID getUniqueId();

    boolean hasPermission(String permission);
}
