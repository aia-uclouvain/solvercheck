package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Assignment;

import java.util.stream.Stream;

public final class UniformAssignmentDistribution {

    /** Utility class has no public constructor. */
    private UniformAssignmentDistribution() { }

    public static Stream<Assignment> stream(final Randomness randomness,
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

    private static Assignment randomItem(final int ofSize,
                                         final Stream<Integer> source) {
        return source.limit(ofSize).collect(Assignment.collector());
    }
}
