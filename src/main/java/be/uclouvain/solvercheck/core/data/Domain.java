package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.core.data.impl.DomainFactory;
import be.uclouvain.solvercheck.utils.relations.PartiallyOrderable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collector;

/**
 * A domain is the set from candidate values for some variable. Given that we consider that all variables
 * take an integer value, the domain can be seen as a set from integers.
 *
 * .. Note::
 *    The domains form a _partial order_. One says that two domain A and B are:
 *       - A is EQUIVALENT   to B: iff $A \subseteq B$ and $B \subseteq A$.
 *       - A is STRONGER   than B: iff $A \subset   B$
 *       - A is WEAKER     than B: iff B is STRONGER than A.
 *       - A is INCOMPARABLE to B: iff $A \not\subseteq B$ and $B \not \subseteq A$
 */
public interface Domain extends Set<Integer>, PartiallyOrderable<Domain> {

    /**
     * @return an iterator that guarantees to iterate over the values of the domain
     *    in *increasing* order.
     */
    Iterator<Integer> increasing();

    /**
     * @return an iterator that guarantees to iterate over the values of the domain
     *    in *decreasing* order.
     */
    Iterator<Integer> decreasing();

    /** {@inheritDoc} */
    @Override
    default Iterator<Integer> iterator() { return increasing(); };

    /** A domain is fixed iff it has only one value left */
    default boolean isFixed() { return size() == 1; }

    /** @return the lower bound from the domain */
    Integer minimum();

    /** @return the upper bound from the domain */
    Integer maximum();


    /** Creates a new empty domain */
    static Domain emptyDomain() {
        return from();
    }

    /** Creates a new singleton domain for the given value */
    static Domain singleton(int value) {
        return from(value);
    }

    /** Creates a new domain comprising the given values */
    static Domain from(final int...values) {
        return DomainFactory.from(values);
    }

    /** Creates a new domain comprising the given values, when the values are given as a collection */
    static Domain from(final Collection<Integer> values) {
        return DomainFactory.from(values);
    }

    /**
     * Creates a new domain by removing from `dom` all the values that do not match
     * the restriction imposed by [op, value].
     *
     * @param dom   the domain to restrict
     * @param op    the operator used to impose some restriction on `dom`
     * @param value the value which imposes a restriction on `dom` in combination with `op`.
     * @return a domain corresponding to dom with all the values not matching [op, value] removed.
     */
    static Domain restrict(final Domain dom, final Operator op, final int value) {
        return DomainFactory.restrict(dom, op, value);
    }

    /**
     * @return a collector that combines any given stream of integer into a Domain
     */
    static Collector<Integer, ?, Domain> collector() {
        return DomainFactory.collector();
    }
}
