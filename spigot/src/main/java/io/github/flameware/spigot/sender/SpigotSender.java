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

public final class SpigotSender implements SpigotCommandSender {
    private final CommandSender sender;
    private final SpigotCommandManager manager;
    private final UUID consoleUUID = UUID.randomUUID();

    public SpigotSender(CommandSender sender, SpigotCommandManager manager) {
        this.sender = sender;
        this.manager = manager;
    }

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
        return SpigotSender.class;
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
    public void reply(ComponentLike component) {
        this.audience().sendMessage(component);
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
    public UUID getUniqueId() {
        if (sender instanceof Player) {
            return ((Player) sender).getUniqueId();
        } else if (sender instanceof ConsoleCommandSender) {
            return consoleUUID;
        }
        return null;
    }
}
