package be.uclouvain.solvercheck.pbt;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class UniformIntDistribution {

    /** Utility class has no public constructor. */
    private UniformIntDistribution() { }

    public static Stream<Integer> stream(final Randomness randomness,
                                final int from,
                                final int to) {
        return randomness.ints(from, to).boxed();
    }
}
