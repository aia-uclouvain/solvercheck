package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.pbt.Generator;

/**
 * This abstract class serves as a basis for the implementation of
 * fluent generator builders.
 *
 * @param <T> the type of the objects to generate.
 */
public abstract class GenBuilder<T> {
    private String name;

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

    public final String name() {
        return name;
    }
}
