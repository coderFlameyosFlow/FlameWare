package io.github.flameware.common.base.manager;

import io.github.flameware.common.annotations.Cooldown;

import io.github.flameware.common.base.arguments.BaseArgumentHandler;
import io.github.flameware.common.base.arguments.ArgumentSuggestionRegistry;
import io.github.flameware.common.base.command.ICommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

public interface CommandManager {
    /**
     * Get the commands map which contains all the commands AND the subcommands.
     * @return the commands map which is <String, ICommand>.</String,>
     */
    Map<String, ICommand> getCommands();

    /**
     * Get the (logger) of the command manager.
     * @return the logger
     */
    Logger getLogger();

    /**
     * Checks if the cooldown is active.
     * @param name the name of the command actor
     * @param commandName the name of the command/subcommand
     * @return true if the cooldown is in the map and active.
     */
    boolean isCooldownActive(String name, String commandName);

    /**
     * Remove the cooldown from the map.
     * @param name the name of the command actor
     * @param commandName the name of the command/subcommand
     */
    void removeCooldown(String name, String commandName);

    /**
     * Get the cooldown entry from the map, may be null if un-found.
     * @param name the name of the command actor
     * @param commandName the name of the command/subcommand
     * @return the cooldown entry.
     */
    @Nullable Long getCooldownEntry(String name, String commandName);

    /**
     * Adds a cooldown to the map which overrides the existing one.
     * @param name the name of the command actor
     * @param commandName the name of the command/subcommand
     * @param cooldown the cooldown annotation for the command.
     */
    void addCooldown(String name, String commandName, @NotNull Cooldown cooldown);

    /**
     * Invoke the method with the given arguments on the given object.
     * @param method the method
     * @param object the object
     * @param args the arguments to invoke the method with.
     */
    void invoke(@NotNull Method method, @NotNull Object object, Object... args);

    /**
     * Registers the given commands to the command manager.
     * <p>
     * The way of registration based on the platform:
     * <ul>
     *     <li>Spigot: CommandMap</li>
     * </ul>
     * @param commands the commands to register.
     */
    <C> void register(C... commands);

    /**
     * Registers the given command to the command manager.
     *
     * The way of registration based on the platform:
     * <ul>
     *     <li>Spigot: CommandMap</li>
     * </ul>
     * @param command the command to register.
     */
    <C> void register(C command);

    /**
     * Enable whether the cooldowns are enabled or not for the console.
     * <p>
     * According to experts, a good idea is to have this at true if you have a public server, and you don't have a good firewall.
     * @param enable whether to enable them or not.
     */
    void setCooldownsForConsole(boolean enable);

    /**
     * Get the argument handler which is in charge of handling and parsing the arguments.
     * @return the argument handler.
     */
    BaseArgumentHandler getArgumentHandler();

    /**
     * Get the argument suggestion registry which is in charge of providing suggestions.
     * @return the argument suggestion registry
     */
    ArgumentSuggestionRegistry getSuggestionRegistry();

    /**
     * Whether to enable logging or not.
     * <p>
     * I recommend setting this to true because it is very good for debugging.
     * @param log whether to enable logging or not.
     */
    void setLogging(boolean log);

    /**
     * Change the messages that is used across the command framework.
     * @param message the message to set.
     * @param messageString the message string.
     */
    void setMessage(@NotNull Message message, String messageString);
}
