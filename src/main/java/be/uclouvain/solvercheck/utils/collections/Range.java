package be.uclouvain.solvercheck.utils.collections;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An inclusive range of value: [lower; upper[.
 *
 * <div>
 *     <h3>Note</h3>
 *     Even though this implementation assumes that a range is a set of
 *     integers, it is encoded using <b>long</b>. This is because we do want to
 *     be able to represent all ranges from Integer.MIN_VALUE to
 *     Integer.MAX_VALUE. This range can obviously not be represented using an
 *     int encoded range as its size would be bounded by Integer.MAX_VALUE.
 * </div>
 */
public final class Range extends AbstractSet<Integer> {
    /** The lower bound (included) of the range. */
    private final long from;
    /** The upper bound (excluded) of the range. */
    private final long to;

    /**
     * Creates the given range of values spanning from `from` (inclusive)
     * until `to` (exclusive) stepping by one.
     *
     * .. Note::
     *    This factory method is a mere wrapper to the constructor. Its
     *    only purpose is to improve the readability of client code
     *
     * @param from the lower bound (incl.) of the set
     * @param to the upper bound (excl.) of the set
     * @return a new range of values representing the interval [from; to[
     */
    public static Range between(final long from, final long to) {
        return new Range(from, to);
    }

    /**
     * Creates the given range of values spanning from `from` (inclusive)
     * until `to` (exclusive) stepping by one.
     *
     * @param from the lower bound (incl.) of the set
     * @param to the upper bound (excl.) of the set
     */
    private Range(final long from, final long to) {
        if (to < from) {
            throw new IllegalArgumentException(
               "Impossible to create the range [" + from + ";" + to + "["
             + "It would have a negative size.");
        }
        this.from = from;
        this.to   = to;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return (int) (to - from);
    }

    /**
     * {@inheritDoc}
     *
     * .. Note::
     *    The order in which the items are enumerated is guaranteed to
     *    follow the natural order of the integer. With items separated
     *    by `step` elements.
     */
    @Override
    public Iterator<Integer> iterator() {
        return new RangeIterator();
    }

    /**
     * An iterator that efficiently goes through all the values in the range.
     */
    private class RangeIterator implements Iterator<Integer> {
        /** The current value of the iterator. */
        private long current;

        /** Creates a new iterator to go over the current range. */
        RangeIterator() {
            this.current = from;
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasNext() {
            return current < to;
        }

        /** {@inheritDoc} */
        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return (int) current++;
        }
    }
}
