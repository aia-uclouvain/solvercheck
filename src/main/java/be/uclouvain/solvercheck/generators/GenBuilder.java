package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.fuzzing.Generator;

/**
 * This abstract class serves as a basis for the implementation of
 * fluent generator builders.
 *
 * @param <T> the type of the objects to generate.
 */
public abstract class GenBuilder<T> {
    /**
     * The name (description) of the items to be generated. This is useful to
     * be able to provide a meaningful information in the error reports.
     */
    private String name;

    /**
     * Creates a new Generator builder with the given name.
     *
     * @param name the name (description) of the items to be generated. This
     *             is useful to be able to provide a meaningful information
     *             in the error reports.
     */
    public GenBuilder(final String name) {
        this.name = name;
    }

    /**
     * Returns the actual generator (stream) instance, based on the current
     * configuration state.
     *
     * @return the actual generator.
     */
    public abstract Generator<T> build();

    /** @return the name (description) of this generator builder */
    public final String name() {
        return name;
    }
}
