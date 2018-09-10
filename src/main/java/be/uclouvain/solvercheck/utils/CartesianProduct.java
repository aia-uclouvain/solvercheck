package be.uclouvain.solvercheck.utils;

import java.util.*;

public class CartesianProduct<T> extends AbstractList<List<T>> implements RandomAccess {

    private final List<T>[] data;
    private final int[]     coeff;
    private final int       nbCol;

    @SuppressWarnings("unchecked")
    public CartesianProduct(final List<Set<T>> data) {
        this.data  = data.stream().map(ArrayList::new).toArray(List[]::new);
        this.nbCol = this.data.length;
        this.coeff = new int[nbCol+1];

        // initialize the offsets
        this.coeff[nbCol]   = 1;
        this.coeff[nbCol-1] = this.data[nbCol-1].size();
        for(int i = nbCol-2; i >= 0; i--) {
            this.coeff[i] = checkedMul(this.data[i].size(), coeff[i+1]);
        }
    }

    @Override
    public int size() {
        return this.coeff[0];
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public List<T> get(int index) {
        return new Line(index);
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new LineIter();
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
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

    private int posInColumn(int index, int column) {
        return (index % coeff[column]) / coeff[column+1] ;
    }

    private int checkedMul(int x, int y) {
        long safe = (long) x * y;

        if( safe != (int) safe )
            throw new RuntimeException("CartesianProduct is larger than Integer.MAX_VALUE");

        return (int) safe;
    }

    private class Line extends AbstractList<T> {
        private final int index;

        public Line(final int index) {
            this.index = index;
        }

        @Override
        public T get(int pos) {
            return data[pos].get(posInColumn(index, pos));
        }

        @Override
        public int size() {
            return nbCol;
        }
    }

    private class LineIter implements Iterator<List<T>> {
        private int cursor;

        @Override
        public boolean hasNext() {
            return cursor != size();
        }

        @Override
        public List<T> next() {
            return get(cursor++);
        }
    }
}
