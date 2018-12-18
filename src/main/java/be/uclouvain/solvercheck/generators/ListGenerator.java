package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.randomness.Randomness;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This generator type produces stream of pseudo random lists. The objects
 * generated for the content of the lists are delegated to an other generator.
 *
 * @param <T> the type of the generated objects (in the list).
 */
public final class ListGenerator<T> extends BaseGenerator<List<T>> {
    /** The generator in charge of producing the actual payload of the lists. */
    private final Generator<T> delegate;
    /** The minimum size of a generated list. */
    private final int szMin;
    /** The maximum size of a generated list. */
    private final int szMax;

    /**
     * A constructor that specifies all parameters.
     *
     * @param name the name of the generator (used when printing a
     *             counterexample).
     * @param delegate the generator in charge of creating the payload.
     * @param szMin the minimum size of a list.
     * @param szMax the maximum size of a list.
     */
    public ListGenerator(final String name,
                         final Generator<T> delegate,
                         final int szMin,
                         final int szMax) {
        super(name);
        this.delegate = delegate;
        this.szMin = szMin;
        this.szMax = szMax;
    }

    /**
     * A constructor for anonymous lists.
     *
     * @param delegate the generator in charge of creating the payload.
     * @param szMin the minimum size of a list.
     * @param szMax the maximum size of a list.
     */
    public ListGenerator(final Generator<T> delegate, final int szMin, final int szMax) {
        this("list of " + delegate.name(), delegate, szMin, szMax);
    }

    /** {@inheritDoc} */
    @Override
    public List<T> item(final Randomness randomness) {
        int size = randomness.randomInt(szMin, szMax);
        return delegate.generate(randomness).limit(size).collect(Collectors.toList());
    }
}
