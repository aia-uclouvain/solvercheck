package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.task.StatefulFilter;

final class Branching implements DiveOperation {
    private final int variable;
    private final Operator operator;
    private final int value;

    Branching(final int variable,
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
