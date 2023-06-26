package io.github.flameware.spigot.sender;

import io.github.flameware.common.sender.CommandActor;
import io.github.flameware.spigot.core.SpigotCommandManager;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The official FlameWare's spigot command actor.
 * @author FlameyosFlow
 */
@SuppressWarnings("unused")
public interface SpigotCommandActor extends CommandActor<CommandSender> {
    /**
     * Gets the Audience of the sender provided by KyoriPowered Adventure API
     * <p>
     * This is a wrapper for {@link net.kyori.adventure.platform.bukkit.BukkitAudiences#sender(CommandSender) BukkitAudiences#sender(CommandSender)}
     * and so it can be used as a replacement for {@link net.kyori.adventure.platform.bukkit.BukkitAudiences#sender(CommandSender) BukkitAudiences#sender(CommandSender)}.
     * @return the Audience.
     */
    Audience audience();

    /**
     * Sends a Component to the sender provided by KyoriPowered Adventure API
     * <p>
     * This is equivalent to {@link Audience#sendMessage(ComponentLike)} but "Object" is used to avoid loading the class.
     * @param component the Component to send.
     */
    void reply(Object component);

    /**
     * Casts the sender as a Player, returns null if the sender is not a Player.
     * @return the Player or null.
     * @see #getConsole()
     */
    @Nullable
    Player getPlayer();

    /**
     * Casts the sender as a ConsoleCommandSender, returns null if the sender is not a ConsoleCommandSender.
     * @return the ConsoleCommandSender or null.
     * @see #getPlayer()
     */
    @Nullable
    ConsoleCommandSender getConsole();

    /**
     * Casts the sender as a Player, sends a message to the sender and then fails the command if the sender is not a Player.
     * @return the Player or throws the error which is converted to a message then sent.
     * @see #requireConsole()
     */
    @NotNull
    Player requirePlayer();

    /**
     * Casts the sender as a ConsoleCommandSender, sends a message to the sender and then fails the command if the sender is not a ConsoleCommandSender.
     * @return the ConsoleCommandSender or throws the error which is converted to a message then sent.
     * @see #requirePlayer()
     */
    @NotNull
    ConsoleCommandSender requireConsole();

    /**
     * Casts the sender as a Player, sends a message to the sender and then fails the command if the sender is not a Player.
     * @param message the custom message to send if not a Player
     * @return the Player or throws the error which is converted to a message then sent.
     * @see #requireConsole(String)
     */
    @NotNull
    Player requirePlayer(String message);

    /**
     * Casts the sender as a ConsoleCommandSender, sends a message to the sender and then fails the command if the sender is not a ConsoleCommandSender.
     * @param message the custom message to send if not a ConsoleCommandSender
     * @return the ConsoleCommandSender or throws the error which is converted to a message then sent.
     * @see #requirePlayer(String)
     */
    @NotNull
    ConsoleCommandSender requireConsole(String message);

    /**
     * Whether the sender is a Console or not.
     * @return true if the sender is a Console
     * @see #isPlayer()
     */
    boolean isConsole();

    /**
     * Whether the sender is a Player or not.
     * @return true if the player is a player
     * @see #isConsole()
     */
    boolean isPlayer();

    /**
     * Wraps the SpigotCommandActor into a SpigotActor.
     * @param sender the CommandSender
     * @param manager the spigot command manager.
     * @return the SpigotActor.
     */
    @Contract("_, _ -> new")
    static @NotNull SpigotActor wrap(CommandSender sender, SpigotCommandManager manager) {
        return new SpigotActor(sender, manager);
    }
}
