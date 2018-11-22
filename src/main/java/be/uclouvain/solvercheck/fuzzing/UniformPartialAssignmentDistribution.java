package be.uclouvain.solvercheck.fuzzing;

import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.stream.Stream;

public final class UniformPartialAssignmentDistribution {

    /** Utility class has no public constructor. */
    private UniformPartialAssignmentDistribution() { }

    public static Stream<PartialAssignment> stream(final Randomness randomness,
                                                   final int     szMin,
                                                   final int     szMax,
                                                   final boolean allowErrors,
                                                   final int     domSzMax,
                                                   final int     valMin,
                                                   final int     valMax) {
        return Stream.generate(() -> {
            final int size = randomness.randomInt(szMin, szMax);
            return UniformDomainDistribution
               .stream(randomness, allowErrors, domSzMax, valMin, valMax)
               .limit(size)
               .collect(PartialAssignment.collector());
        });
    }
}
