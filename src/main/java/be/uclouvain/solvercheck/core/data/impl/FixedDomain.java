package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;

import java.util.Iterator;
import java.util.List;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.WEAKER;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.INCOMPARABLE;

/**
 * This class implements the particular case from a domain having one single
 * value.
 */
final class FixedDomain extends AbstractDomain {
    /** The single value held by the domain. */
    private final Integer value;

    /**
     * Creates a new singleton domain for the given value.
     *
     * @param value the single value wrapped in the new domain
     */
    FixedDomain(final int value) {
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
    public Iterator<Integer> increasing() {
        return List.of(value).iterator();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Integer> decreasing() {
        return List.of(value).iterator();
    }

    /** {@inheritDoc} */
    @Override
    public PartialOrdering compareWith(final Domain other) {
        switch (other.size()) {
            case 0:
                return WEAKER;
            case 1:
                if (other.minimum().equals(value)) {
                    return EQUIVALENT;
                } else {
                    return INCOMPARABLE;
                }
            default:
                if (other.contains(value)) {
                    return STRONGER;
                } else {
                    return INCOMPARABLE;
                }
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
