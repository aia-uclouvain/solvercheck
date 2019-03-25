package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.core.data.impl.AssignmentFactory;

import java.util.List;
import java.util.stream.Collector;

/**
 * An assignment is a complete mapping from variables to values. In this
 * context, we consider variables to be identified by an integer key and values
 * to always be from type integer.
 */
public interface Assignment extends List<Integer> {

    /**
     * @param assignedValues the list of values assigned to each of the
     *                       variables. (ith item in the list corresponds to
     *                       the value assigned to variable x_i)
     * @return Creates a new Assignement from the given list of assigned values
     */
    static Assignment from(final List<Integer> assignedValues) {
        return AssignmentFactory.from(assignedValues);
    }

    /**
     * @param assignedValues the list of values assigned to each of the
     *                       variables. (ith item in the list corresponds to
     *                       the value assigned to variable x_i)
     * @return Creates a new Assignement from the given list of assigned values
     */
    static Assignment from(final int... assignedValues) {
        return AssignmentFactory.from(assignedValues);
    }

    /**
     * @param partial a partial assignment which is fixed. Hence, have all its
     *                domains limited to one single value.
     * @return Creates a new Assignement from the given fixed partial assignment
     */
    static Assignment from(final PartialAssignment partial) {
        return AssignmentFactory.from(partial);
    }

    /**
     * @return a collector that combines any given stream of integer into an
     * Assignment
     */
    static Collector<Integer, ?, Assignment> collector() {
        return AssignmentFactory.collector();
    }
}
