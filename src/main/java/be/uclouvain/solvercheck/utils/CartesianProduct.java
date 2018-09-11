package be.uclouvain.solvercheck.utils;

import java.util.*;

/**
 * This class encapsulates the notion of cartesian product of sets of things. It basically
 * represents the set of all the lists composed with one item from each of the given sets
 * (in order).
 *
 * .. Complexity::
 *    Even though this class represents the complete cartesian product of the given sets,
 *    its space complexity is only $\theta(N)$ as all the computations are made lazily.
 *
 * .. Credits::
 *    This implementation of the computation of the cartesian product of several sets was
 *    inspired by Guava's implementation. It should however be +much+ simpler to read.
 *    Additionally, micro-benchmarking revealed that is runs more than an order of
 *    magnitude faster than Guava's implementation when iterating over the set of all
 *    possible lists.
 *
 * .. Further Improvements::
 *    Even though it showcases very decent performances, this class could be made even
 *    faster by simply avoiding the allocation/gc cost incurred by the creation of
 *    transient objects.
 *    Concretely, this means that we could gain some performance boost by letting the
 *    iterator always return the same object, only updating the 'index' value.
 *
 * @param <T> the type of objects composing the lists of the cartesian product.
 */
public class CartesianProduct<T> extends AbstractSet<List<T>> implements RandomAccess {
    /**
     * This is the actual data, the 'sets' from which we are going to pick values to build
     * the tuples composing the cartesian product.
     *
     * .. Note::
     *    The sets have been turned into lists because we rely on a predictable enumeration
     *    scheme to produce the tuples. In that context, it is useful to know exactly how
     *    the ith tuple is to be created.
     */
    private final ArrayList<T>[] data;
    /**
     * This array holds memoized multiplicative coefficients. These serve to know how to
     * parse a given number 'i' and produce the corresponding ith tuple.
     *
     * Each cell `coeff[j]` of the array holds the value of the product of the sizes
     * of the sets `data[j]` to `data[nbCol-1]`. This means that `coeff[0]` store the
     * result of the multiplication of the sizes of all the sets. That is to say,
     * `coeff[0]` stores the **size** of the cartesian product.
     */
    private final int[] coeff;
    /**
     * This field stores the number of columns of each tuple in the cartesian product
     */
    private final int nbCol;

    /**
     * Creates the cartesian product of all the given sets.
     * @param data the sets of which to compute the cartesian product
     */
    @SuppressWarnings("unchecked")
    public CartesianProduct(final List<Set<T>> data) {
        this.data  = data.stream().map(ArrayList::new).toArray(ArrayList[]::new);
        this.nbCol = this.data.length;
        this.coeff = new int[nbCol+1];

        // initialize the offsets
        this.coeff[nbCol]   = 1;
        this.coeff[nbCol-1] = this.data[nbCol-1].size();
        for(int i = nbCol-2; i >= 0; i--) {
            this.coeff[i] = checkedMul(this.data[i].size(), coeff[i+1]);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return this.coeff[0];
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<List<T>> iterator() {
        return new LineIter();
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * This method has the same meaning as in the context of a List.
     * It returns the `index`th tuple of the cartesian set, where "index-th" is to be
     * interpreted according to the internal order of the cartesian prod.
     *
     * .. The Internal Order::
     *    Internally, the tuples of the cartesian product are assumed to be ordered
     *    according to some 'generalized' counter. Which one is simply an integer that
     *    can be decomposed to find the indices of all the items in all the 'sets'.
     *    The correspondence "counter" to tuple is given by the following formula:
     *    $$
     *      \sum_{i=0}^{nbCol} value_i * coeff[i-1]
     *    $$
     *
     * @param index the index of the desired tuple in the internal ordering
     * @return the index-th tuple.
     */
    public List<T> get(int index) {
        return new Line(index);
    }

    /**
     * The index (according to the internal order) of the object `o` if that is a tuple
     * from the cartesian product. Or -1 if `o` is not a tuple from the cartesian product.
     * @param o the object whose index is searched for.
     * @return the index of the object 'o' if it belongs to the product. -1 otherwise
     */
    @SuppressWarnings("unchecked")
    public int indexOf(Object o) {
        if(! (o instanceof List)) {
            return -1;
        }

        List<T> target = (List<T>) o;
        if( target.size() != nbCol){
            return -1;
        }


        int idx = 0;
        for(int i = 0; i < nbCol; i++) {
            int colIdx = data[i].indexOf(target.get(i));
            if( colIdx == -1 ) {
                return -1;
            } else {
                idx += colIdx * coeff[i+1];
            }
        }

        return idx;
    }

    /**
     * Returns the offset of the value for the `column`th column as it has been encoded
     * into the given `index`.
     *
     * @param index an index identifying some tuple
     * @param column a column. belonging to the range [0..nbCol[
     * @return the position of th value of the ith column of the tuple identified by index
     *    in the original "column"th set.
     */
    private int posInColumn(int index, int column) {
        return (index % coeff[column]) / coeff[column+1] ;
    }

    /**
     * Multiplies the two integers x and y and throws an exception in case an overflow occurs.
     *
     * @param x some int value
     * @param y some int value
     * @return x * y iff these values can be multiplies without overflowing.
     */
    private int checkedMul(int x, int y) {
        long safe = (long) x * y;

        if( safe != (int) safe )
            throw new RuntimeException("CartesianProduct is larger than Integer.MAX_VALUE");

        return (int) safe;
    }

    /**
     * This class provides a list-view into the cartesian product. The represented list
     * constitutes one of the tuples of the product.
     */
    private class Line extends AbstractList<T> {
        /** An index identifying the tuple shown as a list */
        private final int index;

        /** Creates a list-view for the tuple identified by the given index */
        public Line(final int index) {
            this.index = index;
        }

        /** {@inheritDoc} */
        @Override
        public T get(int pos) {
            return data[pos].get(posInColumn(index, pos));
        }

        /** {@inheritDoc} */
        @Override
        public int size() {
            return nbCol;
        }
    }

    /**
     * This class provides an iterator to iterate over the lines (aka tuples) composing
     * this cartesian product.
     */
    private class LineIter implements Iterator<List<T>> {
        /** The index of the current tuple */
        private int cursor;

        /** {@inheritDoc} */
        @Override
        public boolean hasNext() {
            return cursor != size();
        }

        /** {@inheritDoc} */
        @Override
        public List<T> next() {
            return get(cursor++);
        }
    }
}
