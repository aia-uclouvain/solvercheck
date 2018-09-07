package be.uclouvain.solvercheck.core;

/**
 * Filter encapsulates the role of a global constraint. It gets a List of domains as input and
 * returns a list with the *same size* but potentially shrunk domains.
 * <p>
 * The slots of both lists represent the variables passed on to the propagator.
 */
@FunctionalInterface
public interface Filter {
    /**
     * filters the given domains according to some propagator
     */
    PartialAssignment filter(final PartialAssignment partial);
}
