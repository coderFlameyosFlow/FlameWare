package io.github.flameware.spigot.core;

import io.github.flameware.common.base.command.ICommand;
import lombok.Data;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@Data
class PlayerQuit implements Listener {
    private final SpigotCommandManager manager;

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        String uuid = event.getPlayer().getName();
        for (ICommand wrapper : manager.getCommands().values()) {
            String name = wrapper.getName();
            if (!manager.isCooldownActive(uuid, name)) continue;
            manager.removeCooldown(uuid, name);
        }
    }
}
