package be.uclouvain.solvercheck.utils;

import java.util.function.BiFunction;

public final class ZipEntry<A, B> {
    public final A first;
    public final B second;

    public ZipEntry(final A a, final B b) {
        first = a;
        second = b;
    }

    public <R> R apply(final BiFunction<A, B, R> func) {
        return func.apply(first, second);
    }
}
