package be.uclouvain.solvercheck.utils;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Zip<A, B> implements Iterable<ZipEntry<A, B>> {
    private final Iterable<A> first;
    private final Iterable<B> second;

    public Zip(final Iterable<A> a, final Iterable<B> b) {
        first = a;
        second= b;
    }

    @Override
    public Iterator<ZipEntry<A, B>> iterator() {
        return new ZipIterator<>(first.iterator(), second.iterator());
    }

    public Stream<ZipEntry<A, B>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
