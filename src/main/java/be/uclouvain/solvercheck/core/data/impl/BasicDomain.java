package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.Set;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.*;

/**
 * This class merely wraps an existing set to interpret it as a Domain.
 * {@see Domain}
 */
public final class BasicDomain extends AbstractSet<Integer> implements Domain, RandomAccess {
    /** The wrapped collection */
    private final Set<Integer> values;

    /** Creates a new (immutable !) value from the given set */
    public BasicDomain(final Set<Integer> values) {
        this.values = Set.copyOf(values);
    }

    /*
    public BasicDomain remove(final Integer val) {
        if (!contains(val)) {
            return this;
        } else {
            return new BasicDomain(values.stream()
                    .filter(x -> !x.equals(val))
                    .collect(Collectors.toSet()));
        }
    }
    */

    /** {@inheritDoc} */
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
