package be.uclouvain.solvercheck.core.task;


import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;

/**
 * This interface adapts the notion of `Filter` to place it in the context
 * of a sateful (incremental) problem solving. Its purpose it to place the
 * tested constraint under realistic circumstances and detect classes of bugs
 * that could otherwise have gone unnoticed until the constraint is actually
 * used to solve some problem.
 */
public interface StatefulFilter {
    /**
     * Gives an opportunity to the tested subsystem to initialize itself
     * before to actually start testing the behavior of the constraint.
     *
     * @param initialDomains the initial domains of the considered variables.
     */
    void setup(PartialAssignment initialDomains);

    /**
     * Tells the subsystem that it should remember its current state so as to
     * be able to restore it when a call to `popState()` is issued.
     */
    void pushState();

    /**
     * Tells the subsystem that it should restore its internal state to the
     * previous snapshot of that state. (It should restore its state to the
     * value it had before the last call to `pushState()`.
     */
    void popState();

    /**
     * Returns the partial assignment representing the current value of all
     * the variables domains as maintained by the subssystem.
     *
     * @return the partial assignment representing the current domains of all
     * variables.
     */
    PartialAssignment currentState();

    /**
     * Tells the subsystem that it has to branch on [[ variable OP value ]],
     * either through the posting an additional constraint to the underlying cp
     * solver or through the application of some decision related logic.
     *
     * @param variable the variable on which a branching decision is made
     * @param op the constraint imposed on the value that can be taken by
     *           `variable`.
     * @param value in conjunction with `op` determines the constraint
     *              imposed on the values that can be taken by `variable`.
     */
    void branchOn(int variable, Operator op, int value);
}
