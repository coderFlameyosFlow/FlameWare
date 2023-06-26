package io.github.flameware.common.base.command;

import io.github.flameware.common.annotations.Cooldown;
import io.github.flameware.common.annotations.Subcommand;
import io.github.flameware.common.base.manager.CommandManager;
import io.github.flameware.common.exceptions.ArgumentParseException;
import io.github.flameware.common.exceptions.CommandExecutionException;
import io.github.flameware.common.exceptions.CooldownActiveException;
import io.github.flameware.common.exceptions.NotInRangeException;
import io.github.flameware.common.sender.CommandActor;
import io.github.flameware.common.utils.ParsingPredicate;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link ICommand Command} default implementation which may usually be enough for your needs
 */
@AllArgsConstructor
public class CommandImpl implements ICommand {
    private final CommandManager manager;
    private final CommandInfo info;
    private final Object command;

    @Override
    public @NotNull String getName() {
        return info.getName();
    }

    @Override
    public @NotNull String getDescription() {
        return info.getDescription();
    }

    @Override
    public @NotNull String getUsage() {
        return info.getUsage();
    }

    @Override
    public @NotNull String getPermission() {
        return info.getPermission();
    }

    @Override
    public @NotNull List<String> getAliases() {
        return List.of(info.getAliases());
    }

    @Override
    public boolean execute(@NotNull Method m,
                           @NotNull CommandActor<?> sender,
                           String @NotNull [] args,
                           @NotNull Predicate<Class<?>> methodRunPredicate,
                           @NotNull ParsingPredicate predicate)
            throws ArgumentParseException, CooldownActiveException, NotInRangeException {
        Logger logger = manager.getLogger();
        String[] subargs = new String[args.length];
        try {
            Object[] parsedArgs = setArguments(sender, m, args, predicate);
            if (parsedArgs.length == 0) {
                applyCooldown(m, sender);
                return runMethod(m, new Object[]{}, methodRunPredicate, sender);
            }
            applyCooldown(m, sender);
            Subcommand subcommand = m.getAnnotation(Subcommand.class);
            var wrapper = manager.getCommands().get(args[0]);
            if (subcommand != null && wrapper != null) {
                System.arraycopy(args, 0, subargs, 0, subargs.length);
                logger.info("Command " + getName() + " " + args[0] + " was executed by " + sender.getName());
                return wrapper.execute(m, sender, subargs, methodRunPredicate, predicate);
            }
            return runMethod(m, parsedArgs, methodRunPredicate, sender);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            throw new IllegalArgumentException(exception.getMessage());
        }
    }

    @Override
    public Object[] setArguments(CommandActor<?> sender, Method m, String[] args, ParsingPredicate predicate) throws ArgumentParseException {
        try {
            return manager.getArgumentHandler().parseArguments(sender, m, args, predicate);
        } catch (CommandExecutionException e) {
            throw new ArgumentParseException(e.getMessage());
        }
    }

    @Override
    public boolean runMethod(@NotNull Method m, Object[] parsedArgs, Predicate<Class<?>> predicate, CommandActor<?> actor) {
        Class<?> type = m.getParameterTypes()[0];
        Logger logger = manager.getLogger();
        try {
            if (type.isAssignableFrom(CommandActor.class)) {
                manager.invoke(m, command, actor, parsedArgs);
                logger.info("Command " + getName() + " was executed by " + actor.getName());
                return true;
            } else if (predicate.test(type)) {
                logger.info("Command " + getName() + " was executed by " + actor.getName());
                return true;
            }
            String errorMessage = String.format("Platform Sender or CommandActor expected at parameter %s at %s:%s", m.getParameters()[0].getName(), m.getName(), m.getDeclaringClass().getSimpleName());
            logger.log(Level.SEVERE, errorMessage, new IllegalAccessError(errorMessage));
            return false;
        } catch (IllegalCallerException exception) {
            logger.log(Level.SEVERE, exception.getMessage(), new IllegalCallerException(exception.getMessage()));
            return false;
        }
    }

    @Override
    public @NotNull CommandInfo getInfo() {
        return info;
    }

    private void applyCooldown(@NotNull Method m, CommandActor<?> defaultSender) throws CooldownActiveException {
        Cooldown cooldownAnnotation = (m.getAnnotation(Cooldown.class) != null ? m.getAnnotation(Cooldown.class) : m.getDeclaringClass().getAnnotation(Cooldown.class));
        if (cooldownAnnotation != null) {
            String name = defaultSender.getName();
            if (manager.isCooldownActive(name, getName())) {
                throw new CooldownActiveException("You must wait " + Duration.of(cooldownAnnotation.time(),
                        cooldownAnnotation.unit().toChronoUnit()).toSeconds() +
                        "&cs before using this command again.");
            } else {
                manager.addCooldown(name, getName(), cooldownAnnotation);
            }
        }
    }
}