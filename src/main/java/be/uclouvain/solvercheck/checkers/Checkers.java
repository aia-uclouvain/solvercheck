package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.data.Operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;

public final class Checkers {
    private Checkers() {
    }

    public static Checker alwaysTrue() {
        return s -> true;
    }

    public static Checker alwaysFalse() {
        return s -> false;
    }

    public static Checker allDiff() {
        return s -> Set.copyOf(s).size() == s.size();
    }

    public static Checker sum(final Operator op, final int constant) {
        return s -> op.check(s.stream().mapToInt(Integer::intValue).sum(), constant);
    }

    /**
     * This checker assumes the following structure:
     *  - variables in [0;size-3] constitute the array from plain variables
     *  - variable at size-2 is the 'index  variable
     *  - varaible at size-1 is the 'value'
     *
     *  Such that the element is satisfied iff array[index] == value
     * @return
     */
    public static Checker element() {
        return Checkers::element;
    }

    private static boolean element(final Assignment assignment) {
        List<Integer> elements = assignment.subList(0, assignment.size()-2);
        int              index = assignment.get(assignment.size()-2);
        int              value = assignment.get(assignment.size()-1);
        return element(elements, index, value);
    }
    private static boolean element(final List<Integer> variables, final int index, final int value) {
        return isValidIndex(index, variables.size()) && variables.get(index) == value;
    }

    public static Checker table(final List<Assignment> table) {
        return table::contains;
    }

    /**
     * This checkers verifies the Global Cardinality Constraint (gcc) considering that
     * $$
     * \forall i \in 0..values.length : |\left\{ v \in variables | v.val = values[i] \right\}| = cardinalities[i]
     * $$
     *
     * In this setup, the cardinalities are considered a given.
     */
    public static Checker gcc(final List<Integer> cardinalities, final List<Integer> values) {
        // Check the conformance of the sizes of cardinalities and values
        if( cardinalities.size() != values.size()) {
            throw new IllegalArgumentException("The number of given cardinalities do not match the number of given values");
        }

        return x -> uncheckedGcc(x, cardinalities, values);
    }

    /**
     * This checkers verifies the Global Cardinality Constraint (gcc) considering that
     * $$
     * \forall i \in 0..values.length : |\left\{ v \in variables | v.val = values[i] \right\}| = cardinalities[i].val
     * $$
     *
     * In this setup, the cardinalities are assumed to be the last N variables of the assignment.
     */
    public static Checker gccVar(final List<Integer> values) {
        return x -> gcc(x.subList(0, x.size()-values.size()),        // the variables
                        x.subList(x.size()-values.size(), x.size()), // the cardinalities
                        values);                                     // the values
    }

    private static boolean gcc(final List<Integer> variables, final List<Integer> cardinalities, final List<Integer> values){
        // Check the conformance of the sizes of cardinalities and values
        if( cardinalities.size() != values.size()) {
            throw new IllegalArgumentException("The number of given cardinalities do not match the number of given values");
        }

        return uncheckedGcc(variables, cardinalities, values);
    }

    /** performs the exact same validation as gcc but omits to sanity check the input arguments */
    private static boolean uncheckedGcc(final List<Integer> variables, final List<Integer> cardinalities, final List<Integer> values){
        // short circuit: assess infeasibility in O(1)
        if( variables.size() < cardinalities.size() ) {
            return false;
        }

        // Actually perform the check
        final Map<Integer, Integer> actualCounts = new HashMap<>();

        for(Integer variable : variables) {
            actualCounts.merge(variable, 1, (x,y) -> x+y);
        }

        return   IntStream.range(0 ,values.size())
                .allMatch(i ->
                        actualCounts.getOrDefault(values.get(i), 0).equals(cardinalities.get(i)));
    }

}
