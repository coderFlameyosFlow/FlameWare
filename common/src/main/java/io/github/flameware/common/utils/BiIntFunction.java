package io.github.flameware.common.utils;

@FunctionalInterface
public interface BiIntFunction<E, A> {
    int apply(E e, A a);

    default BiIntFunction<E, A> and(BiIntFunction<E, A> after) {
        return (e, a) -> after.apply(e, a) & this.apply(e, a);
    }

    default BiIntFunction<E, A> or(BiIntFunction<E, A> before) {
        return (e, a) -> before.apply(e, a) | this.apply(e, a);
    }
}
