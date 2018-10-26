package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Assignment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static be.uclouvain.solvercheck.pbt.UniformIntDistribution.randomInt;

public final class SkewedTableDistribution {

    /** Utility class has no public constructor. */
    private SkewedTableDistribution() { }

    public static Stream<List<Assignment>> stream(final int nbLinesMin,
                                                  final int nbLinesMax,
                                                  final int nbVarsMin,
                                                  final int nbVarsMax,
                                                  final int valMin,
                                                  final int valMax) {

        final Stream.Builder<List<Assignment>> exceptions = Stream.builder();

        if (0 >= nbLinesMin && 0 <= nbLinesMax) {
            // Add empty table if it is allowed
            exceptions.add(List.of());
        }

        if (1 >= nbLinesMin && 1 <= nbLinesMax) {
            // Add the 'simplest' case if it is allowed
            if (0 > valMin && 0 < valMax) {
                exceptions.add(
                   List.of(aFill(randomInt(nbVarsMin, nbVarsMax),0)));
            }

            // Add 'minimum'
            exceptions.add(
               List.of(aFill(randomInt(nbVarsMin, nbVarsMax), valMin)));

            // Add 'maximum'
            exceptions.add(
               List.of(aFill(randomInt(nbVarsMin, nbVarsMax), valMax)));
        }

        if (2 >= nbLinesMin && 2 <= nbLinesMax) {
            // Add a 'basic' case
            final int basicSz = randomInt(nbVarsMin, nbVarsMax);
            exceptions.add(
               List.of(aFill(basicSz, valMin), aFill(basicSz, valMax)));
        }


        return Stream.concat(
           exceptions.build(),
           UniformTableDistribution
              .stream(nbLinesMin, nbLinesMax, nbVarsMin, nbVarsMax, valMin, valMax)
        );
    }

    private static Assignment aFill(final int sz, final int value) {
        int[] data = new int[sz];
        Arrays.fill(data, value);

        return Assignment.from(data);
    }
}
