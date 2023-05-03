package io.github.flameware.spigot.core;

import io.github.flameware.common.annotations.Default;
import io.github.flameware.common.annotations.Join;
import io.github.flameware.common.base.arguments.*;

import io.github.flameware.common.sender.InterfaceCommandSender;
import io.github.flameware.common.utils.TiFunction;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class ArgumentHandler {
    private final ConcurrentMap<Class<?>, Function<String, ?>> parsers = new ConcurrentHashMap<>();

    public ArgumentHandler() {
        parsers.put(String.class, Function.identity());
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
    public final ArgumentHandler addParser(Map<Class<?>, Function<String, ?>> parser) {
        parsers.putAll(parser);
        return this;
    }

    @Contract(pure = true)
    public final <T> ArgumentHandler addParser(Class<T> clazz, Function<String, ?> argument) {
        parsers.putIfAbsent(clazz, argument);
        return this;
    }

    @Nullable
    public final Object[] parse(@NotNull InterfaceCommandSender sender,
                          @NotNull Method method,
                          @NotNull String[] args,
                          @Nullable TiFunction<Class<?>, String[], InterfaceCommandSender, Boolean> function) {
        Class<?>[] types = method.getParameterTypes();
        Class<?> firstType = types[0];
        int argIndex = 1;
        int length = types.length;
        Object[] list = new Object[length];
        Parameter[] parameters = method.getParameters();

        if (function != null && !function.apply(firstType, args, sender)) {
            throw new IllegalStateException("You are not allowed to use this command!");
        }

        for (int i = 1; i < length; i++) {
            Function<String, ?> type = parsers.get(types[i]);
            if (type == null)
                throw new IllegalStateException(String.format("Unregistered argument type: %s at %s", types[i].getSimpleName(), method.getName()));
            Parameter parameter = parameters[i];
            Default argAnnotation = parameter.getAnnotation(Default.class);
            if (argAnnotation != null && args.length < argIndex) {
                list[i] = argAnnotation.def().isEmpty() ? null : type.apply(argAnnotation.def());
                argIndex++;
            } else if (args.length <= argIndex) {
                throw new IllegalArgumentException(String.format("You must provide a value for %s!", parameter.getName()));
            } else {
                argIndex = parseArgument(parameter, type, argIndex, i, list, args);
            }
        }

        return list;
    }

    private static int parseArgument(@NotNull Parameter parameter, Function<String, ?> type, int argIndex, int i, Object[] list, String[] args) {
        try {
            if (parameter.isAnnotationPresent(Join.class)) {
                Join join = parameter.getAnnotation(Join.class);
                list[i] = GreedyStringArgument.join(args, join, argIndex);
                return args.length;
            } else {
                list[i] = type.apply(args[argIndex]);
                return ++argIndex;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("You must provide a number for %s!", parameter.getName()));
        }
    }
}
