package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Domain;

import java.util.stream.Stream;

public final class UniformDomainDistribution {

    /** Utility class has no public constructor. */
    private UniformDomainDistribution() { }

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

        return Stream.generate(() ->
           randomItem(
              randomness.randomInt(szMin, szMax),
              UniformIntDistribution.stream(randomness, valMin, valMax))
        );
    }


    private static Domain randomItem(final int ofSize,
                                     final Stream<Integer> source) {
        return source.limit(ofSize).collect(Domain.collector());
    }
}
