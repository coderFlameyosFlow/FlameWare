package io.github.flameware.spigot.core.sender;

import io.github.flameware.common.exceptions.SenderNotConsoleException;
import io.github.flameware.common.exceptions.SenderNotPlayerException;
import io.github.flameware.spigot.core.SpigotCommandManager;
import io.github.flameware.spigot.sender.SpigotCommandSender;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpigotSender implements SpigotCommandSender {
    private final CommandSender sender;
    private final SpigotCommandManager manager;
    private final BukkitAudiences audiences;

    public SpigotSender(CommandSender sender, SpigotCommandManager manager) {
        this.sender = sender;
        this.manager = manager;
        this.audiences = BukkitAudiences.create(manager.getPlugin());
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
    public @NotNull CommandSender getSender() {
        return sender;
    }

    @Override
    public void reply(String message) {
        sender.sendMessage(message);
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
    public ConsoleCommandSender requireConsole() throws SenderNotConsoleException {
        if (sender instanceof ConsoleCommandSender)
            return (ConsoleCommandSender) sender;
        throw new SenderNotConsoleException(null);
    }

    @Override
    public Player requirePlayer() throws SenderNotPlayerException {
        if (sender instanceof Player)
            return (Player) sender;
        throw new SenderNotPlayerException(null);
    }

    @Override
    public void reply(ComponentLike component) {
        audience().sendMessage(component);
    }

    @Override
    public Audience audience() {
        if (Audience.class.isAssignableFrom(CommandSender.class)) return ((Audience) sender);
        return audiences.sender(sender);
    }
}
