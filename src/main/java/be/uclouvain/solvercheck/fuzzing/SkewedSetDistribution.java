package be.uclouvain.solvercheck.fuzzing;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class SkewedSetDistribution {

    /** Utility class has no public constructor. */
    private SkewedSetDistribution() { }

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

        final Set<Integer> simplest = randomItem(
           randomness.randomInt(szMin, szMax),
           IntStream.generate(() -> 0)
        );

        final Set<Integer> minimum = randomItem(
           randomness.randomInt(szMin, szMax),
           IntStream.generate(() -> valMin)
        );

        final Set<Integer> maximum = randomItem(
           randomness.randomInt(szMin, szMax),
           IntStream.generate(() -> valMax)
        );

        if (0 > valMin && 0 < valMax) {
          return Stream.concat(
             Stream.of(simplest, minimum, maximum),
             UniformSetDistribution.stream(
                randomness, canBeEmpty, szMax, valMin, valMax)
          );
        } else {
          return Stream.concat(
             Stream.of(minimum, maximum),
             UniformSetDistribution.stream(
                randomness, canBeEmpty, szMax, valMin, valMax)
          );
        }
    }

    private static Set<Integer> randomItem(final int ofSize,
                                           final IntStream source) {
        return source.limit(ofSize).boxed().collect(Collectors.toSet());
    }

}
