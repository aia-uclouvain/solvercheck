package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Assignment;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class UniformAssignmentDistribution {

    /** Utility class has no public constructor. */
    private UniformAssignmentDistribution() { }

    public static Stream<Assignment> stream(final int szMin,
                                            final int szMax,
                                            final int valMin,
                                            final int valMax) {
        return Stream.generate(() ->
           randomItem(
              UniformIntDistribution.randomInt(szMin, szMax),
              UniformIntDistribution.stream(valMin, valMax))
        );
    }

    private static Assignment randomItem(final int ofSize,
                                         final IntStream source) {
        return source.limit(ofSize).boxed().collect(Assignment.collector());
    }
}
