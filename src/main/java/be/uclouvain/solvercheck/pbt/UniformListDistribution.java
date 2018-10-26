package be.uclouvain.solvercheck.pbt;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class UniformListDistribution {

    /** Utility class has no public constructor. */
    private UniformListDistribution() { }

    public static Stream<List<Integer>> stream(final int szMin,
                                               final int szMax,
                                               final int valMin,
                                               final int valMax) {
        return Stream.generate(() ->
           randomItem(
              UniformIntDistribution.randomInt(szMin, szMax),
              UniformIntDistribution.stream(valMin, valMax))
        );
    }

    private static List<Integer> randomItem(final int ofSize,
                                            final IntStream source) {
        return source.limit(ofSize).boxed().collect(Collectors.toList());
    }
}
