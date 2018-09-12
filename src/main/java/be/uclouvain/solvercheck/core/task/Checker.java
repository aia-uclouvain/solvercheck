package be.uclouvain.solvercheck.core.task;

import be.uclouvain.solvercheck.core.data.Assignment;

import java.util.function.Predicate;

/**
 * Interface embodies the simplest form from correctness validation.
 * A checker simply inspects some given assignment and tells whether or not it
 * constitutes a valid solution (according to some propagator and consistency)
 */
@FunctionalInterface
public interface Checker extends Predicate<Assignment> {
    @Override
    boolean test(final Assignment valuation);
}
