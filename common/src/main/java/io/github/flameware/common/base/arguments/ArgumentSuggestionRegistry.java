package io.github.flameware.common.base.arguments;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import io.github.flameware.common.annotations.Suggest;
import io.github.flameware.common.base.manager.CommandManager;
import io.github.flameware.common.utils.BiIntFunction;

import io.github.flameware.common.utils.SuggestionPredicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.*;

/**
 * @author FlameyosFlow
 */
@SuppressWarnings("unused")
public class ArgumentSuggestionRegistry {
    private final ConcurrentMap<String, List<String>> autoCompletions;
    private final ConcurrentMap<Class<?>, Function<String, ?>> parsers;

    public ArgumentSuggestionRegistry(@NotNull CommandManager manager) {
        autoCompletions = new ConcurrentHashMap<>(25);
        parsers = manager.getArgumentHandler().getParsers();
    }

    private void checkCompletion(@NotNull String completion) {
        if (completion.charAt(0) != '@') {
            throw new IllegalArgumentException("Completion must start with '@'");
        }
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestion(@NotNull String completion, @NotNull String[] strings) {
        checkCompletion(completion);
        autoCompletions.put(completion, List.of(strings));
        return this;
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestion(@NotNull String completion, @NotNull List<String> strings) {
        checkCompletion(completion);
        autoCompletions.put(completion, strings);
        return this;
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestion(@NotNull String completion, @NotNull Supplier<List<String>> strings) {
        checkCompletion(completion);
        autoCompletions.put(completion, strings.get());
        return this;
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestionIfAbsent(@NotNull String completion, @NotNull List<String> strings) {
        checkCompletion(completion);
        autoCompletions.putIfAbsent(completion, strings);
        return this;
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestionIfAbsent(@NotNull String completion, @NotNull String[] strings) {
        checkCompletion(completion);
        autoCompletions.putIfAbsent(completion, List.of(strings));
        return this;
    }

    @CanIgnoreReturnValue
    public @NotNull ArgumentSuggestionRegistry registerSuggestionIfAbsent(@NotNull String completion, @NotNull Supplier<List<String>> strings) {
        checkCompletion(completion);
        autoCompletions.putIfAbsent(completion, strings.get());
        return this;
    }

    /**
     * Completes arguments
     * @param commandClass the command class
     * @param function the int function for completion for certain arguments.
     * @return the list of completions
     */
    @ApiStatus.Experimental
    public final @NotNull @Unmodifiable List<String> complete(@NotNull Method method, SuggestionPredicate function) {
        List<String> list = new ArrayList<>(1000);
        int parameterCount = method.getParameterCount();
        for (int i = 0; i < parameterCount; i++) {
            Parameter parameter = method.getParameters()[i];
            Class<?> type = method.getParameterTypes()[i];
            if (function.test(type, list)) continue;
            int finalI = i;
            Optional.ofNullable(parameter.getAnnotation(Suggest.class)).ifPresent(suggestAnnotation -> {
                if (type == String.class) {
                    this.autoCompleteStrings(suggestAnnotation, parameter, list);
                } else {
                    var parser = parsers.get(type);
                    if (parser == null)
                        throw new IllegalArgumentException("Unknown autocompleted type: " + type);
                    list.add(String.valueOf(parser.apply(suggestAnnotation.value()[finalI])));
                }
            });
        }
        return list;
    }

    private void autoCompleteStrings(@NotNull Suggest suggestAnnotation, @NotNull Parameter parameter, List<String> list) {
        String[] autoCompletions = suggestAnnotation.value();
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
