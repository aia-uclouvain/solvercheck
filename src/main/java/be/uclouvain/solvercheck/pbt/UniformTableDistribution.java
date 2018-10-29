package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Assignment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class UniformTableDistribution {

    /** Utility class has no public constructor. */
    private UniformTableDistribution() { }

    public static Stream<List<Assignment>> stream(final Randomness randomness,
                                                  final int nbLinesMin,
                                                  final int nbLinesMax,
                                                  final int nbVarsMin,
                                                  final int nbVarsMax,
                                                  final int valMin,
                                                  final int valMax) {
        return Stream.generate(() -> {
            final int nbLines = randomness.randomInt(nbLinesMin, nbLinesMax);
            final int arity   = randomness.randomInt(nbVarsMin, nbVarsMax);

            return UniformAssignmentDistribution
               .stream(randomness, arity, arity, valMin, valMax)
               .limit(nbLines)
               .collect(Collectors.toList());
        });
    }
}
