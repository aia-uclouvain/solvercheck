package be.uclouvain.solvercheck.pbt;

import java.util.stream.IntStream;

public final class SkewedIntDistribution {

    /** Utility class has no public constructor. */
    private SkewedIntDistribution() { }

    public static IntStream stream(final int from, final int to) {
        if (0 > from && 0 < to) {
            return IntStream.concat(
               IntStream.of(0, from, to),
               UniformIntDistribution.stream(from, to));
        } else {
            return IntStream.concat(
               IntStream.of(from, to),
               UniformIntDistribution.stream(from, to));
        }
    }
}
