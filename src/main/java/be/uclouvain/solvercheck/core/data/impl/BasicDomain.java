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
final class BasicDomain extends AbstractSet<Integer> implements Domain, RandomAccess {
    /** The wrapped collection */
    private final List<Integer> values;

    /** Creates a new (immutable !) value from the given set */
    public BasicDomain(final int...values) {
        this.values = Arrays.stream(values).sorted().boxed().collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * .. Note::
     *    This iterator guarantees to iterate over the elements of the set
     *    in *increasing* order.
     */
    @Override
    public Iterator<Integer> iterator() {
        return values.iterator();
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
}
