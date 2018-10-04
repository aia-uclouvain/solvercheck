package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;

/**
 * The point of this factory is to create partial assignments, potentially
 * restricting some of its domains.
 */
public final class PartialAssignmentFactory {

    /** An utility class has no public constructor. */
    private PartialAssignmentFactory() { }

    /**
     * Creates a new PartialAssignement from the given list of domains.
     *
     * @param domains the domains that will compose the new partial
     *                assignment instance
     * @return a new PartialAssignement from the given list of domains
     */
    public static PartialAssignment from(final List<Domain> domains) {
        return new BasicPartialAssignment(domains);
    }

    /**
     * Creates a new PartialAssignement from the given list of domains.
     *
     * @param domains the domains that will compose the new partial
     *                assignment instance
     * @return a new PartialAssignement from the given list of domains
     */
    public static PartialAssignment from(final Domain...domains) {
        return new BasicPartialAssignment(Arrays.asList(domains));
    }

    /**
     * This method returns a copy of the given `partial` assignment in which the
     * domain of `variable` has been restricted according to [`op`, `value`].
     * (see DomainFactory.restrict).
     *
     * @param partial the partial assignment for which one of the variables
     *                domain must be restricted
     * @param var the variable whose domain must be restricted
     * @param op the operator defining the restriction relation to apply
     * @param val the value completing the definition of the restriction
     * @return a copy of the given `partial` assignment in which the domain of
     * variable has been restricted
     */
    public static PartialAssignment restrict(
            final PartialAssignment partial,
            final int var,
            final Operator op,
            final int val) {

        final Domain restricted = Domain.restrict(partial.get(var), op, val);
        if (restricted.equals(partial.get(var))) {
            return partial;
        } else {
            final List<Domain> domains = new ArrayList<>(partial);
            domains.set(var, restricted);
            return from(domains);
        }
    }


    /**
     * This method returns a Partial Assignment whose domains are built from
     * the values appearing in all the tuples.
     *
     * @param arity the expected arity of the resulting partial assignment.
     * @param tuples the tuples that need to be collapsed and union-ed in order
     *               to create a partial assignment reflecting the
     *               possibilities occurring in the given tuples.
     * @return a partial assignment creating domains from the values appearing
     * in the columns of all tuples
     */
    public static PartialAssignment unionOf(
            final int arity,
            final Collection<? extends List<Integer>> tuples) {

        if (arity < 0) {
            throw new IllegalArgumentException("Arity must be natural");
        }

        if (tuples.isEmpty()) {
            ArrayList<Domain> resulting = new ArrayList<>();
            for (int i = 0; i < arity; i++) {
                resulting.add(Domain.emptyDomain());
            }
            return from(resulting);
        }

        // initialize the container
        List<Set<Integer>> domains = new ArrayList<>();
        for (int i = 0; i < arity; i++) {
            domains.add(new HashSet<>());
        }

        // actually compute the unions
        for (List<Integer> tuple : tuples) {
            if (tuple.size() != arity) {
                throw new IllegalArgumentException(
                    "Not all assignments have the same number of variables");
            }

            for (int i = 0; i < tuple.size(); i++) {
                domains.get(i).add(tuple.get(i));
            }
        }

        // and then turn that all into a partial assignment
        return domains.stream().collect(collector());
    }

    /**
     * Returns a partial assignment with the specified arity whose domains
     * are all empty. This "error" partial assignment is meant to be used as
     * a marker to tell that a contradiction has been detected.
     *
     * @param arity the desired arity for the partial assignment.
     * @return a partial assignment with the specified arity whose domains
     * are all empty.
     */
    public static PartialAssignment error(final int arity) {
        return unionOf(arity, List.of());
    }

    /**
     * @return a collector that combines any given domain-representing
     * collection into a PartialAssignment
     */
    @SuppressWarnings("checkstyle:leftcurly")
    public static Collector<Collection<Integer>, ?, PartialAssignment> collector() {
        return Collector.of(
                ()          -> new ArrayList<Domain>(),
                (acc, item) -> acc.add(Domain.from(item)),
                (a1,  a2)   -> { a1.addAll(a2); return a1; },
                (set)       -> from(set)
        );
    }
}
