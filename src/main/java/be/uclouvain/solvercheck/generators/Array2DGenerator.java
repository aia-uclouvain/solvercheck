package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.randomness.Randomness;

import java.lang.reflect.Array;
import java.util.stream.Collectors;

/**
 * A generator to create arrays of objects specified by a given generator.
 *
 * @param <T> the type of the generated objects.
 */
public final class Array2DGenerator<T> extends BaseGenerator<T[][]> {
    /** The generator used to actually generate the objects in the array. */
    private final Generator<T> delegate;
    /** The class of the objects in the generated array. */
    private final Class<T> clazz;
    /** The minimum size of the array in dim 0(bounded by 0, obviously !). */
    private final int szMin0;
    /** The maximum size of the array in dim 0. */
    private final int szMax0;
    /** The maximum size of the array in dim 1(bounded by 0, obviously !). */
    private final int szMin1;
    /** The maximum size of the array in dim 1. */
    private final int szMax1;

    /**
     * A constructor that specifies all possible arguments.
     *
     * @param name the name of the generator
     *             (how it will appear in the counter examples).
     * @param delegate the delegate generator used to create the actual values
     *                 in the array.
     * @param clazz the class of the generated objects.
     * @param szMin0 the minimum size of the generated array in dim0.
     * @param szMax0 the maximum size of the generated array in dim0.
     * @param szMin1 the minimum size of the generated array in dim1.
     * @param szMax1 the maximum size of the generated array in dim1.
     */
    public Array2DGenerator(final String name,
                            final Generator<T> delegate,
                            final Class<T> clazz,
                            final int szMin0,
                            final int szMax0,
                            final int szMin1,
                            final int szMax1) {
        super(name);
        this.delegate = delegate;
        this.clazz    = clazz;
        this.szMin0    = szMin0;
        this.szMax0    = szMax0;
        this.szMin1    = szMin1;
        this.szMax1    = szMax1;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public T[][] item(final Randomness randomness) {
        int size0 = randomness.randomInt(szMin0, szMax0);
        int size1 = randomness.randomInt(szMin1, szMax1);
        T[][] result = (T[][]) Array.newInstance(clazz, size0, size1);
        for (int i = 0; i < size0; i++) {
            for (int j = 0; j < size1; j++) {
                result[i][j] = delegate.item(randomness);
            }
        }
        return result;
    }
}
