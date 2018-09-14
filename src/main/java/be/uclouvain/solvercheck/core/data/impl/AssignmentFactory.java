package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

/**
 * The point of this factory is to create assignments. Note, this class is mostly present to ensure
 * the style-consistency with other classes {@see DomainFactory, PartialAssignmentFactory}.
 */
public final class AssignmentFactory {

    /** An utility class has no public constructor */
    private AssignmentFactory(){}

    /** Creates a new Assignement from the given list of domains */
    public static Assignment from(final List<Integer> domains) {
        return new BasicAssignment(domains);
    }

    /** Creates a new Assignement from the given fixed partial assignment */
    public static Assignment from(final PartialAssignment partial) {
        return partial.asAssignment();
    }

    /**
     * @return a collector that combines any given stream of integer into an Assignment
     */
    public static Collector<Integer, ?, Assignment> collector() {
        return Collector.of(
                ()          -> new ArrayList<Integer>(),      // supplier aka "mutable container"
                (acc, item) -> acc.add(item),                 // accumulator function
                (a1, a2)    -> { a1.addAll(a2); return a1; }, // combiner (to handle parallel processing)
                (acc)       -> from(acc)                      // finisher (performs the final conversion)
        );
    }
}
