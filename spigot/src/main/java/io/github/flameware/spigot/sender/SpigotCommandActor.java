package io.github.flameware.spigot.sender;

import io.github.flameware.common.sender.CommandActor;
import io.github.flameware.spigot.core.SpigotCommandManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public sealed interface SpigotCommandActor extends CommandActor permits SpigotActor {
    Audience audience();

    void reply(Object component);

    @Nullable
    Player getPlayer();

    @Nullable
    ConsoleCommandSender getConsole();

    Player requirePlayer();

    ConsoleCommandSender requireConsole();

    static SpigotActor wrap(CommandSender sender, SpigotCommandManager manager) {
        return new SpigotActor(sender, manager);
    }

    Player requirePlayer(String message);

    ConsoleCommandSender requireConsole(String message);
}
