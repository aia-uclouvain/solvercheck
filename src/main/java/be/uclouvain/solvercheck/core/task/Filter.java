package be.uclouvain.solvercheck.core.task;

import be.uclouvain.solvercheck.core.data.PartialAssignment;

/**
 * Filter encapsulates the role of a global constraint. It gets a partial assignment
 * as input and returns a filtered version of that given partial assignment.
 */
@FunctionalInterface
public interface Filter {
    /**
     * Filters the given domains according to some propagator
     */
    PartialAssignment filter(final PartialAssignment partial);
}
