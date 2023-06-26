package io.github.flameware.common.utils;

import java.util.List;

@FunctionalInterface
public interface SuggestionPredicate {
    boolean test(Class<?> clazz, List<String> list);
}
