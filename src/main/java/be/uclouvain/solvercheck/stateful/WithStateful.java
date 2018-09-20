package be.uclouvain.solvercheck.stateful;

import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.core.task.StatefulFilter;

/**
 * This interface collects all the useful methods that let you plug
 * SolverCheck's stateful DSL into your code.
 *
 * Note: so far, the stateful DSL only comprises a way to tersely express
 * that some filter should be decorated to appear as if it were a SatefulFilter.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithStateful {

    /**
     * you turn any Filter into a StatefulFilter which can be interacted with
     * so as to simulate the interaction with a solver.
     *
     * @param filter the filter being adapted to a stateful filter.
     * @return a StatefulFilter adapting the given `filter` so as to be
     * useable from within a `Dive` check.
     */
    default StatefulFilter stateful(final Filter filter) {
        return new StatefulFilterAdapter(filter);
    }

}
