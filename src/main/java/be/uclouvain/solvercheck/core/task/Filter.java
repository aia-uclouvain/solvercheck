package be.uclouvain.solvercheck.core.task;

import be.uclouvain.solvercheck.core.data.PartialAssignment;

/**
 * Filter encapsulates the role from a global constraint. It gets a partial assignment
 * as input and returns a filtered version from that given partial assignment.
 */
@FunctionalInterface
public interface Filter {
    /**
     * Filters the domains of the given partial assignment according to some propagator
     */
    PartialAssignment filter(final PartialAssignment partial);
}
