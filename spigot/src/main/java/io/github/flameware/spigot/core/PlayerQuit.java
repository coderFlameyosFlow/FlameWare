package io.github.flameware.spigot.core;

import io.github.flameware.common.base.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

record PlayerQuit(SpigotCommandManager manager) implements Listener {
    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        for (Command wrapper : manager.getCommands().values()) {
            String name = wrapper.getName();
            if (manager.isCooldownActive(uuid, name)) {
                manager.removeCooldown(uuid, name);
            }
        }
    }
}
