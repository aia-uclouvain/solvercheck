package be.uclouvain.solvercheck.utils.collections;

import java.util.function.BiFunction;

/**
 * A 2-tuple returned while iterating over some Zip.
 *
 * @param <A> the type of the elements held in the first component of the
 *           2-tuple.
 * @param <B> the type of the elements held in the second component of the
 *           2-tuple.
 */
public final class ZipEntry<A, B> {
    /** The 1st component of this 2-tuple. */
    private final A first;
    /** The 2nd component of this 2-tuple. */
    private final B second;

    /**
     * Creates a new 2-tuple with the given `a` and `b` components.
     *
     * @param a the first component of the 2-tuple.
     * @param b the second component of the 2-tuple.
     */
    ZipEntry(final A a, final B b) {
        first = a;
        second = b;
    }

    /** @return the first component of this 2-tuple */
    public A first() {
        return first;
    }

    /** @return the second component of this 2-tuple */
    public B second() {
        return second;
    }

    /**
     * Executes the given binary function on the current tuple and returns
     * its result.
     *
     * @param func a bi argument function to be called with the 2-tuple
     *             components as arguments.
     * @param <R> the return type of the applied bi-function.
     * @return the result of calling `func` on the 2 components of this
     * ZipEntry.
     */
    public <R> R apply(final BiFunction<A, B, R> func) {
        return func.apply(first, second);
    }
}
