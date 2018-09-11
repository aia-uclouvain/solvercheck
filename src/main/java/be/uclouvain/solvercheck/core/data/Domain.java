package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.utils.relations.PartiallyOrderable;

import java.util.Set;

/**
 * A domain is the set of candidate values for some variable. Given that we consider that all variables
 * take an integer value, the domain can be seen as a set of integers.
 *
 * .. Note::
 *    The domains form a _partial order_. One says that two domain A and B are:
 *       - A is EQUIVALENT   to B: iff $A \subseteq B$ and $B \subseteq A$.
 *       - A is STRONGER   than B: iff $A \subset   B$
 *       - A is WEAKER     than B: iff B is STRONGER than A.
 *       - A is INCOMPARABLE to B: iff $A \not\subseteq B$ and $B \not \subseteq A$
 */
public interface Domain extends Set<Integer>, PartiallyOrderable<Domain> {

    /** A domain is fixed iff it has only one value left */
    default boolean isFixed() { return size() == 1; }

}
