package be.uclouvain.solvercheck.pbt;

import java.util.Random;
import java.util.stream.IntStream;

public final class UniformIntDistribution {

    /** Utility class has no public constructor. */
    private UniformIntDistribution() { }

    public static IntStream stream(final int from, final int to) {
        return Randomness.getInstance().ints(from, to);
    }
}
