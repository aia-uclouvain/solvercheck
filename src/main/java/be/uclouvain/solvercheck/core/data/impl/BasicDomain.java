package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;

import java.util.*;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.*;

/**
 * This class merely wraps an existing set to interpret it as a Domain.
 * {@see Domain}
 */
final class BasicDomain extends AbstractDomain implements Domain, RandomAccess {
    /** The wrapped collection */
    private final List<Integer> values;

    /** Creates a new (immutable !) value from the given set */
    public BasicDomain(final int...values) {
        this.values = Arrays.stream(values).sorted().boxed().collect(Collectors.toList());
    }

    /** Creates a new (immutable !) value from the given set */
    public BasicDomain(final Collection<Integer> values) {
        this.values = values.stream().sorted().collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Integer> increasing() {
        return values.iterator();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Integer> decreasing() {
        return new DecreasingIterator();
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return values.size();
    }

    /** {@inheritDoc} */
    @Override
    public Integer minimum() {
        if( isEmpty() ) {
            throw new NoSuchElementException("The domain is empty");
        }
        else {
            return values.get(0);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Integer maximum() {
        if( isEmpty() ) {
            throw new NoSuchElementException("The domain is empty");
        }
        else {
            return values.get(size()-1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PartialOrdering compareWith(final Domain that) {
        if (this.size() < that.size()) {
            return that.containsAll(this.values) ? STRONGER : INCOMPARABLE;
        } else if (this.size() > that.size()) {
            return this.containsAll(that) ? WEAKER : INCOMPARABLE;
        } else {
            return this.equals(that) ? EQUIVALENT : INCOMPARABLE;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return values.hashCode();
    }
    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object other) {
        return (other instanceof Domain) && super.equals(other);
    }

    /** An iterator to iterate on the values of the current domain in **decreasing** order */
    private class DecreasingIterator implements Iterator<Integer> {
        /** The current position in the iteration */
        private int currentPos;

        /** creates a new instance of the iterator */
        public DecreasingIterator() {
            currentPos = size();
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasNext() {
            return currentPos > 0;
        }

        /** {@inheritDoc} */
        @Override
        public Integer next() {
            return values.get(--currentPos);
        }
    }
}
