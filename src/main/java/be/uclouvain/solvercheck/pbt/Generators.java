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

    public static IntStream ints(final int from, final int to) {
        return SkewedIntDistribution.stream(from, to);
    }

    public static Stream<Boolean> booleans() {
        return ints(0, 1).mapToObj(i -> i % 2 == 0);
    }

    public static Stream<List<Integer>> lists(final int szMin,
                                              final int szMax,
                                              final int valMin,
                                              final int valMax) {

        return SkewedListDistribution.stream(szMin, szMax, valMin, valMax);
    }

    public static Stream<Assignment> assignments(final int szMin,
                                                 final int szMax,
                                                 final int valMin,
                                                 final int valMax) {

        return SkewedAssignmentDistribution.stream(szMin, szMax, valMin, valMax);
    }

    public static Stream<Domain> domains(final boolean canBeEmpty,
                                         final int     szMax,
                                         final int     valMin,
                                         final int     valMax) {

        return SkewedDomainDistribution.stream(canBeEmpty, szMax, valMin, valMax);
    }

    public static Stream<PartialAssignment> partialAssignments(final int     szMin,
                                                               final int     szMax,
                                                               final boolean allowErrors,
                                                               final int     domSzMax,
                                                               final int     valMin,
                                                               final int     valMax) {

        return SkewedPartialAssignmentDistribution
           .stream(szMin, szMax, allowErrors, domSzMax, valMin, valMax);
    }

    public static Stream<Operator> operators() {
        return UniformOperatorDistribution.stream();
    }

    public static Stream<List<Assignment>> tables(final int nbLinesMin,
                                                  final int nbLinesMax,
                                                  final int nbVarsMin,
                                                  final int nbVarsMax,
                                                  final int valMin,
                                                  final int valMax) {
        return SkewedTableDistribution
           .stream(nbLinesMin, nbLinesMax, nbVarsMin, nbVarsMax, valMin, valMax);
    }
}
