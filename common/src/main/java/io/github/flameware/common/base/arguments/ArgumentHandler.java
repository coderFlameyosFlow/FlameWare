package io.github.flameware.common.base.arguments;

import io.github.flameware.common.annotations.Default;
import io.github.flameware.common.annotations.Join;

import io.github.flameware.common.exceptions.ArgumentParseException;
import io.github.flameware.common.sender.CommandActor;
import io.github.flameware.common.utils.TiPredicate;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

/**
 * The class that parses a String[] into a Object[].
 * <p>
 * This class is the default implementation of {@link ArgumentHandler}, and it is thread-safe by default due to the usage of ConcurrentMap.
 * <p>
 * This is normally a good enough implementation for most use cases, but feel free to extend it for anything else.
 */
public class ArgumentHandler {
    @Getter(AccessLevel.PACKAGE)
    private final ConcurrentMap<Class<?>, Function<String, ?>> parsers;
    private static final BiFunction<Parameter, Class<? extends Annotation>, ? extends Annotation> PARAMETER_ANNOTATION_FUNCTION = Parameter::getAnnotation;

    public ArgumentHandler() {
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

    @Contract(pure = true)
    @CanIgnoreReturnValue
    public final ArgumentHandler addParser(Map<Class<?>, Function<String, ?>> parser) {
        parsers.putAll(parser);
        return this;
    }

    @Contract(pure = true)
    @CanIgnoreReturnValue
    public final <T> ArgumentHandler addParser(Class<T> clazz, Function<String, T> argument) {
        parsers.putIfAbsent(clazz, argument);
        return this;
    }

    @Contract(pure = true)
    @CanIgnoreReturnValue
    public final ArgumentHandler addParserIfAbsent(@NotNull Map<Class<?>, Function<String, ?>> parser) {
        for (Map.Entry<Class<?>, Function<String, ?>> entry : parser.entrySet()) {
            parsers.putIfAbsent(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Contract(pure = true)
    @CanIgnoreReturnValue
    public final <T> ArgumentHandler addParserIfAbsent(Class<T> clazz, Function<String, T> argument) {
        parsers.putIfAbsent(clazz, argument);
        return this;
    }

    /**
     * Parse the arguments from a <strong>String[]</strong> into a <strong>Object[]</strong> which consists of possible arguments such as integers, etc.
     * @param sender The command actor.
     * @param method The method
     * @param args The arguments to parse
     * @param function the predicate to check for, allowed to be null.
     * @return the parsed arguments from String[] to Object[]
     */
    @Nullable
    public final Object @NotNull[] parseArguments(@NotNull CommandActor sender,
                                     @NotNull Method method,
                                     @NotNull String @NotNull [] args,
                                     @Nullable TiPredicate<Class<?>, String[], CommandActor> function) throws ArgumentParseException {
        Class<?>[] types = method.getParameterTypes();
        Class<?> firstType = types[0];
        int argIndex = 1;
        int length = types.length;
        int argsLength = args.length;
        Object[] list = new Object[length];
        Parameter[] parameters = method.getParameters();
        String methodName = method.getName();

        if (function != null && !function.test(firstType, args, sender)) {
            throw new IllegalStateException("You are not allowed to use this command!");
        }

        for (int i = 1; i < length; i++) {
            Parameter parameter = parameters[i];
            String name = parameter.getName();
            Function<String, ?> type = parsers.get(types[i]);
            if (type == null)
                throw new ArgumentParseException(String.format("Unregistered argument type: %s at %s", types[i].getSimpleName(), methodName));
            Default defaultAnnotation = (Default) PARAMETER_ANNOTATION_FUNCTION.apply(parameter, Default.class);
            if (defaultAnnotation != null && argsLength < argIndex) {
                list[i] = defaultAnnotation.def().isEmpty() ? null : type.apply(defaultAnnotation.def());
                argIndex++;
            } else if (argsLength <= argIndex) {
                throw new IllegalArgumentException(String.format("You must provide a value for %s!", name));
            } else {
                argIndex = parseArgument(parameter, type, argIndex, i, list, args);
                if (argIndex >= argsLength) break;
            }
        }

        return list;
    }

    private static int parseArgument(@NotNull Parameter parameter, Function<String, ?> type, int argIndex, int i, Object[] list, String[] args) {
        Join join = parameter.getAnnotation(Join.class);
        if (join != null) {
            list[i] = String.join(join.delimiter(), args);
            return args.length;
        }

        try {
            list[i] = type.apply(args[argIndex]);
            return ++argIndex;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("You must provide a number for %s!", parameter.getName()));
        }
    }
}
