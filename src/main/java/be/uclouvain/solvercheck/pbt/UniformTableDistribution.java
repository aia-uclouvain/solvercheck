package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Assignment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.uclouvain.solvercheck.pbt.UniformIntDistribution.randomInt;

public final class UniformTableDistribution {

    /** Utility class has no public constructor. */
    private UniformTableDistribution() { }

    public static Stream<List<Assignment>> stream(final int nbLinesMin,
                                                  final int nbLinesMax,
                                                  final int nbVarsMin,
                                                  final int nbVarsMax,
                                                  final int valMin,
                                                  final int valMax) {
        return Stream.generate(() -> {
            final int nbLines = randomInt(nbLinesMin, nbLinesMax);
            final int arity   = randomInt(nbVarsMin, nbVarsMax);

            return UniformAssignmentDistribution
               .stream(arity, arity, valMin, valMax)
               .limit(nbLines)
               .collect(Collectors.toList());
        });
    }
}
