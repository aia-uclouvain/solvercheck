package be.uclouvain.solvercheck.fuzzing;

import java.util.Set;
import java.util.stream.Collectors;

public class SetGenerator<T> extends BaseGenerator<Set<T>> {
    private final Generator<T> delegate;
    private final int szMin;
    private final int szMax;

    public SetGenerator(final String name, final Generator<T> delegate, final boolean canBeEmpty, final int szMax) {
        super(name);
        this.delegate = delegate;
        this.szMin = canBeEmpty ? 0 : 1;
        this.szMax = szMax;
    }

    public SetGenerator(final Generator<T> delegate,  final boolean canBeEmpty, final int szMax) {
        this("set of " + delegate.name(), delegate, canBeEmpty, szMax);
    }

    @Override
    public Set<T> item(final Randomness randomness) {
        int size = randomness.randomInt(szMin, szMax);
        return delegate.generate(randomness).limit(size).collect(Collectors.toSet());
    }
}
