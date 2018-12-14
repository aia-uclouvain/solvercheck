package be.uclouvain.solvercheck.fuzzing.distribution;

import java.util.Random;

public final class UniformDistribution implements Distribution {
    private static final UniformDistribution INSTANCE = new UniformDistribution();

    private UniformDistribution() { }

    public static UniformDistribution getInstance() {
        return INSTANCE;
    }

    @Override
    public int next(final Random prng, final int l, final int h) {
        return (int) (prng.nextDouble() * ((long) h - (long) l) + l);
    }
}
