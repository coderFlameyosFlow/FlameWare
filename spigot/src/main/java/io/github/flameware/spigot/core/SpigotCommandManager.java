package io.github.flameware.spigot.core;

import io.github.flameware.common.annotations.Command;
import io.github.flameware.common.annotations.Default;
import io.github.flameware.common.base.CommandDefinition;
import io.github.flameware.common.base.manager.AbstractCommandManager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class SpigotCommandManager extends AbstractCommandManager<Plugin> {
    public SpigotCommandManager(Plugin plugin) {
        super(plugin);
    }

    public CommandMap getCommandMap() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerCommands(boolean useCustomSender, Object... objects) {
        for (Object object : objects) {
            Command commandAnnotationClass = object.getClass().getAnnotation(Command.class);
            Method[] methods = object.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Command.class)) {
                    Command commandAnnotationMethod = method.getAnnotation(Command.class);
                    CommandDefinition definition = CommandDefinition.of(
                            commandAnnotationMethod.name(),
                            commandAnnotationMethod.description(),
                            commandAnnotationMethod.permission(),
                            commandAnnotationMethod.aliases(),
                            commandAnnotationMethod.usage());
                    InternalCommand internalCommand = new InternalCommand(definition, object);
                    getCommandMap().register(getPlugin().getName(), internalCommand);
                } else if (method.isAnnotationPresent(Default.class)) {
                    CommandDefinition definition = CommandDefinition.of(
                            commandAnnotationClass.name(),
                            commandAnnotationClass.description(),
                            commandAnnotationClass.permission(),
                            commandAnnotationClass.aliases(),
                            commandAnnotationClass.usage());
                    InternalCommand internalCommand = new InternalCommand(definition, object);
                    getCommandMap().register(getPlugin().getName(), internalCommand);
                }
                runMethodsAsync(object);
            }
        }
    }
}
