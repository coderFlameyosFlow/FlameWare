package io.github.flameware.common.base.manager;

import io.github.flameware.common.CommandExecutionType;
import io.github.flameware.common.annotations.Async;
import io.github.flameware.common.annotations.Cooldown;
import io.github.flameware.common.base.arguments.ArgumentHandler;
import io.github.flameware.common.base.arguments.ArgumentSuggestionRegistry;
import io.github.flameware.common.base.command.Command;

import lombok.Getter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * The abstract command manager with execution for async and sync for methods and cooldowns.
 * <p>
 * Only extend this if you are looking to make your own custom manager.
 * @param <P> The plugin/platform.
 */
@SuppressWarnings("unused")
public abstract non-sealed class AbstractCommandManager<P> implements CommandManager {
    @Getter
    protected final P plugin;

    @Getter
    protected final Logger logger;

    @Getter
    protected final Map<String, Command> commands;

    private final CommandExecutionType commandExecutionType;
    //private final Set<SenderFactory<?>> senderFactorySet;
    private final ConcurrentMap<UUID, ConcurrentMap<String, Long>> cooldownMap;
    protected final ArgumentHandler argumentHandler;

    protected final ArgumentSuggestionRegistry suggestionRegistry;

    protected AbstractCommandManager(P plugin, CommandExecutionType commandExecutionType) {
        this.plugin = plugin;
        this.commandExecutionType = commandExecutionType;
        //this.senderFactorySet = new HashSet<>(10);
        this.cooldownMap = new ConcurrentHashMap<>(1024);
        this.argumentHandler = new ArgumentHandler();
        this.suggestionRegistry = new ArgumentSuggestionRegistry(this);
        this.logger = Logger.getLogger("Minecraft");
        this.commands = new HashMap<>(754);
    }

    /*@CanIgnoreReturnValue
    public <T> CommandManager addSenderFactory(SenderFactory<T> factory) {
        senderFactorySet.add(factory);
        return this;
    }*/

    private static void invokeAsync(@NotNull Method method, @NotNull Object object, Object... args) {
        CompletableFuture.runAsync(() -> invokeSync(method, object, args));
    }

    private static void invokeSync(@NotNull Method m, @NotNull Object object, Object... args) {
        try {
            MethodHandle handle = MethodHandles.publicLookup().unreflect(m).bindTo(object);
            handle.invokeExact(args);
        } catch (Throwable exception) {
            throw new IllegalCallerException(exception);
        }
    }

    /**
     * invoke the Method object using MethodHandle.
     * <p>
     * The method runs asynchronously if the method is annotated with {@link Async} or the command execution is {@link CommandExecutionType#ASYNC}
     * @param method the method to invoke
     * @param object the object to invoke the method on
     * @param args the arguments to invoke the method with
     */
    @Override
    public void invoke(@NotNull Method method, @NotNull Object object, Object... args) {
        if (commandExecutionType == CommandExecutionType.ASYNC || method.isAnnotationPresent(Async.class)) {
            invokeAsync(method, object, args);
        } else {
            invokeSync(method, object, args);
        }
    }

    /**
     * checks if the cooldown is active.
     * @param uuid the uuid to check for
     * @param commandName the command name
     * @return true if the cooldown is active, else false.
     */
    @Override
    public boolean isCooldownActive(UUID uuid, String commandName) {
        long timeNow = System.currentTimeMillis();
        Optional<Long> cooldownEntry = Optional.ofNullable(this.getCooldownEntry(uuid, commandName));
        return cooldownEntry.isPresent() && cooldownEntry.get() < timeNow;
    }

    /**
     * Get the cooldown entry if present
     * @param uuid the uuid to check for
     * @param commandName the command name
     * @return the cooldown entry or null
     */
    @Override
    public @Nullable Long getCooldownEntry(UUID uuid, String commandName) {
        return Optional.ofNullable(cooldownMap.get(Objects.requireNonNull(uuid)))
                    .orElseThrow(() -> new IllegalCallerException("UUID " + uuid + " is not in the ConcurrentMap."))
                    .get(commandName);
    }

    /**
     * Add the cooldown to the concurrent map.
     * @param uuid the uuid to check for
     * @param commandName the command name
     * @param cooldown the cooldown annotation
     * @return the command manager for chaining
     */
    @Override
    @SuppressWarnings("DataFlowIssue") // There is literally an Optional.ofNullable(...), good job Intellij IDEA
    public CommandManager addCooldown(UUID uuid, String commandName, @NotNull Cooldown cooldown) {
        Optional.ofNullable(cooldownMap.get(Objects.requireNonNull(uuid)))
                .orElse(cooldownMap.put(uuid, new ConcurrentHashMap<>(754)))
                .put(commandName, System.currentTimeMillis());
        return this;
    }

    /**
     * Remove the cooldown from the concurrent map.
     * @param uuid the uuid to check for
     * @param commandName the command name
     * @return the command manager for chaining
     */
    @Override
    public CommandManager removeCooldown(UUID uuid, String commandName) {
        Optional.ofNullable(cooldownMap.get(Objects.requireNonNull(uuid)))
                .orElseThrow(() -> new IllegalCallerException("UUID " + uuid + " is not in the ConcurrentMap."))
                .remove(commandName);
        return this;
    }

    /**
     * Get the argument handler that parses the arguments.
     * @return the argument handler
     */
    @Override
    public ArgumentHandler getArgumentHandler() {
        return argumentHandler;
    }

    /**
     * Get the suggestion registry that suggests such arguments.
     *
     * @return the suggestion registry
     */
    @Override
    public ArgumentSuggestionRegistry getSuggestionRegistry() {
        return suggestionRegistry;
    }
}
