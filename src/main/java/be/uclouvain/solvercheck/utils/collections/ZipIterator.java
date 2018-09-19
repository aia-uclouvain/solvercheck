package be.uclouvain.solvercheck.utils.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class implements an iterator for the the 'zip' functional programming
 * idiom. It takes two iterable as input and produces a new iterable object
 * which delivers 2-tuples ({@see }ZipEntry}) at each iteration.
 *
 * .. Note::
 *    In the event where the two iterables do not have the same size,
 *    this iterable will generate entries with null-padding.
 *
 * @param <A> the type of the elements held in the first component of the
 *           2-tuples iterated upon.
 * @param <B> the type of the elements held in the second component of the
 *           2-tuples iterated upon.
 */
public final class ZipIterator<A, B> implements Iterator<ZipEntry<A, B>> {
    /** The 1st component of the 2-tuples. */
    private final Iterator<A> first;
    /** The 2nd component of the 2-tuples. */
    private final Iterator<B> second;

    /**
     * Creates a new iterator to iterate over 2-tuples ({@see ZipEntry}) made
     * of one element of each `a` and `b`.
     *
     * @param a the sequence of items making up the first component of all
     *          zip entries
     * @param b the sequence of items making up the second component of all
     *          zip entries
     */
    ZipIterator(final Iterator<A> a, final Iterator<B> b) {
        first = a;
        second = b;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return first.hasNext() || second.hasNext();
    }

    /** {@inheritDoc} */
    @Override
    public ZipEntry<A, B> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        A nextA = getNextOrDefaultNull(first);
        B nextB = getNextOrDefaultNull(second);

        return new ZipEntry<>(nextA, nextB);
    }

    /**
     * Returns the next item from the given iterator or null when `iterator`
     * is out of next elements.
     *
     * @param iterator an iterator whose next element is desired.
     * @param <T> the type of the element to return.
     * @return the next element of `iterator` or null if the end of the
     * sequence has been reached.
     */
    private static <T> T getNextOrDefaultNull(final Iterator<T> iterator) {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }
}
