package io.github.flameware.common.base.arguments;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import io.github.flameware.common.annotations.AutoComplete;
import io.github.flameware.common.base.manager.CommandManager;
import io.github.flameware.common.utils.BiIntFunction;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.*;

@SuppressWarnings("unused")
public class ArgumentSuggestionRegistry {
    private final ConcurrentMap<String, List<String>> autoCompletions;
    private final ConcurrentMap<Class<?>, Function<String, ?>> parsers;

    public ArgumentSuggestionRegistry(CommandManager manager) {
        autoCompletions = new ConcurrentHashMap<>(25);
        parsers = manager.getArgumentHandler().getParsers();
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestion(@NotNull String completion, @NotNull String[] strings) {
        if (completion.charAt(0) != '@')
            throw new IllegalArgumentException("Completion must start with '@'");
        autoCompletions.put(completion, List.of(strings));
        return this;
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestion(@NotNull String completion, @NotNull List<String> strings) {
        if (completion.charAt(0) != '@')
            throw new IllegalArgumentException("Completion must start with '@'");
        autoCompletions.put(completion, strings);
        return this;
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestion(@NotNull String completion, @NotNull Supplier<List<String>> strings) {
        if (completion.charAt(0) != '@')
            throw new IllegalArgumentException("Completion must start with '@'");
        autoCompletions.put(completion, strings.get());
        return this;
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestionIfAbsent(@NotNull String completion, @NotNull List<String> strings) {
        autoCompletions.putIfAbsent(completion, strings);
        return this;
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestionIfAbsent(@NotNull String completion, @NotNull String[] strings) {
        autoCompletions.putIfAbsent(completion, List.of(strings));
        return this;
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestionIfAbsent(@NotNull String completion, @NotNull Supplier<List<String>> strings) {
        autoCompletions.putIfAbsent(completion, strings.get());
        return this;
    }

    @ApiStatus.Experimental
    public final @NotNull @Unmodifiable List<String> autoComplete(@NotNull Class<?> commandClass, BiIntFunction<Class<?>, List<String>> function) {
        Method[] methods = commandClass.getDeclaredMethods();
        List<String> list = new ArrayList<>(1000);
        int methodCount = methods.length;
        for (int i = 0; i < methodCount; i++) {
            Method method = methods[i];
            Parameter parameter = method.getParameters()[i];
            Class<?> type = method.getParameterTypes()[i];
            if (function.apply(type, list) == 0)
                continue;
            AutoComplete autoCompleteAnnotation = parameter.getAnnotation(AutoComplete.class);
            if (autoCompleteAnnotation != null) {
                if (type == String.class) {
                    this.autoCompleteStrings(autoCompleteAnnotation, parameter, list);
                } else {
                    var parser = parsers.get(type);
                    if (parser == null) throw new IllegalArgumentException("Unknown autocompleted type: " + type);
                    list.add(String.valueOf(parser.apply(autoCompleteAnnotation.value()[i])));
                }
            }
        }
        return list;
    }

    private void autoCompleteStrings(@NotNull AutoComplete autoCompleteAnnotation, @NotNull Parameter parameter, List<String> list) {
        String[] autoCompletions = autoCompleteAnnotation.value();
        for (String autoCompletion : autoCompletions) {
            if (autoCompletion.charAt(0) == '@') {
                List<String> strings = this.autoCompletions.get(autoCompletion);
                if (strings == null)
                    throw new IllegalArgumentException("Unknown auto-complete type starting with @: " + autoCompletion);
                list.addAll(strings);
            }
            list.add(autoCompletion);
        }
    }
}
