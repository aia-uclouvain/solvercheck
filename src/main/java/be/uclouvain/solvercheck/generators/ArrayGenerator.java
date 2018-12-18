package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.randomness.Randomness;

import java.lang.reflect.Array;
import java.util.stream.Collectors;

/**
 * A generator to create arrays of objects specified by a given generator.
 *
 * @param <T> the type of the generated objects.
 */
public final class ArrayGenerator<T> extends BaseGenerator<T[]> {
    /** The generator used to actually generate the objects in the array. */
    private final Generator<T> delegate;
    /** The class of the objects in the generated array. */
    private final Class<T> clazz;
    /** The minimum size of the array (bounded by 0, obviously !). */
    private final int szMin;
    /** The maximum size of the array. */
    private final int szMax;

    /**
     * A constructor that specifies all possible arguments.
     *
     * @param name the name of the generator
     *             (how it will appear in the counter examples).
     * @param delegate the delegate generator used to create the actual values
     *                 in the array.
     * @param clazz the class of the generated objects.
     * @param szMin the minimum size of the generated array.
     * @param szMax the maximum size of the generated array.
     */
    public ArrayGenerator(final String name,
                          final Generator<T> delegate,
                          final Class<T> clazz,
                          final int szMin,
                          final int szMax) {
        super(name);
        this.delegate = delegate;
        this.clazz    = clazz;
        this.szMin    = szMin;
        this.szMax    = szMax;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public T[] item(final Randomness randomness) {
        int size = randomness.randomInt(szMin, szMax);
        return delegate.generate(randomness)
                    .limit(size)
                    .collect(Collectors.toList())
                    .toArray((T[]) Array.newInstance(clazz, size));
    }
}
