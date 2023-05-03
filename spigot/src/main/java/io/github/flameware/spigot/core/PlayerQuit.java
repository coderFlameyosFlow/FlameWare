package io.github.flameware.spigot.core;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@RequiredArgsConstructor
class PlayerQuit implements Listener {
    private final SpigotCommandManager manager;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        for (CommandWrapper wrapper : manager.getWrappers().values()) {
            // check if the cooldown exists
            if (manager.getCooldownMap().containsKey(uuid)) {
                manager.removeCooldown(uuid, wrapper.getName());
            }
        }
    }
}
