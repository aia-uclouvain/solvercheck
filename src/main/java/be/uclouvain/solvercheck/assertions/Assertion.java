package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.fuzzing.Randomness;

/** Interface of an assertion. */
@FunctionalInterface
public interface Assertion {
    /**
     * Verifies the property for one single partial assignment.
     *
     * @param rnd the source of randomness (for the search)
     * @throws AssertionError whenever the property is violated
     */
    void check(Randomness rnd);
}
