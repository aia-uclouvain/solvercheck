package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.randomness.Randomness;

/** This generator produces a stream of random boolean values. */
public final class BooleanGenerator extends BaseGenerator<Boolean> {

    /**
     * Creates a new instance with a given name.
     *
     * @param name the name (description) associated to the generated values
     *             in an error report.
     */
    public BooleanGenerator(final String name) {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    public Boolean item(final Randomness randomness) {
        return randomness.nextBoolean();
    }
}
