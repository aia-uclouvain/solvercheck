package be.uclouvain.solvercheck.utils.collections;

import java.util.Iterator;

/**
 * This class implements an iterator for the the 'zip' functional programming
 * idiom. It takes two iterable as input and produces a new iterable object
 * which delivers 2-tuples ({@see }ZipEntry}) at each iteration.
 *
 * .. Note::
 *    In the event where the two iterables do not have the same size,
 *    this iterable will generate entries with null-padding.
 */
public final class ZipIterator<A, B> implements Iterator<ZipEntry<A, B>> {
    /** The 1st element */
    private final Iterator<A> first;
    /** The 2nd element */
    private final Iterator<B> second;

    public ZipIterator(final Iterator<A> a, final Iterator<B> b) {
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
        A nextA = first.hasNext() ? first.next() : null;
        B nextB = second.hasNext() ? second.next() : null;

        return new ZipEntry<>(nextA, nextB);
    }
}
