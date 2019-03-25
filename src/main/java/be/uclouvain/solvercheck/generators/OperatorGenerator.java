package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.randomness.Randomness;

/** This generator produces streams of pseudo random operators. */
public final class OperatorGenerator extends BaseGenerator<Operator> {
    /**
     * Creates a new instance with a given name.
     *
     * @param name the name (description) associated to the generated values
     *             in an error report.
     */
    public OperatorGenerator(final String name) {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    public Operator item(final Randomness randomness) {
        return Operator.values()[randomness.nextInt(Operator.values().length)];
    }
}
