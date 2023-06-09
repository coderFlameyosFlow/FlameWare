package io.github.flameware.spigot.core;

import io.github.flameware.common.annotations.Arg;
import io.github.flameware.common.annotations.Default;
import io.github.flameware.common.base.command.ICommand;
import io.github.flameware.common.base.command.CommandInfo;
import io.github.flameware.common.exceptions.ArgumentParseException;
import io.github.flameware.common.exceptions.CooldownActiveException;
import io.github.flameware.common.exceptions.NotInRangeException;
import io.github.flameware.spigot.sender.SpigotCommandActor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The main CommandWrapper for spigot.
 * @author FlameyosFlow
 */
@ApiStatus.Internal
public final class CommandWrapper extends Command implements PluginIdentifiableCommand {
    private final SpigotCommandManager manager;
    private final Object object;
    private final ICommand command;
    private final Method method;

    CommandWrapper(@NotNull CommandInfo info, @NotNull SpigotCommandManager manager, ICommand command, @NotNull Object object, @NotNull Method method) {
        super(info.getName(), info.getDescription(), info.getUsage(), List.of(info.getAliases()));
        this.manager = manager;
        this.object = object;
        this.method = method;
        this.command = command;
        if (info.getPermission() != null && !info.getPermission().isEmpty()) {
            setPermission(info.getPermission());
        }

        if (info.getUsage().isEmpty() || "/".equals(info.getUsage())) {
            StringBuilder sb = new StringBuilder(100);
            sb.append('/').append(info.getName());
            for (String alias : info.getAliases()) {
                sb.append('|').append(alias);
            }
            for (Parameter parameter : method.getParameters()) {
                var arg = parameter.getAnnotation(Arg.class);
                var defaultAnnotation = parameter.getAnnotation(Default.class);
                sb.append(' ').append(defaultAnnotation != null ? '[' : '<');
                sb.append(arg != null ? arg.id() : parameter.getName());
                sb.append(defaultAnnotation != null ? ']' : '>');
            }
            setUsage(sb.toString());
        }
    }



    @Override
    public boolean execute(CommandSender sender, String commandLabel, String @NotNull [] args) {
        SpigotCommandActor defaultSender = SpigotCommandActor.wrap(sender, manager);

        try {
            this.executeCommand(method, defaultSender, sender, args);
        } catch (final ArgumentParseException | IllegalArgumentException |
                       CooldownActiveException | NotInRangeException e) {
            defaultSender.reply(ChatColor.RED + e.getMessage());
            return true;
        } catch (final NegativeArraySizeException | ArrayIndexOutOfBoundsException e) {
            defaultSender.reply(ChatColor.RED + "Usage: " + getUsage());
            return false;
        }


        return true;
    }

    @Override
    @Contract(pure = true)
    public @NotNull @Unmodifiable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return manager.getSuggestionRegistry().complete(method, (clazz, list) -> {
            if (clazz.isAssignableFrom(Player.class)) {
                List<String> offlinePlayers = new ArrayList<>(500);
                for (var player : Bukkit.getOnlinePlayers())
                    offlinePlayers.add(player.getName());
                list.addAll(offlinePlayers);
                return true;
            } else if (clazz.isAssignableFrom(OfflinePlayer.class)) {
                List<String> offlinePlayers = new ArrayList<>(5000);
                for (var player : Bukkit.getOfflinePlayers())
                    offlinePlayers.add(player.getName());
                list.addAll(offlinePlayers);
                return true;
            } else if (clazz.isAssignableFrom(World.class)) {
                list.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                return true;
            }
            return false;
        });
    }

    @Override
    public Plugin getPlugin() {
        return manager.getPlugin();
    }

    private void executeCommand(Method m, SpigotCommandActor defaultSender, CommandSender sender, String @NotNull [] args)
            throws ArgumentParseException, CooldownActiveException, NotInRangeException {
        this.command.execute(m, defaultSender, args, (type) -> {
            if (type.isAssignableFrom(CommandSender.class)) {
                manager.invoke(m, object, sender, args);
                return true;
            }
            return false;
        }, (type, moreArgs, actor) -> {
            // todo: switch to hashmap for better performance and extensibility

            if (type == CommandSender.class) {
                return false;
            } else if (type == Player.class && defaultSender.getPlayer() == null) {
                return true;
            }
            return (type == ConsoleCommandSender.class && defaultSender.getConsole() == null);
        });
    }
}
