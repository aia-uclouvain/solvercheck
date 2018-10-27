package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Domain;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static be.uclouvain.solvercheck.pbt.Randomness.randomInt;

public final class UniformDomainDistribution {

    /** Utility class has no public constructor. */
    private UniformDomainDistribution() { }

    public static Stream<Domain> stream(final boolean canBeEmpty,
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
              randomInt(szMin, szMax),
              UniformIntDistribution.stream(valMin, valMax))
        );
    }


    private static Domain randomItem(final int ofSize,
                                     final IntStream source) {
        return source.limit(ofSize).boxed().collect(Domain.collector());
    }
}
