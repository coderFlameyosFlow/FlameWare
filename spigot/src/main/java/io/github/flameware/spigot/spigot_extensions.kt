package io.github.flameware.spigot

import org.bukkit.ChatColor

inline fun String.colorize() = ChatColor.translateAlternateColorCodes('&', this)