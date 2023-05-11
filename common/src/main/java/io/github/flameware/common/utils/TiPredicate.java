package io.github.flameware.common.utils;

@FunctionalInterface
public interface TiPredicate<T, A, E> {
    boolean test(T t, A a, E e);

    default TiPredicate<T, A, E> and(TiPredicate<T, A, E> other) {
        return (t, a, e) -> test(t, a, e) && other.test(t, a, e);
    }

    default TiPredicate<T, A, E> or(TiPredicate<T, A, E> other) {
        return (t, a, e) -> test(t, a, e) || other.test(t, a, e);
    }

    default TiPredicate<T, A, E> negate() {
        return (t, a, e) -> !test(t, a, e);
    }
}
