package be.uclouvain.solvercheck.pbt;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class UniformListDistribution {

    /** Utility class has no public constructor. */
    private UniformListDistribution() { }

    public static Stream<List<Integer>> stream(final Randomness randomness,
                                               final int szMin,
                                               final int szMax,
                                               final int valMin,
                                               final int valMax) {
        return Stream.generate(() ->
           randomItem(
              randomness.randomInt(szMin, szMax),
              UniformIntDistribution.stream(randomness, valMin, valMax))
        );
    }

    private static List<Integer> randomItem(final int ofSize,
                                            final Stream<Integer> source) {
        return source.limit(ofSize).collect(Collectors.toList());
    }
}
