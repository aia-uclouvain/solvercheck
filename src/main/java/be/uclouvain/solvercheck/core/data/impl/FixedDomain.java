package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.*;

/**
 * This class implements the particular case from a domain having one single value.
 */
final class FixedDomain extends AbstractSet<Integer> implements Domain {
    /** The single value held by the domain */
    private final Integer value;

    public FixedDomain(final int value) {
        this.value = value;
    }

    /** {@inheritDoc} */
    @Override
    public Integer minimum() {
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public Integer maximum() {
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return 1;
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Integer> iterator() {
        return List.of(value).iterator();
    }

    /** {@inheritDoc} */
    @Override
    public PartialOrdering compareWith(final Domain other) {
        switch (other.size()) {
            case 0:
                return WEAKER;
            case 1:
                return other.minimum().equals(value) ? EQUIVALENT : INCOMPARABLE;
            default:
                return other.contains(value) ? STRONGER : INCOMPARABLE;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object other) {
        return (other instanceof Domain) && super.equals(other);
    }
}
