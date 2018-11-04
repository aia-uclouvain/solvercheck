package be.uclouvain.solvercheck.pbt;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class UniformSetDistribution {

    /** Utility class has no public constructor. */
    private UniformSetDistribution() { }

    public static Stream<Set<Integer>> stream(final Randomness randomness,
                                              final boolean canBeEmpty,
                                              final int     szMax,
                                              final int     valMin,
                                              final int     valMax) {
        int min = 1;
        if (canBeEmpty) {
            min = 0;
        }

        final int szMin = min;

        return Stream.generate(() ->
           randomItem(
              randomness.randomInt(szMin, szMax),
              UniformIntDistribution.stream(randomness, valMin, valMax))
        );
    }

    private static Set<Integer> randomItem(final int ofSize,
                                           final Stream<Integer> source) {
        return source.limit(ofSize).collect(Collectors.toSet());
    }
}
