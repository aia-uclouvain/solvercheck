package be.uclouvain.solvercheck.pbt;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static be.uclouvain.solvercheck.pbt.UniformIntDistribution.randomInt;

public final class SkewedListDistribution {

    /** Utility class has no public constructor. */
    private SkewedListDistribution() { }

    public static Stream<List<Integer>> stream(final int szMin,
                                               final int szMax,
                                               final int valMin,
                                               final int valMax) {

        final List<Integer> simplest = randomList(
           randomInt(szMin, szMax),
           IntStream.generate(() -> 0)
        );

        final List<Integer> minimum = randomList(
           randomInt(szMin, szMax),
           IntStream.generate(() -> valMin)
        );

        final List<Integer> maximum = randomList(
           randomInt(szMin, szMax),
           IntStream.generate(() -> valMax)
        );

        if (0 > valMin && 0 < valMax) {
          return Stream.concat(
             Stream.of(simplest, minimum, maximum),
             UniformListDistribution.stream(szMin, szMax, valMin, valMax)
          );
        } else {
          return Stream.concat(
             Stream.of(minimum, maximum),
             UniformListDistribution.stream(szMin, szMax, valMin, valMax)
          );
        }
    }

    private static List<Integer> randomList(final int ofSize,
                                            final IntStream source) {
        return source.limit(ofSize).boxed().collect(Collectors.toList());
    }
}
