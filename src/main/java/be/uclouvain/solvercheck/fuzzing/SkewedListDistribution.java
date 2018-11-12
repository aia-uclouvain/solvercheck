package be.uclouvain.solvercheck.fuzzing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class SkewedListDistribution {

    /** Utility class has no public constructor. */
    private SkewedListDistribution() { }

    public static Stream<List<Integer>> stream(final Randomness randomness,
                                               final int szMin,
                                               final int szMax,
                                               final int valMin,
                                               final int valMax) {

        final List<Integer> simplest = randomList(
           randomness.randomInt(szMin, szMax),
           IntStream.generate(() -> 0)
        );

        final List<Integer> minimum = randomList(
           randomness.randomInt(szMin, szMax),
           IntStream.generate(() -> valMin)
        );

        final List<Integer> maximum = randomList(
           randomness.randomInt(szMin, szMax),
           IntStream.generate(() -> valMax)
        );

        if (0 > valMin && 0 < valMax) {
          return Stream.concat(
             Stream.of(simplest, minimum, maximum),
             UniformListDistribution
                .stream(randomness, szMin, szMax, valMin, valMax)
          );
        } else {
          return Stream.concat(
             Stream.of(minimum, maximum),
             UniformListDistribution
                .stream(randomness, szMin, szMax, valMin, valMax)
          );
        }
    }

    private static List<Integer> randomList(final int ofSize,
                                            final IntStream source) {
        return source.limit(ofSize).boxed().collect(Collectors.toList());
    }
}
