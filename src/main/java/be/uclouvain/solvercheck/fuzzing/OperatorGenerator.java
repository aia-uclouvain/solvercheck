package be.uclouvain.solvercheck.fuzzing;

import be.uclouvain.solvercheck.core.data.Operator;

public final class OperatorGenerator extends BaseGenerator<Operator> {
    /**
     * Creates a new instance with a given name.
     *
     * @param name the name (description) associated to the generated values
     *             in an error report.
     */
    public OperatorGenerator(String name) {
        super(name);
    }

    @Override
    public Operator item(final Randomness randomness) {
        return Operator.values()[randomness.nextInt(Operator.values().length)];
    }
}
