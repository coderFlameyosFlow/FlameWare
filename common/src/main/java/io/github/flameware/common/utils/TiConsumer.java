package io.github.flameware.common.utils;

@FunctionalInterface
public interface TiConsumer<E, A, O> {
    void accept(E e, A a, O o);

    default TiConsumer<E, A, O> and(TiConsumer<E, A, O> after) {
        return after;
    }
}
