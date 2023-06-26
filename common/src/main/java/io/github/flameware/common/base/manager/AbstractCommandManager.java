package io.github.flameware.common.base.manager;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.flameware.common.CommandExecutionType;
import io.github.flameware.common.annotations.Async;
import io.github.flameware.common.annotations.Cooldown;
import io.github.flameware.common.base.arguments.BaseArgumentHandler;
import io.github.flameware.common.base.arguments.ArgumentSuggestionRegistry;
import io.github.flameware.common.base.command.ICommand;

import lombok.Getter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * @author FlameyosFlow
 * @param <P> The plugin/platform.
 * @since 1.0.0 (alpha release)
 */
@Getter
@SuppressWarnings("unused")
public abstract class AbstractCommandManager<P> implements CommandManager {
    protected final P plugin;
    protected final Logger logger;
    protected final ConcurrentMap<String, ICommand> commands;
    protected boolean log,allowCooldownsForConsole = false;
    private final CommandExecutionType commandExecutionType;
    //private final Set<SenderFactory<?>> senderFactorySet;
    private final Map<String, Map<String, Long>> cooldownMap;
    protected final BaseArgumentHandler argumentHandler;
    protected final ArgumentSuggestionRegistry suggestionRegistry;

    protected AbstractCommandManager(P plugin, CommandExecutionType commandExecutionType) {
        this.plugin = plugin;
        this.commandExecutionType = commandExecutionType;
        this.commands = new ConcurrentHashMap<>(754);
        this.cooldownMap = new HashMap<>(1024);
        this.argumentHandler = new BaseArgumentHandler();
        this.suggestionRegistry = new ArgumentSuggestionRegistry(this);
        this.logger = Logger.getLogger("FlameWare");
        //this.senderFactorySet = new HashSet<>(10);
    }

    /*@CanIgnoreReturnValue
    public <T> CommandManager addSenderFactory(SenderFactory<T> factory) {
        senderFactorySet.add(factory);
        return this;
    }

    @CanIgnoreReturnValue
    public <T> CommandManager addSenderFactoryIfAbsent(SenderFactory<T> factory) {
        if (!senderFactorySet.contains(factory))
            senderFactorySet.add(factory);
        return this;
    }*/

    private void invokeAsync(@NotNull Method method, @NotNull Object object, Object... args) {
        CompletableFuture.runAsync(() -> invokeSync(method, object, args)).whenComplete((v, throwable) ->
                logger.info("Asynchronously ran method named " + method.getName()));
    }

    private static void invokeSync(@NotNull Method m, @NotNull Object object, Object... args) {
        try {
            MethodHandles.publicLookup().unreflect(m).invokeExact(object, args);
        } catch (Throwable exception) {
            throw new IllegalCallerException(exception);
        }
    }

    @Override
    public void setCooldownsForConsole(boolean enable) {
        this.allowCooldownsForConsole = enable;
    }

    @Override
    public void invoke(@NotNull Method method, @NotNull Object object, Object... args) {
        if (commandExecutionType == CommandExecutionType.ASYNC || method.isAnnotationPresent(Async.class)) {
            invokeAsync(method, object, args);
            return;
        }
        invokeSync(method, object, args);
    }

    @Override
    public boolean isCooldownActive(String name, String commandName) {
        long timeNow = System.currentTimeMillis();
        Long cooldownEntry = this.getCooldownEntry(name, commandName);
        return cooldownEntry != null && cooldownEntry < timeNow;
    }

    @Override
    public @Nullable Long getCooldownEntry(@NotNull String name, String commandName) {
        return Optional.ofNullable(cooldownMap.get(name))
                .orElseThrow(() -> new IllegalStateException("String " + name + " not in Cooldown Map"))
                .get(name);
    }

    @Override
    @SuppressWarnings("DataFlowIssue") // There is literally an Optional.ofNullable(...).orElse(...), good job Intellij IDEA
    public void addCooldown(String name, String commandName, @NotNull Cooldown cooldown) {
        Optional.ofNullable(cooldownMap.get(Objects.requireNonNull(name)))
                .orElse(cooldownMap.put(name, new HashMap<>(754)))
                .put(commandName, System.currentTimeMillis());
    }

    @Override
    public void removeCooldown(String name, String commandName) {
        Optional.ofNullable(cooldownMap.get(Objects.requireNonNull(name)))
                .orElseThrow(() -> new IllegalCallerException("Name " + name + " is not in the ConcurrentMap."))
                .remove(commandName);
    }

    @Override
    public void setLogging(boolean log) {
        this.log = log;
    }

    @Override
    @CanIgnoreReturnValue
    @Contract("_, _ -> _")
    public void setMessage(@NotNull Message message, String messageString) {
        message.setMessage(messageString);
    }
}