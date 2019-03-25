package be.uclouvain.solvercheck.randomness;

import java.util.Random;

/**
 * An uniform integer distribution.
 *
 * Technical Note: this distribution is implemented as a singleton.
 */
public final class UniformDistribution implements Distribution {
    /** The only instance of this class. */
    private static final UniformDistribution INSTANCE = new UniformDistribution();

    /** A singleton class has no public constructor. */
    private UniformDistribution() { }

    /** @return the only existing instance of this class. */
    public static UniformDistribution getInstance() {
        return INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final Random prng, final int l, final int h) {
        return (int) (prng.nextDouble() * ((long) h - (long) l) + l);
    }
}
