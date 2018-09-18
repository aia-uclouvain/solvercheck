package be.uclouvain.solvercheck.assertions;

/** Interface of an assertion. */
@FunctionalInterface
public interface Assertion {
    /**
     * Tests whether the assertion is satisfied, throws an AssertionError
     * otherwise.
     */
    void check();
}
