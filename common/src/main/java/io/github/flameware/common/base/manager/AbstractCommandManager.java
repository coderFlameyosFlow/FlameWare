package io.github.flameware.common.base.manager;

import io.github.flameware.common.CommandExecutionType;
import io.github.flameware.common.annotations.Async;
import io.github.flameware.common.annotations.Cooldown;
import io.github.flameware.common.sender.SenderFactory;

import lombok.Data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
public abstract class AbstractCommandManager<P> implements CommandManager {
    protected final P plugin;
    protected final CommandExecutionType commandExecutionType;
    private final UUID consoleUUID = UUID.randomUUID();
    private final Set<SenderFactory<?>> senderFactorySet = new HashSet<>();
    private final ConcurrentMap<UUID, ConcurrentMap<String, Long>> cooldownMap = new ConcurrentHashMap<>();

    public <T> void addSenderFactory(SenderFactory<T> factory) {
        senderFactorySet.add(factory);
    }

    private static void invokeAsync(@NotNull Method method, @NotNull Object object, Object... args) {
        new Thread(() -> invokeSync(method, object, args)).start();
    }

    private static void invokeSync(@NotNull Method method, @NotNull Object object, Object... args) {
        try {
            method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalCallerException(e.getMessage());
        }
    }

    @Override
    public void invoke(@NotNull Method method, @NotNull Object object, Object... args) {
        if (commandExecutionType == CommandExecutionType.ASYNC || method.isAnnotationPresent(Async.class)) {
            invokeAsync(method, object, args);
        } else {
            invokeSync(method, object, args);
        }
    }

    @Override
    public boolean isCooldownActive(UUID uuid, String commandName) {
        return Optional.ofNullable(this.getCooldownEntry(uuid, commandName)).isPresent();
    }

    @Override
    public @Nullable Long getCooldownEntry(UUID uuid, String commandName) {
        return Optional.ofNullable(cooldownMap.get(Objects.requireNonNull(uuid)))
                    .orElseThrow(() -> new IllegalCallerException("UUID " + uuid + " is not in the ConcurrentMap."))
                    .get(commandName);
    }

    @Override
    public CommandManager addCooldown(UUID uuid, String commandName, @NotNull Cooldown cooldown) {
        Optional.ofNullable(cooldownMap.get(Objects.requireNonNull(uuid)))
                .orElse(cooldownMap.put(uuid, new ConcurrentHashMap<>()))
                .put(commandName, System.currentTimeMillis());
        return this;
    }

    @Override
    public CommandManager removeCooldown(UUID uuid, String commandName) {
        Optional.ofNullable(cooldownMap.get(Objects.requireNonNull(uuid)))
                .orElseThrow(() -> new IllegalCallerException("UUID " + uuid + " is not in the ConcurrentMap."))
                .remove(commandName);
        return this;
    }
}
