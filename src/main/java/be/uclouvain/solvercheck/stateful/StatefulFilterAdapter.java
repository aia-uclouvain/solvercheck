package be.uclouvain.solvercheck.stateful;

import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.core.task.StatefulFilter;

import java.util.Stack;

/**
 * This class lets you turn any Filter into a StatefulFilter which can be
 * interacted with so as to simulate the interaction with a solver.
 *
 * Even though this class permits the adaptation of virtually any Filter,
 * it is understood that it will likely only be used to adapt trusted filters
 * (the ones built on top of a simple checker).
 */
public final class StatefulFilterAdapter implements StatefulFilter {
    /** The filter which is being adapted into a StatefulFilter. */
    private final Filter filter;
    /** The current value of variables domains. */
    private PartialAssignment current;
    /** The trail of partial assignments that has led to the `current` state. */
    private final Stack<PartialAssignment> snapshots;

    /**
     * Creates a new stateful filter adapting of some existing filter.
     *
     * @param filter the filter being adapted to a stateful filter.
     */
    public StatefulFilterAdapter(final Filter filter) {
        this.filter = filter;
        this.snapshots = new Stack<>();
    }

    /** {@inheritDoc} */
    @Override
    public void setup(final PartialAssignment initialDomains) {
        snapshots.clear();
        current = filter(initialDomains);
    }

    /** {@inheritDoc} */
    @Override
    public void pushState() {
        snapshots.push(current);
    }

    /** {@inheritDoc} */
    @Override
    public void popState() {
        if (!snapshots.isEmpty()) {
            current = snapshots.pop();
        }
    }

    /** {@inheritDoc} */
    @Override
    public PartialAssignment currentState() {
        return current;
    }

    /** {@inheritDoc} */
    @Override
    public void branchOn(final int variable, final Operator op, final int value) {
        current = filter(PartialAssignment.restrict(current, variable, op, value));
    }

    /**
     * Filters the given partial assignment.
     *
     * @param partial the partial assignment to filter
     * @return a filtered copy of the partial assignment which satisfies
     * `filter`.
     */
    private PartialAssignment filter(final PartialAssignment partial) {
        return this.filter.filter(partial);
    }
}
