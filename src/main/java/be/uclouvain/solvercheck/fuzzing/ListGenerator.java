package be.uclouvain.solvercheck.fuzzing;

import java.util.List;
import java.util.stream.Collectors;

public final class ListGenerator<T> extends BaseGenerator<List<T>> {

    private final Generator<T> delegate;
    private final int szMin;
    private final int szMax;

    public ListGenerator(final String name, final Generator<T> delegate, final int szMin, final int szMax) {
        super(name);
        this.delegate = delegate;
        this.szMin = szMin;
        this.szMax = szMax;
    }

    public ListGenerator(final Generator<T> delegate, final int szMin, final int szMax) {
        this("list of " + delegate.name(), delegate, szMin, szMax);
    }

    public ListGenerator(final Generator<T> delegate, final int size) {
        this("list of " + delegate.name(), delegate, size, size);
    }

    @Override
    public List<T> item(final Randomness randomness) {
        int size = randomness.randomInt(szMin, szMax);
        return delegate.generate(randomness).limit(size).collect(Collectors.toList());
    }
}
