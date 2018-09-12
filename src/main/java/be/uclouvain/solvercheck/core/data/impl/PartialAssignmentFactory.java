package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.*;
import java.util.stream.Collectors;

/** The point of this factory is to create partial assignments, potentially restricting some of its domains */
public final class PartialAssignmentFactory {

    /** An utility class has no public constructor */
    private PartialAssignmentFactory() {}

    /** Creates a new PartialAssignement from the given list of domains */
    public static PartialAssignment from(final List<Domain> domains) {
        return new BasicPartialAssignment(domains);
    }

    /**
     * This method returns a copy of the given `partial` assignment in which the domain of `variable`
     * has been restricted according to [`op`, `value`]. {@see DomainFactory.restrict}.
     *
     * @param partial the partial assignment for which one of the variables domain must be restricted
     * @param variable the variable whose domain must be restricted
     * @param op the operator defining the restriction relation to apply
     * @param value the value completing the definition of the restriction
     * @return a copy of the given `partial` assignment in which the domain of variable has been restricted
     */
    public static PartialAssignment restrict(
            final PartialAssignment partial,
            final int variable,
            final Operator op,
            final int value) {

        final Domain restricted = DomainFactory.restrict(partial.get(variable), op, value);
        if( restricted.equals(partial.get(variable)) ) {
            return partial;
        } else {
            final List<Domain> domains = new ArrayList<>(partial);
            domains.set(variable, restricted);
            return from(domains);
        }
    }


    /**
     * This method returns a Partial Assignment whose domains are built from the values appearing in
     * all the tuples.
     *
     * @return a partial assignment creating domains from the values appearing in the columns of all tuples
     */
    public static PartialAssignment unionOf(Collection<List<Integer>> tuples) {
        if( tuples.isEmpty())
            return from(List.of());

        int nbVars = tuples.stream().findAny().get().size();

        // initialize the container
        List<Set<Integer>> domains = new ArrayList<>();
        for(int i = 0; i < nbVars; i++) {
            domains.add(new HashSet<>());
        }

        // actually compute the unions
        for(List<Integer> tuple : tuples) {
            if( tuple.size() != nbVars)
                throw new IllegalArgumentException("Not all assignments have the same number of variables");

            for(int i = 0; i < tuple.size(); i++) {
                domains.get(i).add(tuple.get(i));
            }
        }

        // and then turn that all into a partial assignment
        return from(domains.stream().map(DomainFactory::fromCollection).collect(Collectors.toList()));
    }
}
