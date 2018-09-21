package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The base class of all Domains. It provides some facility wrt iterators and
 * spliterators.
 */
/* package */ abstract class AbstractDomain
        extends AbstractSet<Integer>
        implements Domain {

    /** {@inheritDoc} */
    @Override
    public Iterator<Integer> iterator() {
        return increasing();
    }

    /** {@inheritDoc} */
    @Override
    public Stream<Integer> increasingStream() {
        return StreamSupport.stream(from(this::decreasing), true);
    }

    /** {@inheritDoc} */
    @Override
    public Stream<Integer> decreasingStream() {
        return StreamSupport.stream(from(this::decreasing), true);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("{%s}",
                stream().map(Object::toString)
                        .collect(Collectors.joining(",")));
    }

    /**
     * @param supplier a function that yields the iterator when called w/o
     *                 arguments.
     * @return a spliterator for the given iterator supplier. assuming all
     * characteristics of an abstract domain
     */
    private Spliterator<Integer> from(final Supplier<Iterator<Integer>> supplier) {
        return Spliterators.spliterator(supplier.get(), size(), characteristics());
    }

    /**
     * @return the characteristics describing how spliterators can be used
     * by Stream
     */
    private int characteristics() {
        return Spliterator.ORDERED
                | Spliterator.DISTINCT
                | Spliterator.IMMUTABLE
                | Spliterator.SIZED
                | Spliterator.SUBSIZED;
    }
}
