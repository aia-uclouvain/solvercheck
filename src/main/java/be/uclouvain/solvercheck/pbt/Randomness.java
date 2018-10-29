package be.uclouvain.solvercheck.pbt;

import java.util.Random;

public final class Randomness extends Random {
    /** The seed used to initialize the prng */
    private long seed;

    public Randomness(final long seed) {
        super(seed);
        this.seed = seed;
    }

    public int randomInt(final int from, final int to) {
        return nextInt((to - from) + 1) + from;
    }

    public long getSeed() {
        return seed;
    }
}
