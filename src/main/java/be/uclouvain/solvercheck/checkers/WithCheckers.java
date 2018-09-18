package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.task.Checker;

import java.util.List;

/**
 * This interface collects all the useful methods that let you plug
 * SolverCheck's checkers into your code as an internal DSL.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithCheckers {

    /** @return a fake checker evaluates any given assignment to true. */
    default Checker alwaysTrue() {
        return Checkers.alwaysTrue();
    }
    /** @return a fake checker evaluates any given assignment to false. */
    default Checker alwaysFalse() {
        return Checkers.alwaysTrue();
    }
    /**
     * A checker that tells whether all variables of the given assignment
     * satisfy the allDifferent constraint. In other words, this checker
     * evaluates an assignment to true iff all the variables of the
     * assignment take a different value.
     *
     * @return a checker validating if a solution satisfies the alldiff
     *         constraint
     */
    default Checker allDiff() {
        return Checkers.allDiff();
    }
    /**
     * A checker that tells whether the sum of the values of all variables of
     * the given assignment satisfy some given linear condition.
     *
     * .. Example::
     *    sum(LT, 3) evaluates an assignment to true iff the sum of all the
     *    values of the variables of the assignment add up to less than three.
     *
     * .. More formally::
     *    This checker evaluates true iff the assignment satisfies the given
     *    expression:
     *    $$
     *    \sum_{i = 0}^{|assignment|} assignment[i] \otimes constant
     *    $$
     *    where
     *    $\otimes$ is one of the following operators: $<, \le, =, \ne, \ge, >$
     *
     * @param operator the operator determining the condition that applies to
     *                 the total of the sum
     * @param value the constant that imposes some value constraint
     *              on the sum
     * @return checker that tells whether the sum of the values of all variables
     *      of the given assignment satisfy some given linear condition.
     */
    default Checker sum(final Operator operator, final int value) {
        return Checkers.sum(operator, value);
    }
    /**
     * This checker assumes the following structure:
     *  - variables in [0;size-3] constitute the array from plain variables
     *  - variable at size-2 is the 'index  variable
     *  - varaible at size-1 is the 'value'
     *
     *  Such that the element is satisfied iff array[index] == value.
     *
     * @return a checker verifying that the element constraint is satisfied by
     *         the given assignment.
     */
    default Checker element() {
        return Checkers.element();
    }
    /**
     * A checker that returns true iff the given assignment belongs to the given
     * table of possible assignments (This is the typical case of an
     * extensional constraint).
     *
     * @param table the table comprising all possible solutions.
     * @return true iff the given assignment belongs to the given table of
     *         assignments
     */
    default Checker table(final List<Assignment> table) {
        return Checkers.table(table);
    }
    /**
     * This checkers verifies the Global Cardinality Constraint (gcc)
     * considering that:
     * $$
     * \forall i \in 0..values.length :
     * |\left\{ v \in variables | v.val = values[i] \right\}| = cardinalities[i]
     * $$
     *
     * In this setup, the cardinalities are considered a given.
     *
     * @param cardinalities the list of cardinality for each value. (The number
     *                      of time each value[i] must occur in the evaluated
     *                      assignment.
     * @param values the list of values that must occur with certain
     *               cardinalities in the assignment.
     * @return a checker validating that some assignment verifies the gcc
     * constraint.
     */
    default Checker gcc(final List<Integer> cardinalities,
                        final List<Integer> values) {
        return Checkers.gcc(cardinalities, values);
    }
    /**
     * This checkers verifies the Global Cardinality Constraint (gcc)
     * considering that:
     *
     * $$
     * \forall i \in 0..values.length :
     * |\left\{ v \in variables | v.val = values[i] \right\}| =
     * cardinalities[i].val
     * $$
     *
     * In this setup, the cardinalities are assumed to be the last N variables
     * of the assignment.
     *
     * @param values the list of values that must occur with certain
     *               cardinalities in the assignment.
     * @return a checker validating that some assignment verifies the gcc
     *         constraint.
     */
    default Checker gccVar(final List<Integer> values) {
        return Checkers.gccVar(values);
    }
}
