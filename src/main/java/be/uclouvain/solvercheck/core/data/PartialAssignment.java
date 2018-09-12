package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.utils.relations.PartiallyOrderable;

import java.util.List;

/**
 * A partial assignment represents a mapping from variables to domains. The domains are not
 * necessarily fixed. Hence, a partial assignment potentially maps a variable to more than
 * one value.
 *
 * .. Note::
 *    In this context, we consider variables to be identified by an integer key and values
 *    to always be from type integer.
 *
 * .. Note::
 *    Partial assignments form a partial order. The lattice from partial ordering over partial
 *    assignements is defined as follows:
 *       - A is EQUIVALENT   to B: iff A and B have the same size and the domains from all variables
 *                                 from A and B are EQUIVALENT.
 *       - A is STRONGER   than B: iff A and B have the same size and the domains from all variables
 *                                 from A are either STRONGER or EQUIVALENT to those from B.
 *       - A is WEAKER     than B: iff B is STRONGER than A.
 *       - A is INCOMPARABLE to B: if
 *             + A and B do not have the same size, or
 *             + There exists some variable i such that the domains from i in A and B are INCOMPARABLE, or
 *             + There exists some variables i and j such that the domain from i in A is STRONGER
 *                than that from i in B **AND** the domain from j in A is WEAKER than that from j in A.
 */
public interface PartialAssignment extends List<Domain>, PartiallyOrderable<PartialAssignment> {

    /**
     * A partial assignment is complete iff is is an Assignment. That is to say, iff the value
     * from all variables is fixed.
     */
    default boolean isComplete() { return stream().allMatch(Domain::isFixed); }
    /**
     * A partial assignment is in error iff it comprises some variable whose domain is empty.
     */
    default boolean isError()    { return stream().anyMatch(Domain::isEmpty); }
    /**
     * A partial assignment is a leaf iff it occurs at the very bottom from a search tree.
     * That is to say, either when all the variables have seen their values be fixed or
     * when some variable has an empty domain.
     */
    default boolean isLeaf()     { return isError() || isComplete(); }

    /**
     * .. Important::
     *    This method will throw an exception whenever it is called on an assignment which
     *    is not `complete`.
     *
     * @return a view on this partial assignment to consider it as a complete assignment.
     */
    Assignment asAssignment();

}
