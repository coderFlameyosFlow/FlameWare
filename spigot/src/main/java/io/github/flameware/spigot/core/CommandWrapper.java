package io.github.flameware.spigot.core;

import io.github.flameware.common.base.command.Command;
import io.github.flameware.common.base.arguments.ArgumentSuggestionRegistry;
import io.github.flameware.common.base.command.CommandImpl;
import io.github.flameware.common.base.command.CommandInfo;
import io.github.flameware.common.exceptions.ArgumentParseException;
import io.github.flameware.spigot.sender.SpigotCommandActor;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

public final class CommandWrapper extends org.bukkit.command.Command implements PluginIdentifiableCommand {
    private final SpigotCommandManager manager;
    private final Object object;
    private final Command command;

    CommandWrapper(@NotNull CommandInfo info, @NotNull SpigotCommandManager manager, @NotNull Object object) {
        super(info.name(), info.description(), info.usage(), List.of(info.aliases()));
        this.manager = manager;
        this.object = object;
        this.command = Command.wrap(info, manager, object);
        if (info.permission() != null && !info.permission().isEmpty()) {
            setPermission(info.permission());
        }
    }



    @Override
    @ApiStatus.NonExtendable
    public boolean execute(CommandSender sender, String commandLabel, String @NotNull [] args) {
        SpigotCommandActor defaultSender = SpigotCommandActor.wrap(sender, manager);
        for (Method m : object.getClass().getDeclaredMethods()) {
            try {
                this.executeCommand(m, defaultSender, sender, args);
            } catch (ArgumentParseException e) {
                manager.getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return true;
    }

    @Override
    @Contract(pure = true)
    @ApiStatus.NonExtendable
    public @NotNull @Unmodifiable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        ArgumentSuggestionRegistry completer = manager.getSuggestionRegistry();
        return completer.autoComplete(command.getClass(), (clazz, list) -> {
            if (clazz.isAssignableFrom(Player.class)) {
                list.addAll(List.of(SpigotCommandManager.ONLINE_PLAYERS));
                return 0;
            } else if (clazz.isAssignableFrom(OfflinePlayer.class)) {
                list.addAll(List.of(SpigotCommandManager.OFFLINE_PLAYERS));
                return 0;
            } else if (clazz.isAssignableFrom(World.class)) {
                list.addAll(List.of(SpigotCommandManager.WORLDS));
                return 0;
            }
            return 1;
        });
    }

    @Override
    public Plugin getPlugin() {
        return manager.getPlugin();
    }

    private void executeCommand(Method m, SpigotCommandActor defaultSender, CommandSender sender, String @NotNull [] args) throws ArgumentParseException {
        this.command.execute(m, defaultSender, args, (type) -> {
            if (type.isAssignableFrom(CommandSender.class)) {
                manager.invoke(m, object, sender, args);
                return true;
            }
            return false;
        }, (type, moreArgs, actor) -> {
            if (type == CommandSender.class) {
                return true;
            } else if (type == Player.class && (actor.getPlayer() == null)) {
                return false;
            } else return (type != ConsoleCommandSender.class && actor.getConsole() != null);
        });
    }
}
