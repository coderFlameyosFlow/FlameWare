package io.github.flameware.common.utils;

@FunctionalInterface
public interface TiFunction<T, U, E, R> {
    R apply(T first, U second, E third);
}
