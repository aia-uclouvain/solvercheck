package be.uclouvain.solvercheck.fuzzing;

/**
 * Base class for a named generator. Encapsulates the logic relative to the
 * named part of the generator.
 *
 * @param <T> the type of generated object.
 */
public abstract class BaseGenerator<T> implements Generator<T> {
    /**
     * The name (description) associated to the generated values in an error
     * report.
     */
    private final String name;

    /**
     * Creates a new instance with a given name.
     *
     * @param name the name (description) associated to the generated values
     *             in an error report.
     */
    protected BaseGenerator(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public final String name() {
        return name;
    }
}
