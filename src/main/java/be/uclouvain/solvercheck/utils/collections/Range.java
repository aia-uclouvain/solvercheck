package be.uclouvain.solvercheck.utils.collections;

import java.util.AbstractSet;
import java.util.Iterator;

/** An inclusive range of value: [lower; upper] */
public final class Range extends AbstractSet<Integer> {
    /** The lower bound of the range */
    private final int from;
    /** The upper bound of the range */
    private final int to;
    /** The step size between two consecutive elements */
    private final int step;

    /**
     * Creates the given range of values spanning from `from` (inclusive)
     * until `to` (inclusive) stepping by one.
     *
     * .. Note::
     *    This factory method is a mere wrapper to the constructor. Its
     *    only purpose is to improve the readability of client code
     *
     * @param from the lower bound (incl.) of the set
     * @param to the upper bound (incl.) of the set
     */
    public static Range between(final int from, final int to) {
        return new Range(from, to);
    }

    /**
     * Creates the given range of values spanning from `from` (inclusive)
     * until `to` (inclusive) stepping by `step` items.
     *
     * .. Note::
     *    This factory method is a mere wrapper to the constructor. Its
     *    only purpose is to improve the readability of client code
     *
     * @param from the lower bound (incl.) of the set
     * @param to the upper bound (incl.) of the set
     * @param step the step size between any two elements of the set
     */
    public static Range between(final int from, final int to, final int step) {
        return new Range(from, to, step);
    }

    /**
     * Creates the given range of values spanning from `from` (inclusive)
     * until `to` (inclusive) stepping by one.
     *
     * @param from the lower bound (incl.) of the set
     * @param to the upper bound (incl.) of the set
     */
    private Range(final int from, final int to){
        this(from, to, 1);
    }

    /**
     * Creates the given range of values spanning from `from` (inclusive)
     * until `to` (inclusive) stepping by `step` items.
     *
     * @param from the lower bound (incl.) of the set
     * @param to the upper bound (incl.) of the set
     * @param step the step size between any two elements of the set
     */
    private Range(final int from, final int to, final int step){
        if(! isFeasible(from, to, step)){
            throw new IllegalArgumentException("The range ["+from+";"+to+":"+step+"] has an infinite size");
        }
        this.from = from;
        this.to   = to;
        this.step = step;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return 1+(to-from)/step;
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
     * Tells whether or not a range specified with the given parameters is closed.
     * That is to say, it has a finite size
     */
    private static boolean isFeasible(final int from, final int to, final int step) {
        return ( (from <= to && step > 0) || (from >= to && step < 0 ) );
    }

    /** An iterator that efficiently goes through all the values in the range */
    private class RangeIterator implements Iterator<Integer> {
        /** The current value of the iterator */
        private int current;

        /** Creates a new iterator to go over the current range */
        public RangeIterator() {
            this.current = from;
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasNext(){
            return step > 0 ? current <= to : current >= to;
        }

        /** {@inheritDoc} */
        @Override
        public Integer next(){
            int result = current;
            current += step;
            return result;
        }
    }
}
