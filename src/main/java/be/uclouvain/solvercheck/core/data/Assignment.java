package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.core.data.impl.AssignmentFactory;
import java.util.List;
import java.util.stream.Collector;

/**
 * An assignment is a complete mapping from variables to values.
 * In this context, we consider variables to be identified by an integer key and values to always be from type integer.
 */
public interface Assignment extends List<Integer> {

    /** Creates a new Assignement from the given list of domains */
    static Assignment from(final List<Integer> domains) {
        return AssignmentFactory.from(domains);
    }

    /** Creates a new Assignement from the given fixed partial assignment */
    static Assignment from(final PartialAssignment partial) {
        return AssignmentFactory.from(partial);
    }

    /**
     * @return a collector that combines any given stream of integer into an Assignment
     */
    static Collector<Integer, ?, Assignment> collector() {
        return AssignmentFactory.collector();
    }
}
