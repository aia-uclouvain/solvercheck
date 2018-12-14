package be.uclouvain.solvercheck.fuzzing;

import java.lang.reflect.Array;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ArrayGenerator<T> extends BaseGenerator<T[]>{
    private final Generator<T> delegate;
    private final Class<T> clazz;
    private final int szMin;
    private final int szMax;

    public ArrayGenerator(final String name, final Generator<T> delegate, final Class<T> clazz, final int szMin,
                         final int szMax) {
        super(name);
        this.delegate = delegate;
        this.clazz    = clazz;
        this.szMin = szMin;
        this.szMax = szMax;
    }

    public ArrayGenerator(final Generator<T> delegate, final Class<T> clazz, final int szMin, final int szMax) {
        this("array of " + delegate.name(), delegate, clazz, szMin, szMax);
    }

    public ArrayGenerator(final Generator<T> delegate, final Class<T> clazz, final int size) {
        this("array of " + delegate.name(), delegate, clazz, size, size);
    }

    @Override
    public T[] item(final Randomness randomness) {
        int size = randomness.randomInt(szMin, szMax);
        return delegate.generate(randomness)
                    .limit(size)
                    .collect(Collectors.toList())
                    .toArray((T[]) Array.newInstance(clazz, size));
    }
}
