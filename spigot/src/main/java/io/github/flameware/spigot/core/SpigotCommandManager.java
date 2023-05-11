package io.github.flameware.spigot.core;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.flameware.common.CommandExecutionType;
import io.github.flameware.common.annotations.Command;
import io.github.flameware.common.annotations.Subcommand;
import io.github.flameware.common.base.command.CommandImpl;
import io.github.flameware.common.base.command.CommandInfo;
import io.github.flameware.common.base.manager.AbstractCommandManager;

import io.github.flameware.common.utils.TiConsumer;
import lombok.Getter;

import org.bukkit.*;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The command manager for spigot commands.
 * <p>
 * it is highly suggested to use this for spigot and (as of now) paper.
 * <p>
 * <strong>TODO:</strong> add implementation for paper to use async tab completion and more performance/quality-of-life features.
 */
@SuppressWarnings({ "deprecation", "unused", "DataFlowIssue" })
public final class SpigotCommandManager extends AbstractCommandManager<Plugin> {
    // filter those who cannot be seen
    public static final String[] ONLINE_PLAYERS = Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .toArray(String[]::new);
    public static final String[] OFFLINE_PLAYERS = Arrays.stream(Bukkit.getOfflinePlayers())
            .map(OfflinePlayer::getName)
            .toArray(String[]::new);
    public static final String[] WORLDS = Bukkit.getWorlds().stream()
            .map(World::getName)
            .toArray(String[]::new);


    @Getter
    private final SimpleCommandMap commandMap;

    private static final SimpleCommandMap COMMAND_MAP = ((Supplier<SimpleCommandMap>) () -> {
        try {
            Field mapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            mapField.setAccessible(true);
            return (SimpleCommandMap) mapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }).get();

    private final TiConsumer<Method, Command, Object> wrapperCommandFunction;

    private SpigotCommandManager(Plugin plugin) {
        this(plugin, CommandExecutionType.SYNC);
    }

    private SpigotCommandManager(Plugin plugin, CommandExecutionType type) {
        super(plugin, type);
        //this.autoCompleterRegistry = completer;
        this.commandMap = COMMAND_MAP;
        this.wrapperCommandFunction = (m, commandClassAnnotation, object) -> {
            Class<?> commandClass = object.getClass();
            Subcommand subcommand = m.getAnnotation(Subcommand.class);
            Command command = m.getAnnotation(Command.class);
            if (m.isAnnotationPresent(Subcommand.class) && m.isAnnotationPresent(Command.class)){
                throw new IllegalCallerException("You cannot have both @Subcommand and @Command on a method/class at class " + commandClass.getSimpleName());
            }

            if (subcommand != null) {
                CommandInfo info = new CommandInfo(subcommand.name(), subcommand.desc(),
                        subcommand.perm(), subcommand.usage(), subcommand.aliases());
                CommandImpl commandImpl = new CommandImpl(this, info, object);
                commands.put(subcommand.name(), commandImpl);
            } else if (command != null) {
                CommandInfo info = new CommandInfo(commandClassAnnotation.name(), commandClassAnnotation.desc(),
                        commandClassAnnotation.perm(), commandClassAnnotation.usage(), commandClassAnnotation.aliases());
                io.github.flameware.common.base.command.Command wrapper = io.github.flameware.common.base.command.Command.wrap(info, this, object);
                commands.put(commandClassAnnotation.name(), wrapper);
                commandMap.register(plugin.getName(), new CommandWrapper(info, this, object));
            }
        };


        Map<Class<?>, Function<String, ?>> argumentMap = Map.of(
                Player.class, Bukkit::getPlayerExact,
                OfflinePlayer.class, Bukkit::getOfflinePlayer, // deprecation is here
                Material.class, Material::getMaterial,
                Enchantment.class, Enchantment::getByName,
                ItemStack.class, string -> new ItemStack(Material.getMaterial(string))
        );

        argumentHandler.addParser(argumentMap)
                       .addParser(World.class, Bukkit::getWorld);
    }

    /**
     * Returns a new instance of SpigotCommandManager.
     * @param plugin The plugin for the command framework.
     * @return a new instance of SpigotCommandManager.
     */
    @Contract("_ -> new")
    public static @NotNull SpigotCommandManager create(@NotNull Plugin plugin) {
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
    public static @NotNull SpigotCommandManager create(@NotNull Plugin plugin, @NotNull CommandExecutionType type) {
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
    @CanIgnoreReturnValue
    public SpigotCommandManager register(Object object) {
        Class<?> commandClass = object.getClass();
        Command commandClassAnnotation = commandClass.getAnnotation(Command.class);
        if (commandClass.isAnnotationPresent(Command.class) && commandClass.isAnnotationPresent(Subcommand.class))
            throw new IllegalStateException("You cannot have both @Command and @Subcommand on a method/class");
        if (commandClassAnnotation.name().equals("<CLASS_COMMAND_DEFAULT>"))
            throw new IllegalStateException("@Command annotated the class with name <CLASS_COMMAND_DEFAULT> at class " + commandClass.getSimpleName());
        for (Method m : commandClass.getMethods()) {
            wrapperCommandFunction.accept(m, commandClassAnnotation, object);
        }
        return this;
    }

    /**
     * Register multiple commands, better for readability.
     * <p>
     * This is null safe, it will silently ignore null commands which is good for in-method null checks or ternary operators.
     *
     * @param commands The commands to register, may be null
     * @return the manager for chaining.
     */
     @Contract(value = "_ -> this", pure = true)
     @CanIgnoreReturnValue
     public SpigotCommandManager register(Object @Nullable ... commands) {
         for (Object command : commands) {
             if (command == null) continue;
             this.register(command);
         }
         return this;
     }
}
