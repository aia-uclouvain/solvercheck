package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Assignment;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static be.uclouvain.solvercheck.pbt.UniformIntDistribution.randomInt;

public final class SkewedAssignmentDistribution {

    /** Utility class has no public constructor. */
    private SkewedAssignmentDistribution() { }

    public static Stream<Assignment> stream(final int szMin,
                                            final int szMax,
                                            final int valMin,
                                            final int valMax) {

        final Assignment simplest = randomItem(
           randomInt(szMin, szMax),
           IntStream.generate(() -> 0)
        );

        final Assignment minimum = randomItem(
           randomInt(szMin, szMax),
           IntStream.generate(() -> valMin)
        );

        final Assignment maximum = randomItem(
           randomInt(szMin, szMax),
           IntStream.generate(() -> valMax)
        );

        if (0 > valMin && 0 < valMax) {
          return Stream.concat(
             Stream.of(simplest, minimum, maximum),
             UniformAssignmentDistribution.stream(szMin, szMax, valMin, valMax)
          );
        } else {
          return Stream.concat(
             Stream.of(minimum, maximum),
             UniformAssignmentDistribution.stream(szMin, szMax, valMin, valMax)
          );
        }
    }

    private static Assignment randomItem(final int ofSize,
                                         final IntStream source) {
        return source.limit(ofSize).boxed().collect(Assignment.collector());
    }
}
