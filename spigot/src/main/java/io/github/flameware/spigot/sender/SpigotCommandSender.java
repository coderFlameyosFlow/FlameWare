package io.github.flameware.spigot.sender;

import io.github.flameware.common.exceptions.SenderNotConsoleException;

import io.github.flameware.common.exceptions.SenderNotPlayerException;
import io.github.flameware.common.sender.CommandSender;
import io.github.flameware.spigot.core.SpigotCommandManager;
import io.github.flameware.spigot.core.sender.SpigotSender;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface SpigotCommandSender extends CommandSender {
    @Nullable
    Player getPlayer();

    @Nullable
    ConsoleCommandSender getConsole();

    ConsoleCommandSender requireConsole() throws SenderNotConsoleException;

    Player requirePlayer() throws SenderNotPlayerException;

    void reply(ComponentLike component);

    Audience audience();

    static SpigotCommandSender wrap(org.bukkit.command.CommandSender sender, SpigotCommandManager manager) {
        return new SpigotSender(sender, manager);
    }
}
