package io.github.flameware.spigot.sender;

import io.github.flameware.common.sender.InterfaceCommandSender;
import io.github.flameware.spigot.core.SpigotCommandManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface SpigotCommandSender extends InterfaceCommandSender {
    Audience audience();

    void reply(Object component);

    @Nullable
    Player getPlayer();

    @Nullable
    ConsoleCommandSender getConsole();

    Player requirePlayer();

    ConsoleCommandSender requireConsole();

    static SpigotSender wrap(CommandSender sender, SpigotCommandManager manager) {
        return new SpigotSender(sender, manager);
    }
}
