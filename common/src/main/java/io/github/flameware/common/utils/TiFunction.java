package io.github.flameware.common.utils;

@FunctionalInterface
public interface TiFunction<T, U, E, R> {
    R apply(T first, U second, E third);

    default <V> TiFunction<T, U, E, V> and(TiFunction<? super T, ? super U, ? super E, ? extends V> after) {
        return after::apply;
    }
}
