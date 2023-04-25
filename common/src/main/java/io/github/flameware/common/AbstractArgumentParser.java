package io.github.flameware.common;

import io.github.flameware.common.annotations.Arg;
import io.github.flameware.common.annotations.Range;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractArgumentParser {
    private static final Map<Method, Integer[]> parameterIndexesCache = new HashMap<>();
    private static final Set<Class<?>> supportedTypes = new HashSet<>();

    static {
        supportedTypes.add(String.class);
        supportedTypes.add(int.class);
        supportedTypes.add(Integer.class);
        supportedTypes.add(double.class);
        supportedTypes.add(Double.class);
        supportedTypes.add(boolean.class);
        supportedTypes.add(Boolean.class);
        supportedTypes.add(float.class);
        supportedTypes.add(Float.class);
        supportedTypes.add(long.class);
        supportedTypes.add(Long.class);
    }

    public Map<String, String> parseArgs(String[] args, Method method) {
        Integer[] parameterIndexes = parameterIndexesCache.computeIfAbsent(method, m -> IntStream.range(0, m.getParameterCount()).boxed().toArray(Integer[]::new));

        return IntStream.range(0, parameterIndexes.length)
                .filter(i -> method.getParameters()[parameterIndexes[i]].isAnnotationPresent(Arg.class))
                .boxed()
                .collect(Collectors.toMap(
                        i -> Integer.toString(i),
                        i -> parseArgument(args, method.getParameters()[parameterIndexes[i]])
                ));
    }

    private String parseArgument(String[] args, java.lang.reflect.Parameter parameter) {
        Arg argAnnotation = parameter.getAnnotation(Arg.class);
        int position = argAnnotation.hashCode();

        if (position >= args.length) {
            if (argAnnotation.optional()) {
                throw new IllegalArgumentException("Missing value for parameter " + position);
            }
            return argAnnotation.defaultValue();
        }

        Class<?> type = parameter.getType();
        if (!supportedTypes.contains(type)) {
            throw new IllegalArgumentException("Unsupported parameter type: " + type.getName());
        }

        Range range = parameter.getAnnotation(Range.class);
        if (argAnnotation.join()) {
            return parseJoinArgument(args, position);
        } else if (type == int.class || type == Integer.class) {
            return parseIntegerArgument(args, position, range);
        } else if (type == double.class || type == Double.class) {
            return parseDoubleArgument(args, position, range);
        } else if (type == boolean.class || type == Boolean.class) {
            return parseBooleanArgument(args, position, argAnnotation);
        } else if (type == float.class || type == Float.class) {
            return parseFloatArgument(args, position, range);
        } else if (type == long.class || type == Long.class) {
            return parseLongArgument(args, position, range);
        } else {
            return args[position];
        }
    }

    private String parseJoinArgument(String[] args, int position) {
        StringBuilder builder = new StringBuilder();
        for (int i = position; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        return builder.toString().trim();
    }

    private String parseIntegerArgument(String @NotNull [] args, int position, Range rangeAnnotation) {
        int value;
        try {
            value = Integer.parseInt(args[position]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for parameter " + position + ": " + args[position]);
        }
        if (rangeAnnotation != null && (value < rangeAnnotation.min() || value > rangeAnnotation.max())) {
            throw new IllegalArgumentException("Value out of range for parameter " + position + ": " + value);
        }
        return Integer.toString(value);
    }

    private String parseDoubleArgument(String[] args, int position, Range rangeAnnotation) {
        double value;
        try {
            value = Double.parseDouble(args[position]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for parameter " + position + ": " + args[position]);
        }
        if (rangeAnnotation != null && (value < rangeAnnotation.min() || value > rangeAnnotation.max())) {
            throw new IllegalArgumentException("Value out of range for parameter " + position + ": " + value);
        }
        return Double.toString(value);
    }

    private String parseBooleanArgument(String[] args, int position, Arg argAnnotation) {
        boolean value = !argAnnotation.defaultValue().isEmpty() && Boolean.parseBoolean(argAnnotation.defaultValue());
        if (args[position].equalsIgnoreCase("true") || args[position].equalsIgnoreCase("false")) {
            value = Boolean.parseBoolean(args[position]);
        } else if (!argAnnotation.optional()) {
            throw new IllegalArgumentException("Invalid value for parameter " + position + ": " + args[position]);
        }
        return Boolean.toString(value);
    }

    private String parseFloatArgument(String[] args, int position, Range rangeAnnotation) {
        float value;
        try {
            value = Float.parseFloat(args[position]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for parameter " + position + ": " + args[position]);
        }
        if (rangeAnnotation != null && (value < rangeAnnotation.min() || value > rangeAnnotation.max())) {
            throw new IllegalArgumentException("Value out of range for parameter " + position + ": " + value);
        }
        return Float.toString(value);
    }

    private String parseLongArgument(String[] args, int position, Range rangeAnnotation) {
        long value;
        try {
            value = Long.parseLong(args[position]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for parameter " + position + ": " + args[position]);
        }
        if (rangeAnnotation != null && (value < rangeAnnotation.min() || value > rangeAnnotation.max())) {
            throw new IllegalArgumentException("Value out of range for parameter " + position + ": " + value);
        }
        return Long.toString(value);
    }
}
