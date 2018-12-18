package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.randomness.Randomness;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This generator type produces stream of pseudo random sets. The objects
 * generated for the content of the sets are delegated to an other generator.
 *
 * @param <T> the type of the generated objects (in the set).
 */
public class SetGenerator<T> extends BaseGenerator<Set<T>> {
    /** The generator in charge of producing the actual payload of the sets. */
    private final Generator<T> delegate;
    /** The minimum size of a generated list. */
    private final int szMin;
    /** The maximum size of a generated list. */
    private final int szMax;

    /**
     * A constructor that specifies all parameters.
     *
     * @param name the name of the generator (used when reporting a
     *             counterexample).
     * @param delegate the generator in charge of creating the payload.
     * @param canBeEmpty can the generated sets be empty sets ?
     * @param szMax the maximum size of a set.
     */
    @SuppressWarnings("checkstyle:avoidinlineconditionals")
    public SetGenerator(final String name,
                        final Generator<T> delegate,
                        final boolean canBeEmpty,
                        final int szMax) {
        super(name);
        this.delegate = delegate;
        this.szMin = canBeEmpty ? 0 : 1;
        this.szMax = szMax;
    }

    /**
     * A constructor for anonymous sets.
     *
     * @param delegate the generator in charge of creating the payload.
     * @param canBeEmpty can the generated sets be empty sets ?
     * @param szMax the maximum size of a set.
     */
    public SetGenerator(final Generator<T> delegate,
                        final boolean canBeEmpty,
                        final int szMax) {
        this("set of " + delegate.name(), delegate, canBeEmpty, szMax);
    }

    /** {@inheritDoc} */
    @Override
    public Set<T> item(final Randomness randomness) {
        int size = randomness.randomInt(szMin, szMax);
        return delegate.generate(randomness).limit(size).collect(Collectors.toSet());
    }
}
