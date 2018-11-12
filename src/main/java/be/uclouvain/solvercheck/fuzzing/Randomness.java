package be.uclouvain.solvercheck.fuzzing;

import java.util.Random;
import java.util.stream.IntStream;

public final class Randomness extends Random {
    /** The seed used to initialize the prng. */
    private long seed;

    public Randomness(final long seed) {
        super(seed);
        this.seed = seed;
    }

    public int randomInt(final int from, final int to) {
        return nextInt((to - from) + 1) + from;
    }

    public IntStream intsBetween(final int lowerBound, final int upperBound) {
        return super
           .longs((long) lowerBound,  1L + (long) upperBound)
           .mapToInt(l -> (int) l);
    }

    public long getSeed() {
        return seed;
    }
}
