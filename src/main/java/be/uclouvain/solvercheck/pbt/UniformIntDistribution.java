package be.uclouvain.solvercheck.pbt;

import java.util.Random;
import java.util.stream.IntStream;

public final class UniformIntDistribution {

    public static final Random RANDOM = new Random();

    /** Utility class has no public constructor. */
    private UniformIntDistribution() { }

    public static IntStream stream(final int from, final int to) {
        return RANDOM.ints(from, to);
    }

    public static int randomInt(final int from, final int to) {
        return RANDOM.nextInt((to - from) + 1) + from;
    }
}
