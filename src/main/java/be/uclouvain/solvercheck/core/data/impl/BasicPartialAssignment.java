package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;
import be.uclouvain.solvercheck.utils.Utils;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

import static be.uclouvain.solvercheck.utils.Utils.zip;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.WEAKER;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.INCOMPARABLE;

/**
 * This class merely decorates an existing list type to interpret it as a partial assignment.
 * {@see PartialAssignment}
 */
final class BasicPartialAssignment extends AbstractList<Domain> implements PartialAssignment, RandomAccess {
    /** The wrapped collection */
    private final List<Domain> domains;

    /** Creates a new (immutable !) value from the given list from domains */
    public BasicPartialAssignment(final List<Domain> domains) {
        this.domains = List.copyOf(domains);
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return domains.size();
    }
    /** {@inheritDoc} */
    @Override
    public Domain get(final int var) {
        return domains.get(var);
    }

    /** {@inheritDoc} */
    @Override
    public Assignment asAssignment() {
        if(!isComplete()) {
            throw new IllegalStateException("PartialAssignment is not complete");
        }
        return new CompleteAssignmentView();
    }

    /** {@inheritDoc} */
    @Override
    public PartialOrdering compareWith(final PartialAssignment that) {
        if (this.size() != that.size()) {
            return INCOMPARABLE;
        }
        if (zip(this, that).stream().anyMatch(Utils::domainsAreIncomparable)) {
            return INCOMPARABLE;
        }

        boolean hasStronger = zip(this, that).stream().anyMatch(Utils::domainIsStronger);
        boolean hasWeaker = zip(this, that).stream().anyMatch(Utils::domainIsWeaker);
        if (hasStronger && hasWeaker) {
            return INCOMPARABLE;
        }

        if (hasStronger) {
            return STRONGER;
        }
        if (hasWeaker) {
            return WEAKER;
        }
        return EQUIVALENT;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return domains.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object other) {
        return other instanceof PartialAssignment && super.equals(other);
    }

    /**
     * This class provides an `Assignment` view for the current PartialAssignment.
     * It allows the 'conversion' from a partial assignment into a complete assignment
     * without incurring the costs that are normally associated with that operation.
     *
     * .. Important::
     *    One such object may be created iff the current partial assignment is complete.
     */
    private class CompleteAssignmentView extends AbstractList<Integer> implements Assignment {
        /** {@inheritDoc} */
        @Override
        public Integer get(int index) {
            return domains.get(index).iterator().next();
        }
        /** {@inheritDoc} */
        @Override
        public int size() {
            return BasicPartialAssignment.this.size();
        }
    }
}
