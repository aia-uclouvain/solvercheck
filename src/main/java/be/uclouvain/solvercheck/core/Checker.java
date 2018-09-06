package be.uclouvain.solvercheck.core;

import java.util.function.Predicate;

/**
 * Interface embodies the simplest form of correctness validation.
 * A checker simply inspects some given valuation and tells whether or not it
 * constitutes a valid solution (according to some propagator and consistency)
 */
@FunctionalInterface
public interface Checker extends Predicate<PartialAssignment> {
    @Override
    boolean test(final PartialAssignment valuation);
}
