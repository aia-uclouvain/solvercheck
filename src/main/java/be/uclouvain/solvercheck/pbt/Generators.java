package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Generators {

    /** Utility class has no public constructor. */
    private Generators() { }

    public static Stream<Integer> ints(final Randomness randomness,
                                 final int from,
                                 final int to) {
        return SkewedIntDistribution.stream(randomness, from, to);
    }

    public static Stream<Boolean> booleans(final Randomness randomness) {
        return randomness
           .intsBetween(0, 1)
           .mapToObj(i -> i % 2 == 0);
    }

    public static Stream<List<Integer>> lists(final Randomness randomness,
                                              final int szMin,
                                              final int szMax,
                                              final int valMin,
                                              final int valMax) {

        return SkewedListDistribution
           .stream(randomness, szMin, szMax, valMin, valMax);
    }

    public static Stream<Assignment> assignments(final Randomness randomness,
                                                 final int szMin,
                                                 final int szMax,
                                                 final int valMin,
                                                 final int valMax) {

        return SkewedAssignmentDistribution
           .stream(randomness, szMin, szMax, valMin, valMax);
    }

    public static Stream<Domain> domains(final Randomness randomness,
                                         final boolean canBeEmpty,
                                         final int     szMax,
                                         final int     valMin,
                                         final int     valMax) {

        return SkewedDomainDistribution
           .stream(randomness, canBeEmpty, szMax, valMin, valMax);
    }

    public static Stream<PartialAssignment> partialAssignments(final Randomness randomness,
                                                               final int     szMin,
                                                               final int     szMax,
                                                               final boolean allowErrors,
                                                               final int     domSzMax,
                                                               final int     valMin,
                                                               final int     valMax) {

        return SkewedPartialAssignmentDistribution
           .stream(randomness, szMin, szMax, allowErrors, domSzMax, valMin, valMax);
    }

    public static Stream<Operator> operators(final Randomness randomness) {
        return UniformOperatorDistribution.stream(randomness);
    }

    public static Stream<List<Assignment>> tables(final Randomness randomness,
                                                  final int nbLinesMin,
                                                  final int nbLinesMax,
                                                  final int nbVarsMin,
                                                  final int nbVarsMax,
                                                  final int valMin,
                                                  final int valMax) {
        return SkewedTableDistribution
           .stream(randomness, nbLinesMin, nbLinesMax, nbVarsMin, nbVarsMax, valMin, valMax);
    }
}
