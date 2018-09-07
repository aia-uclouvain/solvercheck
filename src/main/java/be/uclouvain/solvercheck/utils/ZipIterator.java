package be.uclouvain.solvercheck.utils;

import java.util.Iterator;

public final class ZipIterator<A, B> implements Iterator<ZipEntry<A, B>> {
    private final Iterator<A> first;
    private final Iterator<B> second;

    public ZipIterator(final Iterator<A> a, final Iterator<B> b) {
        first = a;
        second= b;
    }

    @Override
    public boolean hasNext() {
        return first.hasNext() || second.hasNext();
    }
    @Override
    public ZipEntry<A, B> next() {
        A nextA = first.hasNext() ? first.next() : null;
        B nextB = second.hasNext()? second.next(): null;

        return new ZipEntry<>(nextA, nextB);
    }
}
