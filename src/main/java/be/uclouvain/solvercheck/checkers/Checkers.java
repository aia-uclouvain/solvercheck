package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.core.Assignment;
import be.uclouvain.solvercheck.core.Checker;
import be.uclouvain.solvercheck.core.Operator;

import java.util.List;

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
        return s -> s.asSet().size() == s.size();
    }

    public static Checker sum(final Operator op, final int constant) {
        return s -> op.check(s.stream().mapToInt(Integer::intValue).sum(), constant);
    }

    /**
     * This checker assumes the following structure:
     *  - variables in [0;size-3] constitute the array of plain variables
     *  - variable at size-2 is the 'index  variable
     *  - varaible at size-1 is the 'value'
     *
     *  Such that the element is satisfied iff array[index] == value
     * @return
     */
    public static Checker element() {
        return Checkers::element;
    }

    public static boolean element(final Assignment assignment) {
        List<Integer> elements = assignment.asList().subList(0, assignment.size()-2);
        int              index = assignment.get(-2);
        int              value = assignment.get(-1);
        return element(elements, index, value);
    }
    public static boolean element(final List<Integer> elements, final int index, final int value) {
        return isValidIndex(index, elements.size()) && elements.get(index) == value;
    }

    public static Checker table(final List<Assignment> table) {
        return table::contains;
    }
    // TODO: gcc, gccVar, sum, table
}
