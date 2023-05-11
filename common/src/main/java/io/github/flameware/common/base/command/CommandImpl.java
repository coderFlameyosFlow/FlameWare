package io.github.flameware.common.base.command;

import io.github.flameware.common.annotations.Cooldown;
import io.github.flameware.common.annotations.Range;
import io.github.flameware.common.annotations.Subcommand;
import io.github.flameware.common.base.manager.CommandManager;
import io.github.flameware.common.exceptions.ArgumentParseException;
import io.github.flameware.common.sender.CommandActor;
import io.github.flameware.common.utils.TiPredicate;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public record CommandImpl(CommandManager manager, CommandInfo info, Object command) implements Command {
    @Override
    public @NotNull String getName() {
        return info.name();
    }

    @Override
    public @NotNull String getDescription() {
        return info.description();
    }

    @Override
    public @NotNull String getUsage() {
        return info.usage();
    }

    @Override
    public @NotNull String getPermission() {
        return info.permission();
    }

    @Override
    public @NotNull List<String> getAliases() {
        return List.of(info.aliases());
    }

    @Override
    public boolean execute(@NotNull Method m, @NotNull CommandActor sender, String @NotNull [] args, Predicate<Class<?>> methodRunPredicate, TiPredicate<Class<?>, String[], CommandActor> predicate) throws ArgumentParseException {
        Logger logger = manager.getLogger();
        try {
            String[] subargs = new String[args.length - 1];
            Object[] parsedArgs = setArguments(sender, m, args, predicate);
            if (parsedArgs.length == 0) return false;
            if (applyCooldown(m, sender)) return false;

            Class<?>[] types = m.getParameterTypes();
            int typesLength = types.length;
            for (int i = 0; i < typesLength; i++) {
                Class<?> type = m.getParameterTypes()[i];
                if (type.isAssignableFrom(Number.class) && !applyRange(m.getParameters()[i], sender, (Double) type.cast(parsedArgs[i]))) {
                    return true;
                }
            }

            Subcommand subcommand = m.getAnnotation(Subcommand.class);
            var wrappers = manager.getCommands();
            Command wrapper = wrappers.get(args[0]);
            if (subcommand != null && wrapper != null) {
                System.arraycopy(args, 1, subargs, 0, subargs.length);
                logger.info("Command " + getName() + " " + args[0] + " was executed by " + sender.getName());
                wrapper.execute(m, sender, subargs, methodRunPredicate, predicate);
            } else {
                runMethod(m, setArguments(sender, m, args, predicate), methodRunPredicate, sender);
                logger.info("Command " + getName() + " was executed by " + sender.getName());
            }
        } catch (IllegalStateException | IllegalArgumentException exception) {
            throw new IllegalArgumentException(exception.getMessage());
        }
        return true;
    }

    @Override
    public Object[] setArguments(CommandActor sender, Method m, String[] args, TiPredicate<Class<?>, String[], CommandActor> predicate) throws ArgumentParseException {
        try {
            return manager.getArgumentHandler().parseArguments(sender, m, args, predicate);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            throw new ArgumentParseException(exception.getMessage());
        }
    }

    @Override
    public void runMethod(Method m, Object[] parsedArgs, Predicate<Class<?>> predicate, CommandActor actor) {
        Class<?> type = m.getParameterTypes()[0];
        Logger logger = manager.getLogger();
        try {
            if (type.isAssignableFrom(CommandActor.class)) {
                manager.invoke(m, command, actor, parsedArgs);
                return;
            } else if (predicate.test(type))
                return;
            String errorMessage = String.format("Platform Sender or CommandActor expected at parameter %s at %s:%s", m.getParameters()[0].getName(), m.getName(), m.getDeclaringClass().getSimpleName());
            logger.log(Level.SEVERE, errorMessage, new IllegalAccessError(errorMessage));
        } catch (IllegalCallerException exception) {
            throw new IllegalCallerException(exception.getMessage());
        }
    }

    @Override
    public @NotNull CommandInfo getInfo() {
        return info;
    }

    private boolean applyCooldown(@NotNull Method m, CommandActor defaultSender) {
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

    private static boolean applyRange(@NotNull Parameter parameter, CommandActor defaultSender, Number numberClass) {
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
        return true;
    }
}
