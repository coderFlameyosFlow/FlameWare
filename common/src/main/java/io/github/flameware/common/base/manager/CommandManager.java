package io.github.flameware.common.base.manager;

import io.github.flameware.common.annotations.Cooldown;

import io.github.flameware.common.base.arguments.ArgumentHandler;
import io.github.flameware.common.base.arguments.ArgumentSuggestionRegistry;
import io.github.flameware.common.base.command.Command;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public sealed interface CommandManager permits AbstractCommandManager {
    Map<String, Command> getCommands();

    Logger getLogger();

    boolean isCooldownActive(UUID uuid, String commandName);

    CommandManager removeCooldown(UUID uuid, String commandName);

    Long getCooldownEntry(UUID uuid, String commandName);

    CommandManager addCooldown(UUID uuid, String commandName, @NotNull Cooldown cooldown);

    void invoke(@NotNull Method method, @NotNull Object object, Object... args);

    CommandManager register(Object... commands);

    CommandManager register(Object command);

    ArgumentHandler getArgumentHandler();

    ArgumentSuggestionRegistry getSuggestionRegistry();
}
