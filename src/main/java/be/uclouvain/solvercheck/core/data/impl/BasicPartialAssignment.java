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
 * This class merely decorates an existing list type to interpret it as a
 * partial assignment {@see PartialAssignment}.
 */
/* package */ final class BasicPartialAssignment
        extends AbstractList<Domain>
        implements PartialAssignment, RandomAccess {

    /** The wrapped collection. */
    private final List<Domain> domains;

    /** Creates a new (immutable !) partial assignment from the given list of
     * domains.
     *
     * @param domains the domains of all the variables represented in this
     *                partial assignment. (ith item in the list corresponds to
     *                the domain of variable x_i)
     */
    /* package */ BasicPartialAssignment(final List<Domain> domains) {
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
        if (!isComplete()) {
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
        if (haveSomeIncomparableDomain(that)) {
            return INCOMPARABLE;
        }

        boolean hasStronger = hasSomeDomainStrongerThan(that);
        boolean hasWeaker = hasSomeDomainWeakerThan(that);
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

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size(); i++) {
            sb.append("x").append(i).append("=").append(get(i));

            if (i < size() - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    /**
     * @param that an other partial assignment
     * @return true iff this and that have domains which are incomparable
     */
    private boolean haveSomeIncomparableDomain(final PartialAssignment that) {
        return zip(this, that).stream()
                .anyMatch(Utils::domainsAreIncomparable);
    }

    /**
     * @param that an other partial assignment
     * @return true iff there is at least one domain which is stronger in
     * `this` than in `that`.
     */
    private boolean hasSomeDomainStrongerThan(final PartialAssignment that) {
        return zip(this, that).stream()
                .anyMatch(Utils::domainIsStronger);
    }

    /**
     * @param that an other partial assignment
     * @return true iff there is at least one domain which is weaker in
     * `this` than in `that`.
     */
    private boolean hasSomeDomainWeakerThan(final PartialAssignment that) {
        return zip(this, that).stream()
                .anyMatch(Utils::domainIsWeaker);
    }

    /**
     * This class provides an `Assignment` view for the current
     * PartialAssignment. It allows the 'conversion' from a partial
     * assignment into a complete assignment without incurring the costs that
     * are normally associated with that operation.
     *
     * .. Important::
     *    One such object may be created iff the current partial assignment
     *    is complete.
     */
    private class CompleteAssignmentView
            extends    AbstractList<Integer>
            implements Assignment {

        /** {@inheritDoc} */
        @Override
        public Integer get(final int index) {
            return domains.get(index).iterator().next();
        }
        /** {@inheritDoc} */
        @Override
        public int size() {
            return BasicPartialAssignment.this.size();
        }
    }
}
