package io.github.flameware.common.base.arguments;

import io.github.flameware.common.exceptions.CommandExecutionException;
import io.github.flameware.common.sender.CommandActor;
import io.github.flameware.common.utils.ParsingPredicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

/**
 * @author FlameyosFlow
 */
public interface ArgumentHandler<H extends ArgumentHandler<H>> {
    /**
     * Parse the arguments from a <strong>String[]</strong> into a <strong>Object[]</strong> which consists of possible arguments such as integers, etc.
     * @param sender The command actor.
     * @param method The method
     * @param args The arguments to parse
     * @param function the predicate to check for while parsing, allowed to be null.
     * @return the parsed arguments from String[] to Object[]
     * @throws CommandExecutionException when the arguments could not be parsed
     */
    Object[] parseArguments(@NotNull CommandActor<?> sender,
                           @NotNull Method method,
                           @NotNull String[] args,
                           @Nullable ParsingPredicate function)
            throws CommandExecutionException;

    /**
     * adds a parser to the argument handler if it is not already present in the map.
     * @param clazz the class of the argument
     * @param argument the argument that can be parsed
     * @param <T> the type of the argument
     * @return the argument handler for chaining
     */
    <T> H addParserIfAbsent(Class<? extends T> clazz, Function<String, ? extends T> argument);

    /**
     * puts all the parsers to the argument handler if it is not already present in the map.
     * @param parser the parser map.
     * @return the argument handler for chaining
     * @param <T> the type of the argument
     */
    <T> H addParserIfAbsent(@NotNull Map<Class<? extends T>, Function<String, ? extends T>> parser);

    /**
     * adds a parser to the argument handler which may override the current one.
     * @param clazz the class of the argument
     * @param argument the argument that can be parsed
     * @param <T> the type of the argument
     * @return the argument handler for chaining
     */
    <T> H addParser(Class<? extends T> clazz, Function<String, ? extends T> argument);

    /**
     * puts all the parsers to the argument handler which may override the current one.
     * @param parser the parser map.
     * @return the argument handler for chaining
     * @param <T> the type of the argument
     */
    <T> H addParser(Map<Class<? extends T>, Function<String, ? extends T>> parser);
}
