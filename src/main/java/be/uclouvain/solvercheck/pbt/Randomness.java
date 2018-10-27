package be.uclouvain.solvercheck.pbt;

import java.util.Random;

public final class Randomness extends Random {

    private static final Randomness INSTANCE = new Randomness();

    private long seed;

    /** No public constructor. */
    private Randomness() {
        seed   = System.currentTimeMillis();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setSeed(final long seed) {
        this.seed = seed;
        super.setSeed(seed);
    }

    public static Randomness getInstance() {
        return INSTANCE;
    }

    public static int randomInt(final int from, final int to) {
        return getInstance().nextInt((to - from) + 1) + from;
    }

    public static void seed(long seed) {
        getInstance().setSeed(seed);
    }
    public static long seed() {
        return getInstance().seed;
    }
}
