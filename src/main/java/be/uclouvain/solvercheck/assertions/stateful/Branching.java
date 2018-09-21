package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.core.data.Operator;

/**
 * This class is meant to materialize a branching operation in the context of
 * a dive search.
 */
/* package */ final class Branching implements DiveOperation {
    /** This is the variable affected by the branching decision. */
    private final int variable;
    /** This is the operator constraining the domain of the variable. */
    private final Operator operator;
    /**
     * This is the value which, in combination with `operator` defines the
     * restriction on the domain of `variable`.
     */
    private final int value;

    /**
     * Creates a new materialized branching decision.
     *
     * @param variable the variable affected by the branching decision.
     * @param operator the operator constraining the domain of the variable.
     * @param value the value which, in combination with `operator` defines
     *              the restriction on the domain of `variable`.
     */
    /* package */ Branching(final int variable,
              final Operator operator,
              final int value) {
        this.variable = variable;
        this.operator = operator;
        this.value = value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("Branch on [[ x%d %s %d ]]",
                    variable,
                    operator,
                    value);
    }
}
