package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.ArrayList;
import java.util.List;

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
}
