package be.uclouvain.solvercheck.pbt;

import java.util.stream.Stream;

public final class SkewedIntDistribution {

    /** Utility class has no public constructor. */
    private SkewedIntDistribution() { }

    public static Stream<Integer> stream(final Randomness randomness,
                                final int from,
                                final int to) {
        if (0 > from && 0 < to) {
            return Stream.concat(
               Stream.of(0, from, to),
               UniformIntDistribution.stream(randomness, from, to));
        } else {
            return Stream.concat(
               Stream.of(from, to),
               UniformIntDistribution.stream(randomness, from, to));
        }
    }
}
