package io.github.flameware.common.utils;

import io.github.flameware.common.sender.CommandActor;

@FunctionalInterface
public interface ParsingPredicate {
    boolean test(Class<?> clazz, String[] args, CommandActor<?> sender);
}
