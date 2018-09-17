package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

/**
 * The point of this factory is to create assignments. Note, this class is
 * mostly present to ensure the style-consistency with other classes
 * {@see DomainFactory, PartialAssignmentFactory}.
 */
public final class AssignmentFactory {

    /** An utility class has no public constructor. */
    private AssignmentFactory() { }

    /**
     * @param assignedValues the list of values assigned to each of the
     *                       variables. (ith item in the list corresponds to
     *                       the value assigned to variable x_i)
     * @return Creates a new Assignement from the given list of assigned values
     */
    public static Assignment from(final List<Integer> assignedValues) {
        return new BasicAssignment(assignedValues);
    }

    /**
     * @param partial a partial assignment which is fixed. Hence, have all its
     *                domains limited to one single value.
     * @return Creates a new Assignement from the given fixed partial assignment
     */
    public static Assignment from(final PartialAssignment partial) {
        return partial.asAssignment();
    }

    /**
     * @return a collector that combines any given stream of integer into
     * an Assignment
     */
    @SuppressWarnings("checkstyle:leftcurly")
    public static Collector<Integer, ?, Assignment> collector() {
        return Collector.of(
                ()          -> new ArrayList<Integer>(),
                (acc, item) -> acc.add(item),
                (a1, a2)    -> { a1.addAll(a2); return a1; },
                (acc)       -> from(acc)
        );
    }
}
