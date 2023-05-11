package io.github.flameware.spigot.sender;

import io.github.flameware.spigot.core.SpigotCommandManager;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record SpigotActor(CommandSender sender, SpigotCommandManager manager) implements SpigotCommandActor {
    @Override
    public void reply(String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public Class<?> getSenderClass() {
        return CommandSender.class;
    }

    @Override
    public Class<?> getWrapperClass() {
        return SpigotActor.class;
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public CommandSender getSender() {
        return sender;
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public Audience audience() {
        if (Audience.class.isAssignableFrom(getSenderClass())) {
            // Paper
            return (Audience) sender;
        }

        try (BukkitAudiences audiences = BukkitAudiences.create(manager.getPlugin())) {
            return audiences.sender(sender);
        }
    }

    @Override
    public void reply(Object component) {
        this.audience().sendMessage((ComponentLike) component);
    }

    @Override
    public @Nullable Player getPlayer() {
        return (sender instanceof Player ? (Player) sender : null);
    }

    @Override
    public @Nullable ConsoleCommandSender getConsole() {
        return (sender instanceof ConsoleCommandSender ? (ConsoleCommandSender) sender : null);
    }

    @Override
    public Player requirePlayer() {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        throw new IllegalStateException("&cYou must be a Player to execute this command.");
    }

    @Override
    public ConsoleCommandSender requireConsole() {
        if (sender instanceof ConsoleCommandSender) {
            return (ConsoleCommandSender) sender;
        }
        throw new IllegalStateException("&cYou must be a Console to execute this command.");
    }

    @Override
    public Player requirePlayer(String message) {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        throw new IllegalStateException(message);
    }

    @Override
    public ConsoleCommandSender requireConsole(String message) {
        if (sender instanceof ConsoleCommandSender) {
            return (ConsoleCommandSender) sender;
        }
        throw new IllegalStateException(message);
    }

    @Override
    public @Nullable UUID getUniqueId() {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        } else if (sender instanceof ConsoleCommandSender) {
            return null;
        }
        throw new IllegalStateException("&cYou must be a Player or Console to execute this command.");
    }
}
