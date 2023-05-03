package io.github.flameware.spigot.core;

import io.github.flameware.common.CommandExecutionType;
import io.github.flameware.common.annotations.Command;
import io.github.flameware.common.annotations.Subcommand;
import io.github.flameware.common.base.command.CommandInfo;
import io.github.flameware.common.base.manager.AbstractCommandManager;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;


@SuppressWarnings("deprecation")
public final class SpigotCommandManager extends AbstractCommandManager<Plugin> {
    @Getter
    private final List<Object> commands;
    @Getter
    private final ConcurrentMap<String, CommandWrapper> wrappers = new ConcurrentHashMap<>();
    @Getter
    private final ArgumentHandler argumentHandler;
    private final SimpleCommandMap map = new SimpleCommandMap(plugin.getServer());
    private final BiFunction<Command, Object, CommandWrapper> wrapperCommandFunction = (commandAnnotation, object) -> {
        if (commandAnnotation == null)
            return null;
        CommandInfo info = new CommandInfo(commandAnnotation.name(),
                commandAnnotation.desc(),
                commandAnnotation.perm(),
                commandAnnotation.usage(),
                commandAnnotation.aliases());
        CommandWrapper wrapper = new CommandWrapper(info, this, object);
        map.register(plugin.getName(), wrapper);
        wrappers.put(commandAnnotation.name(), wrapper);
        return wrapper;
    };

    private SpigotCommandManager(Plugin plugin) {
        super(plugin, CommandExecutionType.SYNC);
        this.argumentHandler = new ArgumentHandler();

        Map<Class<?>, Function<String, ?>> argumentMap = Map.of(
                Player.class, Bukkit::getPlayerExact,
                OfflinePlayer.class, Bukkit::getOfflinePlayer,
                Material.class, Material::getMaterial,
                Enchantment.class, Enchantment::getByName,
                ItemStack.class, string -> new ItemStack(Material.getMaterial(string))
        );

        argumentHandler.addParser(argumentMap);
        this.commands = new ArrayList<>();
    }

    private SpigotCommandManager(Plugin plugin, CommandExecutionType type) {
        super(plugin, type);
        this.argumentHandler = new ArgumentHandler();

        Map<Class<?>, Function<String, ?>> argumentMap = Map.of(
                Player.class, Bukkit::getPlayerExact,
                OfflinePlayer.class, Bukkit::getOfflinePlayer,
                Material.class, Material::getMaterial,
                Enchantment.class, Enchantment::getByName,
                ItemStack.class, string -> new ItemStack(Material.getMaterial(string))
        );

        argumentHandler.addParser(argumentMap);
        this.commands = new ArrayList<>();
    }

    /**
     * Returns a new instance of SpigotCommandManager.
     * @param plugin The plugin for the command framework.
     * @return a new instance of SpigotCommandManager.
     */
    @Contract("_ -> new")
    public static @NotNull SpigotCommandManager of(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin);
        SpigotCommandManager manager = new SpigotCommandManager(plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(manager), plugin);
        return manager;
    }

    /**
     * Returns a new instance of SpigotCommandManager.
     * @param plugin The plugin for the command framework.
     * @param type The execution type, SYNC or ASYNC
     * @return a new instance of SpigotCommandManager.
     */
    @Contract("_, _ -> new")
    public static @NotNull SpigotCommandManager of(@NotNull Plugin plugin, @NotNull CommandExecutionType type) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(type);

        SpigotCommandManager manager = new SpigotCommandManager(plugin, type);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(manager), plugin);
        return manager;
    }

    /**
     * Registers a single command at once.
     * @param object The command class.
     * @return the manager for chaining.
     */
    @Contract(value = "_ -> this", pure = true)
    public SpigotCommandManager register(@NotNull Object object) {
        Class<?> commandClass = object.getClass();
        Command commandClassAnnotation = commandClass.getAnnotation(Command.class);
        if (commandClass.isAnnotationPresent(Command.class) && commandClass.isAnnotationPresent(Subcommand.class)) {
            throw new IllegalStateException("You cannot have both @Command and @Subcommand on a method/class");
        wrapperCommandFunction.apply(commandClassAnnotation, object);
        return this;
    }

    /**
     * Register multiple commands, better for readability.
     * <p>
     * This is null safe, it will silently ignore null commands.
     *
     * @param commands The commands to register, may be null
     * @return the manager for chaining.
     */
    @Contract(value = "_ -> this", pure = true)
    public SpigotCommandManager register(Object... commands) {
        for (Object command : commands) {
            if (command == null) continue;
            this.register(command);
        }
        return this;
    }
}
