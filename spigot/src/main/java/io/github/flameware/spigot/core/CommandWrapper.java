package io.github.flameware.spigot.core;

import io.github.flameware.common.annotations.Cooldown;
import io.github.flameware.common.annotations.Range;
import io.github.flameware.common.annotations.Subcommand;
import io.github.flameware.common.base.command.CommandInfo;
import io.github.flameware.common.sender.InterfaceCommandSender;
import io.github.flameware.spigot.sender.SpigotCommandSender;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

final class CommandWrapper extends Command {
    private final SpigotCommandManager manager;
    private final Object command;

    CommandWrapper(@NotNull CommandInfo info, @NotNull SpigotCommandManager manager, @NotNull Object command) {
        super(info.getName(), info.getDescription(), info.getUsage(), List.of(info.getAliases()));
        this.manager = manager;
        this.command = command;
        if (info.getPermission() != null && !info.getPermission().isEmpty()) {
            setPermission(info.getPermission());
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        SpigotCommandSender defaultSender = SpigotCommandSender.wrap(sender, manager);
        Class<?> clazz = command.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        int methodCount = methods.length;
        Logger logger = manager.getPlugin().getLogger();
        String subcommandLog = "Command " + getName() + " " + args[0] + " was executed by " + sender.getName();
        String commandLog = "Command " + getName() + " was executed by " + sender.getName();
        try {
            for (int i = 0; i != methodCount; i++) {
                Method m = methods[i];
                Object[] parsedArgs = this.setArguments(sender, m, args);
                if (parsedArgs.length == 0) return false;
                if (applyCooldown(m, defaultSender)) return false;

                Class<?> type = m.getParameterTypes()[i];
                if (type.isAssignableFrom(Number.class) && !applyRange(m, defaultSender, (Double) type.cast(parsedArgs[i]))) {
                    return true;
                }

                Subcommand subcommand = m.getAnnotation(Subcommand.class);
                var wrappers = manager.getWrappers();
                var wrapper = wrappers.get(args[0]);
                if (subcommand != null && wrapper != null) {
                    String[] subargs = new String[args.length - 1];
                    System.arraycopy(args, 1, subargs, 0, subargs.length);
                    logger.info(subcommandLog);
                    return wrapper.execute(sender, commandLabel, subargs);
                } else {
                    run(m, sender, defaultSender, this.setArguments(sender, m, args));
                    logger.info(commandLog);
                }
            }
        } catch (IllegalStateException | IllegalArgumentException exception) {
            sender.sendMessage(ChatColor.RED + exception.getMessage());
        }

        return true;
    }

    @Override
    @Contract(pure = true)
    public @NotNull @Unmodifiable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private static boolean checkRequirements(Class<?> type, String[] args, InterfaceCommandSender sender) {
        if (type == CommandSender.class) {
            return true;
        } else if (type == Player.class && (sender.getPlayer() == null)) {
            return false;
        } // return if the console command sender is not equal to the type, if it is ConsoleCommandSender but the console is executing the command then return true, false otherwise
        return (type != ConsoleCommandSender.class || sender.getConsole() != null);
    }

    private Object[] setArguments(CommandSender sender, Method m, String[] args) {
        try {
            return manager.getArgumentHandler().parse(SpigotCommandSender.wrap(sender, manager), m, args, CommandWrapper::checkRequirements);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            sender.sendMessage(ChatColor.RED + exception.getMessage());
            return new Object[0];
        }
    }

    private boolean run(@NotNull Method m, CommandSender sender, SpigotCommandSender defaultSender, Object[] parsedArgs) {
        Class<?> type = m.getParameterTypes()[0];
        try {
            if (type.isAssignableFrom(CommandSender.class)) {
                manager.invoke(m, command, sender, parsedArgs);
                return true;
            } else if (type.isAssignableFrom(SpigotCommandSender.class)) {
                manager.invoke(m, command, defaultSender, parsedArgs);
                return true;
            }
            throw new IllegalAccessError(String.format("CommandSender or SpigotCommandSender expected at parameter %s at %s:%s", m.getParameters()[0].getName(), m.getName(), m.getDeclaringClass().getSimpleName()));
        } catch (IllegalCallerException exception) {
            sender.sendMessage(ChatColor.RED + exception.getMessage());
            return false;
        }
    }

    private boolean applyCooldown(@NotNull Method m, SpigotCommandSender defaultSender) {
        Cooldown cooldownAnnotation = (m.getAnnotation(Cooldown.class) != null ? m.getAnnotation(Cooldown.class) : m.getDeclaringClass().getAnnotation(Cooldown.class));
        if (cooldownAnnotation != null) {
            UUID uuid = defaultSender.getUniqueId();
            if (manager.isCooldownActive(uuid, getName())) {
                defaultSender.reply("&cYou must wait " + Duration.of(cooldownAnnotation.time(),
                        cooldownAnnotation.unit().toChronoUnit()).toSeconds() +
                        "&cs before using this command again.");
                return false;
            } else {
                manager.addCooldown(uuid, getName(), cooldownAnnotation);
            }
        }
        return true;
    }

    private static boolean applyRange(@NotNull Method m, SpigotCommandSender defaultSender, Number numberClass) {
        for (Parameter parameter : m.getParameters()) {
            Range range = parameter.getAnnotation(Range.class);
            Class<?> type = parameter.getType();
            if (range != null && type.isAssignableFrom(Number.class)) {
                Number number = (Number) parameter.getType().cast(numberClass);
                double num = number.doubleValue();
                if (num < range.min() || num > range.max()) {
                    defaultSender.reply("&cvalue " + parameter.getName() + " must be between " + range.min() + " and " + range.max() + ".");
                    return false;
                }
            }
        }
        return true;
    }
}
