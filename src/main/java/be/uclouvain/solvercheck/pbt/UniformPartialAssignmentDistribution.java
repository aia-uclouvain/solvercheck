package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.stream.Stream;

import static be.uclouvain.solvercheck.pbt.Randomness.randomInt;

public final class UniformPartialAssignmentDistribution {

    /** Utility class has no public constructor. */
    private UniformPartialAssignmentDistribution() { }

    public static Stream<PartialAssignment> stream(final int     szMin,
                                                   final int     szMax,
                                                   final boolean allowErrors,
                                                   final int     domSzMax,
                                                   final int     valMin,
                                                   final int     valMax) {
        return Stream.generate(() -> {
            final int size = randomInt(szMin, szMax);
            return UniformDomainDistribution.stream(allowErrors, domSzMax, valMin, valMax)
               .limit(size)
               .collect(PartialAssignment.collector());
        });
    }
}
