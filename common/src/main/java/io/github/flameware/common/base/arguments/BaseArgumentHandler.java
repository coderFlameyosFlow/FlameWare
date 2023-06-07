package io.github.flameware.common.base.arguments;

import io.github.flameware.common.annotations.Arg;
import io.github.flameware.common.annotations.Default;
import io.github.flameware.common.annotations.Join;

import io.github.flameware.common.annotations.Range;
import io.github.flameware.common.base.manager.Message;
import io.github.flameware.common.exceptions.ArgumentParseError;
import io.github.flameware.common.exceptions.CommandExecutionException;
import io.github.flameware.common.exceptions.NotInRangeException;
import io.github.flameware.common.sender.CommandActor;
import io.github.flameware.common.utils.TiPredicate;

import lombok.AccessLevel;
import lombok.Getter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

/**
 * The class that parses a String[] into a Object[].
 * <p>
 * This class is the default implementation of {@link BaseArgumentHandler}, and it is thread-safe by default due to the usage of ConcurrentMap.
 * <p>
 * This is normally a good enough implementation for most use cases, but feel free to extend it for anything else.
 * @author FlameyosFlow
 */
public class BaseArgumentHandler implements ArgumentHandler {
    @Getter(AccessLevel.PACKAGE)
    private final ConcurrentMap<Class<?>, Function<String, ?>> parsers;

    public BaseArgumentHandler() {
        parsers = new ConcurrentHashMap<>(50);
        parsers.put(String.class, (s) -> s);
        parsers.put(Boolean.class, Boolean::parseBoolean);
        parsers.put(Integer.class, Integer::parseInt);
        parsers.put(Double.class, Double::parseDouble);
        parsers.put(Long.class, Long::parseLong);
        parsers.put(Float.class, Float::parseFloat);
        parsers.put(boolean.class, Boolean::parseBoolean);
        parsers.put(double.class, Double::parseDouble);
        parsers.put(long.class, Long::parseLong);
        parsers.put(float.class, Float::parseFloat);
        parsers.put(int.class, Integer::parseInt);
    }

    @Override
    @Contract(pure = true)
    @CanIgnoreReturnValue
    public final <T> BaseArgumentHandler addParser(Map<Class<? extends T>, Function<String, ? extends T>> parser) {
        parsers.putAll(parser);
        return this;
    }

    @Override
    @Contract(pure = true)
    @CanIgnoreReturnValue
    public final <T> BaseArgumentHandler addParser(Class<? extends T> clazz, Function<String, ? extends T> argument) {
        parsers.putIfAbsent(clazz, argument);
        return this;
    }

    @Override
    @Contract(pure = true)
    @CanIgnoreReturnValue
    public final <T> BaseArgumentHandler addParserIfAbsent(@NotNull Map<Class<? extends T>, Function<String, ? extends T>> parser) {
        for (Map.Entry<Class<? extends T>, Function<String, ? extends T>> entry : parser.entrySet()) {
            parsers.putIfAbsent(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    @Contract(pure = true)
    @CanIgnoreReturnValue
    public final <T> BaseArgumentHandler addParserIfAbsent(Class<? extends T> clazz, Function<String, ? extends T> argument) {
        parsers.putIfAbsent(clazz, argument);
        return this;
    }

    @Nullable
    @Override
    public final Object[] parseArguments(@NotNull CommandActor sender,
                                     @NotNull Method method,
                                     @NotNull String @NotNull [] args,
                                     @Nullable TiPredicate<Class<?>, String[], CommandActor> function)
            throws CommandExecutionException {
        Class<?>[] types = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();
        if (args.length == 0 && types.length == 1) return new Object[0];

        Class<?> firstType = types[0];
        int argIndex = 0, typesLength = types.length, argsLength = args.length;
        Object[] list = new Object[typesLength];
        String methodName = method.getName();

        if (function != null && !function.test(firstType, args, sender)) {
            throw new CommandExecutionException("You are not allowed to use this command!");
        }

        while (argIndex <= argsLength) {
            Parameter parameter = parameters[argIndex];
            Arg arg = parameter.getAnnotation(Arg.class);
            Function<String, ?> type = parsers.get(types[argIndex]);
            if (type == null)
                throw new ArgumentParseError(String.format("Unregistered argument type: %s at %s", types[argIndex].getSimpleName(), methodName));
            String id = arg == null ? parameter.getName() : arg.id();
            // TODO: figure out a good way to catch absent values and check for optional arguments
            /*Default defaultAnnotation = parameter.getAnnotation(Default.class);
            if (defaultAnnotation != null && argIndex > argsLength) {
                list[argIndex] = defaultAnnotation.def().isEmpty() ? null : type.apply(defaultAnnotation.def());
                argIndex++;
                continue;
            } else if (argIndex > argsLength) {
                throw new IllegalArgumentException(String.format("You must provide a value for %s!", id));
            }*/
            argIndex = parse(parameter, args, argIndex, list, argsLength, id, type, types);
        }

        return list;
    }

    private static int parse(Parameter parameter, String @NotNull [] args, int argIndex, Object[] list, int argsLength, String id, Function<String, ?> type, Class<?>[] types) {
        Join join = parameter.getAnnotation(Join.class);
        if (join != null) {
            list[argIndex] = String.join(join.delimiter(), args);
            return argsLength;
        }

        Range range = parameter.getAnnotation(Range.class);
        try {
            list[argIndex] = type.apply(args[argIndex]);
            if (range != null && types[argIndex].isAssignableFrom(Number.class)) {
                applyRange(range, parameter, (Double) types[argIndex].cast(list[argIndex]));
            }
            return ++argIndex;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("You must provide a number for %s!", id));
        } catch (NotInRangeException e) {
            // assuming range does exist then
            throw new IllegalArgumentException(Message.NOT_IN_RANGE.getMessage()
                    .replace("%arg%", id)
                    .replace("%min%", Double.toString(range.min()))
                    .replace( "%max%", Double.toString(range.max())));
        }
    }

    private static void applyRange(Range range, Parameter parameter, Double value) throws NotInRangeException {
        if (range != null) {
            double number = (Double) parameter.getType().cast(value);
            if (number < range.min() || number > range.max()) throw new NotInRangeException();
        }
    }
}
