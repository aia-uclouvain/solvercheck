package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.pbt.Randomness;

/** Interface of an assertion. */
@FunctionalInterface
public interface Assertion {
    /**
     * Tests whether the assertion is satisfied, throws an AssertionError
     * otherwise.
     */
    void check(final Randomness randomness);
}
