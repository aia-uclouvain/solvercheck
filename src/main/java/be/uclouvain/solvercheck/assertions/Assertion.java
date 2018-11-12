package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.pbt.Randomness;

/** Interface of an assertion. */
@FunctionalInterface
public interface Assertion {
    /**
     * Tests whether the assertion is satisfied, throws an AssertionError
     * otherwise.
     *
     * @param randomness the source of randomness used for the fuzzing.
     */
    void check(Randomness randomness);
}
