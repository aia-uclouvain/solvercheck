package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.task.Checker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;

/**
 * This class is meant to be used as a factory to instantiate checkers for the
 * most common constraints. Namely, it lets you create checkers for the
 * following constraints:
 * <pre>
 *   - true, false               :: Not real constraints: they always return the
 *                               :: same value
 *   - allDiff(x_1, ..., x_n),   ::
 *   - sum(op, var)              :: sumEq, sumNe, sumLe, sumGe, sumLt, sumGt
 *   - element(xs, index, value) ::
 *   - table,                    ::
 *   - gcc, gccVar               :: The nuance between the two being that gcc
 *                               :: requires values and cardinalities to be
 *                               :: fixed and provided at instantiation time,
 *                               :: whereas gccVar only requires values. The
 *                               :: cardinalities being interpreted as the last
 *                               :: |values| variables of the given partial
 *                               :: assignment.
 * </pre>
 * FIXME: Question, veut-on garder les checkers comme une série de fonctions
 *        statiques ? ou veut-on en faire des classes ? (Elles seraient
 *        simples, cohésives, pas couplées + ca serait sans doute plus
 *        cohérent avec ce qui est fait pour les consistances)
 */
public final class Checkers {
    /** A utility class has no public constructor. */
    private Checkers() { }

    /** @return a fake checker evaluates any given assignment to true. */
    public static Checker alwaysTrue() {
        return s -> true;
    }

    /** @return a fake checker evaluates any given assignment to false. */
    public static Checker alwaysFalse() {
        return s -> false;
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
    public static Checker allDiff() {
        return s -> Set.copyOf(s).size() == s.size();
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
     *    $\otimes$ is one of the following operators: $&lt;, \le, =, \ne,
     *    \ge, &gt;$
     *
     * @param op the operator determining the condition that applies to the
     *           total of the sum
     * @param constant the constant that imposes some value constraint
     *                 on the sum
     * @return checker that tells whether the sum of the values of all variables
     *      of the given assignment satisfy some given linear condition.
     */
    public static Checker sum(final Operator op, final int constant) {
        return s -> op.check(
                        s.stream().mapToLong(Integer::longValue).sum(),
                        constant);
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
    public static Checker element() {
        return Checkers::element;
    }

    /**
     * This method parses the assignment so as to be able to interpret the
     * assignment as a candidate solution to an element constraint.
     *
     * It returns a checker that assumes the following structure:
     *   - variables in [0;size-3] constitute the array from plain variables
     *   - variable at size-2 is the 'index  variable
     *   - varaible at size-1 is the 'value'
     *
     * The checker is satisfied iff assignment[index] == value
     *
     * @param assignment  a candidate solution to an element constraint.
     * @return a checker verifying that the element constraint is satisfied by
     *         the given assignment.
     */
    private static boolean element(final Assignment assignment) {
        List<Integer> elements = assignment.subList(0, assignment.size() - 2);
        int              index = assignment.get(assignment.size() - 2);
        int              value = assignment.get(assignment.size() - 1);
        return element(elements, index, value);
    }

    /**
     * This is the actual implementation of the checker for the element
     * constraint. It is satisfied iff:
     *   - the given index is *valid* (in range [0..variables.size()[ ) **AND**
     *   - variables[index] == value.
     *
     * @param variables the variables that have been assigned in the candidate
     *                  solution
     * @param index the index of the variable whose value is being constrained
     * @param value the value which variable[index] should have for this checker
     *              to evaluate true
     *
     * @return true iff idex is valid **and** variable[index] == value
     */
    private static boolean element(
            final List<Integer> variables,
            final int index,
            final int value) {

        return isValidIndex(index, variables.size())
            && variables.get(index) == value;
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
    public static Checker table(final List<Assignment> table) {
        return table::contains;
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
    public static Checker gcc(
            final List<Integer> cardinalities,
            final List<Integer> values) {

        // Check the conformance of the sizes of cardinalities and values
        if (cardinalities.size() != values.size()) {
            throw new IllegalArgumentException(
                    "The number of given cardinalities does not match the "
                  + "number of given values");
        }

        if (new HashSet<>(values).size() != values.size()) {
            throw new IllegalArgumentException(
                    "All the values specified in the `values` list must be "
                  + "different");
        }

        return x -> uncheckedGcc(x, cardinalities, values);
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
    public static Checker gccVar(final List<Integer> values) {

        if (new HashSet<>(values).size() != values.size()) {
            throw new IllegalArgumentException(
                    "All the values specified in the `values` list must be "
                            + "different");
        }

        return x -> gcc(x.subList(0, x.size() - values.size()),        // vars.
                        x.subList(x.size() - values.size(), x.size()), // cardin
                        values);                                       // values
    }

    /**
     * This is the actual implementation of the gcc checker. It returns true iff
     * for all i, the value[i] occurs cardinalities[i] times in the list of
     * variables.
     *
     * @param variables the variables that have been assigned some value
     * @param cardinalities the cardinalities that must be forced onto the
     *                      variables for each value
     * @param values the values that must occur in the list of variables.
     *
     * @return true iff for all i, the value[i] occurs cardinalities[i] times
     * in the list of variables.
     */
    private static boolean gcc(
            final List<Integer> variables,
            final List<Integer> cardinalities,
            final List<Integer> values) {

        // Check the conformance of the sizes of cardinalities and values
        if (cardinalities.size() != values.size()) {
            throw new IllegalArgumentException(
                    "The number of given cardinalities does not match the "
                  + "number of given values");
        }

        if (new HashSet<>(values).size() != values.size()) {
            throw new IllegalArgumentException(
                    "All the values specified in the `values` list must be "
                            + "different");
        }

        return uncheckedGcc(variables, cardinalities, values);
    }

    /**
     * Performs the exact same validation as gcc but omits to sanity check the
     * input arguments.
     *
     * @param variables the variables that have been assigned some value
     * @param cardinalities the cardinalities that must be forced onto the
     *                      variables for each value
     * @param values the values that must occur in the list of variables.
     *
     * @return true iff for all i, the value[i] occurs cardinalities[i] times
     * in the list of variables.
     */
    private static boolean uncheckedGcc(
            final List<Integer> variables,
            final List<Integer> cardinalities,
            final List<Integer> values) {

        // short circuit: assess infeasibility in O(|Card|)
        int sumOfCardinalities =
                cardinalities.stream().mapToInt(Integer::intValue).sum();

        if (variables.size() < sumOfCardinalities) {
            return false;
        }

        // Actually perform the check
        final Map<Integer, Integer> actualCounts = new HashMap<>();

        for (Integer variable : variables) {
            actualCounts.merge(variable, 1, (x, y) -> x + y);
        }

        return   IntStream.range(0, values.size())
                .allMatch(i ->
                        actualCounts.getOrDefault(values.get(i), 0)
                                    .equals(cardinalities.get(i)));
    }

}
