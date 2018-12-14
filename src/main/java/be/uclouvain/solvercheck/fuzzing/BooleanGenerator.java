package be.uclouvain.solvercheck.fuzzing;

public final class BooleanGenerator extends BaseGenerator<Boolean> {

    /**
     * Creates a new instance with a given name.
     *
     * @param name the name (description) associated to the generated values
     *             in an error report.
     */
    public BooleanGenerator(String name) {
        super(name);
    }

    @Override
    public Boolean item(final Randomness randomness) {
        return randomness.nextBoolean();
    }
}
