package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.utils.collections.Range;

import java.util.ArrayList;
import java.util.stream.Stream;

import static be.uclouvain.solvercheck.pbt.UniformIntDistribution.randomInt;

public final class SkewedPartialAssignmentDistribution {

    /** Utility class has no public constructor. */
    private SkewedPartialAssignmentDistribution() { }


    public static Stream<PartialAssignment> stream(final int     szMin,
                                                   final int     szMax,
                                                   final boolean allowErrors,
                                                   final int     domSzMax,
                                                   final int     valMin,
                                                   final int     valMax) {

        final PartialAssignment minimum =
           paFill(randomInt(szMin, szMax), Domain.from(valMin));

        final PartialAssignment maximum =
           paFill(randomInt(szMin, szMax), Domain.from(valMax));

        final PartialAssignment simplest =
           paFill(randomInt(szMin, szMax), Domain.from(0));

        final PartialAssignment basic =
           paFill(randomInt(szMin, szMax), Domain.from(valMin, valMax));

        final PartialAssignment full =
           paFill(randomInt(szMin, szMax), Domain.from(Range.between(valMin, valMax)));

        Stream<PartialAssignment> extremal =
           Stream.of(minimum, maximum, basic, full);

        if (0 > valMin && 0 < valMax) {
            extremal = Stream.of(simplest, minimum, maximum, basic, full);
        }

        final Stream<PartialAssignment> random =
           UniformPartialAssignmentDistribution
              .stream(szMin, szMax, allowErrors, domSzMax, valMin, valMax);

        return Stream.concat(extremal, random);
    }

    private static PartialAssignment paFill(final int sz, final Domain dom) {
        ArrayList<Domain> l = new ArrayList<>(sz);
        for (int i = 0; i < sz; i++) {
            l.add(dom);
        }
        return PartialAssignment.from(l);
    }
}
