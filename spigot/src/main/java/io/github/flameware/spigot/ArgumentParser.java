package io.github.flameware.spigot;

import io.github.flameware.common.AbstractArgumentParser;

import io.github.flameware.common.exceptions.SenderNotConsoleException;
import io.github.flameware.common.exceptions.SenderNotPlayerException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class ArgumentParser extends AbstractArgumentParser<OfflinePlayer> {
    public Object[] parse(String[] args, Method method, CommandSender commandSender) throws SenderNotPlayerException, SenderNotConsoleException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) return new Object[0];
        Class<?> firstParameterType = parameterTypes[0];
        Object[] objects = parseArgs(args, method).values().toArray();

        if (firstParameterType == Player.class) {
            if (commandSender instanceof Player) return objects;
            else throw new SenderNotPlayerException(null);
        } else if (firstParameterType == CommandSender.class) {
            return objects;
        } else if (firstParameterType == ConsoleCommandSender.class) {
            if (commandSender instanceof ConsoleCommandSender) return objects;
            else throw new SenderNotConsoleException(null);
        }
        return new Object[0];
    }
}
