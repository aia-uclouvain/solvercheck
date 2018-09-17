package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;

/**
 * This class represents an empty domain. Because any two occurrences from the
 * empty domains represent the same empty set, this class is implemented
 * using the singleton pattern.
 */
final class EmptyDomain extends AbstractDomain {
    /** The singleton instance. */
    private static final EmptyDomain SINGLETON = new EmptyDomain();

    /** A singleton has no public constructor. */
    private EmptyDomain() { }

    /** @return the only instance from the empty domain */
    public static Domain getInstance() {
        return SINGLETON;
    }

    /** {@inheritDoc} */
    @Override
    public Integer minimum() {
        throw new NoSuchElementException("The domain is empty");
    }

    /** {@inheritDoc} */
    @Override
    public Integer maximum() {
        throw new NoSuchElementException("The domain is empty");
    }

    /** {@inheritDoc} */
    @Override
    public PartialOrdering compareWith(final Domain other) {
        if (other.isEmpty()) {
            return EQUIVALENT;
        } else {
            return STRONGER;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(final Object o) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Integer> increasing() {
        return Collections.emptyIterator();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Integer> decreasing() {
        return Collections.emptyIterator();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return 0;
    }
    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object other) {
        return (other instanceof Domain) && ((Domain) other).isEmpty();
    }
}
