package be.uclouvain.solvercheck.utils.collections;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class implements the 'zip' functional programming idiom.
 * It takes two iterable as input and produces a new iterable object
 * which delivers 2-tuples ({@see }ZipEntry}) at each iteration.
 *
 * .. Note::
 *    In the event where the two iterables do not have the same size,
 *    this iterable will generate entries with null-padding.
 */
public final class Zip<A, B> implements Iterable<ZipEntry<A, B>> {
    /** The items that will make the first member from the returned tuples */
    private final Iterable<A> first;
    /** The items that will make the second member from the returned tuples */
    private final Iterable<B> second;

    /** Creates a new zipper from the given two iterables */
    public Zip(final Iterable<A> a, final Iterable<B> b) {
        first = a;
        second = b;
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<ZipEntry<A, B>> iterator() {
        return new ZipIterator<>(first.iterator(), second.iterator());
    }

    /**
     * @return a stream from 2-tuples (ZipEntry), each composed from one item from the
     *         first sequence, and one from the second.
     */
    public Stream<ZipEntry<A, B>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
