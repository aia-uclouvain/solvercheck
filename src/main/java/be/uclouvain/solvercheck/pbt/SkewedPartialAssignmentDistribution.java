package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.utils.collections.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class SkewedPartialAssignmentDistribution {

    /** Utility class has no public constructor. */
    private SkewedPartialAssignmentDistribution() { }


    public static Stream<PartialAssignment> stream(final Randomness rand,
                                                   final int     szMin,
                                                   final int     szMax,
                                                   final boolean allowErrors,
                                                   final int     domSzMax,
                                                   final int     valMin,
                                                   final int     valMax) {

        if (domSzMax < 1) {
            return Stream.generate(() ->
                paFill(rand.randomInt(szMin, szMax), Domain.from())
            );
        }

        List<PartialAssignment> extreme = new ArrayList<>();

        // simplest
        if (0 > valMin && 0 < valMax) {
            extreme.add(
               paFill(rand.randomInt(szMin, szMax), Domain.from(0)));
        }

        // minimum
        extreme.add(
           paFill(rand.randomInt(szMin, szMax), Domain.from(valMin)));

        // maximum
        extreme.add(
           paFill(rand.randomInt(szMin, szMax), Domain.from(valMax)));

        // basic
        extreme.add(
           paFill(rand.randomInt(szMin, szMax), Domain.from(valMin, valMax)));

        // full. This check prevents blowing up memory
        if (((long) valMax - (long) valMin) <= domSzMax) {
            extreme.add(
                paFill(rand.randomInt(szMin, szMax), Domain.from(Range.between(valMin, valMax))));
        }

        final Stream<PartialAssignment> random =
           UniformPartialAssignmentDistribution
              .stream(rand, szMin, szMax, allowErrors, domSzMax, valMin, valMax);

        return Stream.concat(extreme.stream(), random);
    }

    private static PartialAssignment paFill(final int sz, final Domain dom) {
        ArrayList<Domain> l = new ArrayList<>(sz);
        for (int i = 0; i < sz; i++) {
            l.add(dom);
        }
        return PartialAssignment.from(l);
    }
}
