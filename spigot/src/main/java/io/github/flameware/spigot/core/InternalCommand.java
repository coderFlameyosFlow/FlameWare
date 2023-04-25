package io.github.flameware.spigot.core;

import io.github.flameware.common.annotations.Command;
import io.github.flameware.common.annotations.Default;
import io.github.flameware.common.base.CommandDefinition;
import io.github.flameware.common.exceptions.SenderNotConsoleException;
import io.github.flameware.common.exceptions.SenderNotPlayerException;
import io.github.flameware.spigot.ArgumentParser;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import revxrsal.asm.BoundMethodCaller;
import revxrsal.asm.MethodCaller;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InternalCommand extends org.bukkit.command.Command {
    private final Object command;
    private final Method method;

    public InternalCommand(CommandDefinition definition,
                           Object command) {
        super(definition.getName(), definition.getDescription(), definition.getUsage(), definition.getAliases().length > 1 ? Arrays.asList(definition.getAliases()) : Collections.singletonList(definition.getAliases()[0]));
        this.command = command;
        if (!definition.getPermission().isEmpty())
            setPermission(definition.getPermission());
        Class<?> clazz = command.getClass();
        Map<String, Method> commands = new HashMap<>();
        if (clazz.isAnnotationPresent(Command.class)) {
            commands.put(clazz.getAnnotation(Command.class).name().toLowerCase(), null);
        }

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Default.class)) commands.put(definition.getName(), m);
            else if (m.isAnnotationPresent(Command.class)) {
                commands.put(clazz.getAnnotation(Command.class).name().toLowerCase(), m);
            }
        }

        this.method = commands.get(getName().toLowerCase());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return true;
        try {
            var parsedArgs = new ArgumentParser().parse(args, method, sender);
            BoundMethodCaller methodCaller = MethodCaller.wrap(method).bindTo(command);
            methodCaller.call(sender, parsedArgs);
        } catch (SenderNotPlayerException e) {
            sender.sendMessage(colorize("&cOnly players can execute this command."));
            return false;
        } catch (SenderNotConsoleException e) {
            sender.sendMessage(colorize("&cOnly console can execute this command."));
            return false;
        }
        return true;
    }

    public String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
