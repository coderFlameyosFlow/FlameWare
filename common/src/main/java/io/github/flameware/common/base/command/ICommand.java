package io.github.flameware.common.base.command;

import io.github.flameware.common.base.manager.CommandManager;
import io.github.flameware.common.exceptions.ArgumentParseException;
import io.github.flameware.common.exceptions.CooldownActiveException;
import io.github.flameware.common.exceptions.NotInRangeException;
import io.github.flameware.common.sender.CommandActor;
import io.github.flameware.common.utils.ParsingPredicate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

/**
 * The interface for command implementations.
 * <p>
 * Extend this if you want to make a custom command implementation <i>and/or</i> you want to fix certain parts or others of the command.
 * @author FlameyosFlow
 */
public interface ICommand {
    /**
     * Gets the name of the command.
     * @return the name
     */
    String getName();

    /**
     * Gets the description of the command.
     * @return the description
     */
    String getDescription();

    /**
     * Gets the usages of the command.
     * @return the usage
     */
    String getUsage();

    /**
     * Gets the required permission to run the command.
     * @return the permission
     */
    String getPermission();

    /**
     * Gets all the aliases of the command.
     * @return the list of aliases.
     */
    List<String> getAliases();

    /**
     * Executes the command, made for multimodule platforms with String[] args.
     * <p>
     * You can use the {@link CommandActor} to get the sender of the command,
     * <p>
     * and then you MUST understand each and every parameter for their usage,
     * or you will experience issues
     * @param m the method to execute.
     * @param sender the command actor
     * @param args the arguments for such modules
     * @param methodRunPredicate the predicate to check for requirements in method invocation
     * @param predicate the predicate for checking for argument parsing in method invocation
     * @return true or false, true is returned for errors that "fail successfully" or for successful invocation, else false.
     * @throws ArgumentParseException when the parsed arguments are invalid.
     * @throws CooldownActiveException when the cooldown is active
     * @throws NotInRangeException when a number argument is not in range
     */
     boolean execute(Method m, CommandActor<?> sender, String[] args,
                     Predicate<Class<?>> methodRunPredicate, ParsingPredicate predicate)
            throws ArgumentParseException, CooldownActiveException, NotInRangeException;

    Object[] setArguments(CommandActor<?> sender, Method m, String[] args, ParsingPredicate predicate)
            throws ArgumentParseException;

    /**
     * Runs the method made by the user
     *
     * @param m          the method to run
     * @param parsedArgs the parsed arguments
     * @param predicate  extra safe checks for method invocation
     * @param actor      the command actor
     * @return
     */
    boolean runMethod(Method m, Object[] parsedArgs, Predicate<Class<?>> predicate, CommandActor<?> actor);

    /**
     * Gets the info of the command that can and will represent
     * <p>
     * the usage, permission, name, description and the aliases
     * @return the command information
     */
    CommandInfo getInfo();

    /**
     * Wraps {@link ICommand} into {@link CommandImpl}.
     * @param info the info
     * @param manager the command manager
     * @param object the object
     * @return the command implementation
     */
    @Contract("_, _, _ -> new")
    static @NotNull ICommand wrap(CommandInfo info, CommandManager manager, Object object) {
        return new CommandImpl(manager, info, object);
    }
}
