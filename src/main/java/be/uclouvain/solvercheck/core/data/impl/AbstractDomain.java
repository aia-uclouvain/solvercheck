package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;

import java.util.AbstractSet;
import java.util.Iterator;

/**
 * The base class of all Domains.
 * Its purpose is really just to avoid repeating the implementation of the `iterator` method
 */
abstract class AbstractDomain extends AbstractSet<Integer> implements Domain {
    /** {@inheritDoc} */
    @Override
    public Iterator<Integer> iterator() {
        return increasing();
    }
}
