package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Domain;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class SkewedDomainDistribution {

    /** Utility class has no public constructor. */
    private SkewedDomainDistribution() { }

    public static Stream<Domain> stream(final Randomness randomness,
                                        final boolean canBeEmpty,
                                        final int     szMax,
                                        final int     valMin,
                                        final int     valMax) {

        int min = 1;
        if (canBeEmpty) {
            min = 0;
        }

        final int szMin = min;

        final Domain simplest = randomItem(
           randomness.randomInt(szMin, szMax),
           IntStream.generate(() -> 0)
        );

        final Domain minimum = randomItem(
           randomness.randomInt(szMin, szMax),
           IntStream.generate(() -> valMin)
        );

        final Domain maximum = randomItem(
           randomness.randomInt(szMin, szMax),
           IntStream.generate(() -> valMax)
        );

        if (0 > valMin && 0 < valMax) {
          return Stream.concat(
             Stream.of(simplest, minimum, maximum),
             UniformDomainDistribution.stream(
                randomness, canBeEmpty, szMax, valMin, valMax)
          );
        } else {
          return Stream.concat(
             Stream.of(minimum, maximum),
             UniformDomainDistribution.stream(
                randomness, canBeEmpty, szMax, valMin, valMax)
          );
        }
    }

    private static Domain randomItem(final int ofSize,
                                         final IntStream source) {
        return source.limit(ofSize).boxed().collect(Domain.collector());
    }

}
