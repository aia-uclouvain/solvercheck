package be.uclouvain.solvercheck.utils.collections;

import java.util.function.BiFunction;

/** A 2-tuple returned while iterating over some Zip */
public final class ZipEntry<A, B> {
    /** The 1st element */
    public final A first;
    /** The 2nd element */
    public final B second;

    public ZipEntry(final A a, final B b) {
        first = a;
        second = b;
    }

    /** Executes the given binary function on the current tuple and returns its result */
    public <R> R apply(final BiFunction<A, B, R> func) {
        return func.apply(first, second);
    }
}
