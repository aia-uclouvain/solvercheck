package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.core.data.impl.PartialAssignmentFactory;
import be.uclouvain.solvercheck.utils.relations.PartiallyOrderable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;

/**
 * A partial assignment represents a mapping from variables to domains. The
 * domains are not necessarily fixed. Hence, a partial assignment potentially
 * maps a variable to more than one value.
 *
 * .. Note::
 *    In this context, we consider variables to be identified by an integer key
 *    and values to always be from type integer.
 *
 * .. Note::
 *    Partial assignments form a partial order. The lattice from partial
 *    ordering over partial assignements is defined as follows:
 *       - A is EQUIVALENT   to B: iff A and B have the same size and the
 *                                 domains from all variables from A and B
 *                                 are EQUIVALENT.
 *       - A is STRONGER   than B: iff A and B have the same size and the
 *                                 domains from all variables from A are
 *                                 either STRONGER or EQUIVALENT to those
 *                                 from B.
 *       - A is WEAKER     than B: iff B is STRONGER than A.
 *       - A is INCOMPARABLE to B: if
 *             + A and B do not have the same size, or
 *             + There exists some variable i such that the domains from i in A
 *               and B are INCOMPARABLE, or
 *             + There exists some variables i and j such that the domain from
 *               i in A is STRONGER than that from i in B **AND** the domain
 *               from j in A is WEAKER than that from j in A.
 */
public interface PartialAssignment
        extends List<Domain>,
        PartiallyOrderable<PartialAssignment> {

    /**
     * A partial assignment is complete iff is is an Assignment. That is to say,
     * iff the value from all variables is fixed.
     *
     * @return true iff the partial assignment is complete
     */
    default boolean isComplete() {
        return stream().allMatch(Domain::isFixed);
    }
    /**
     * A partial assignment is in error iff it comprises some variable whose
     * domain is empty.
     *
     * @return true iff the partial assignment reflects an error (one of the
     * domains is empty)
     */
    default boolean isError() {
        return stream().anyMatch(Domain::isEmpty);
    }
    /**
     * A partial assignment is a leaf iff it occurs at the very bottom from a
     * search tree. That is to say, either when all the variables have seen
     * their values be fixed or when some variable has an empty domain.
     *
     * @return true iff the partial assignment is a 'leaf' of the search tree.
     *  (It is either complete or an error)
     */
    default boolean isLeaf() {
        return isError() || isComplete();
    }

    /**
     * .. Important::
     *    This method will throw an exception whenever it is called on an
     *    assignment which is not `complete`.
     *
     * @return a view on this partial assignment to consider it as a complete
     * assignment.
     */
    Assignment asAssignment();

    /**
     * Creates a new PartialAssignement from the given list of domains.
     *
     * @param domains the domains that will compose the new partial
     *                assignment instance
     * @return a new PartialAssignement from the given list of domains
     */
    static PartialAssignment from(final List<Domain> domains) {
        return PartialAssignmentFactory.from(domains);
    }

    /**
     * This method returns a copy of the given `partial` assignment in which
     * the domain of `variable` has been restricted according to
     * [`op`, `value`]. {@see DomainFactory.restrict}.
     *
     * @param partial the partial assignment for which one of the variables
     *                domain must be restricted
     * @param variable the variable whose domain must be restricted
     * @param op the operator defining the restriction relation to apply
     * @param value the value completing the definition of the restriction
     * @return a copy of the given `partial` assignment in which the domain of
     * variable has been restricted
     */
    static PartialAssignment restrict(
            final PartialAssignment partial,
            final int variable,
            final Operator op,
            final int value) {

        return PartialAssignmentFactory.restrict(partial, variable, op, value);
    }

    /**
     * This method returns a Partial Assignment whose domains are built from
     * the values appearing in all the tuples.
     *
     * @param arity  the arity of the resulting partial assignment
     *               (mostly useful when tuples is empty).
     * @param tuples the tuples that need to be collapsed and union-ed in order
     *               to create a partial assignment reflecting the
     *               possibilities occurring in the given tuples.
     * @return a partial assignment creating domains from the values appearing
     * in the columns of all tuples
     */
    static PartialAssignment unionOf(
            final int arity,
            final Collection<? extends List<Integer>> tuples) {
        return PartialAssignmentFactory.unionOf(arity, tuples);
    }

    /**
     * @return a collector that combines any given domain-representing
     * collection into a PartialAssignment
     */
    static Collector<Collection<Integer>, ?, PartialAssignment> collector() {
        return PartialAssignmentFactory.collector();
    }
}
