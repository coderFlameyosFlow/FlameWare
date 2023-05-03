package io.github.flameware.common.base.manager;

import io.github.flameware.common.annotations.Cooldown;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.UUID;

public interface CommandManager {
    boolean isCooldownActive(UUID uuid, String commandName);

    CommandManager removeCooldown(UUID uuid, String commandName);

    Long getCooldownEntry(UUID uuid, String commandName);

    CommandManager addCooldown(UUID uuid, String commandName, @NotNull Cooldown cooldown);

    void invoke(@NotNull Method method, @NotNull Object object, Object... args);

    CommandManager register(Object... commands);

    CommandManager register(Object command);
}
