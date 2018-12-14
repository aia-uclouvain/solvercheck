package be.uclouvain.solvercheck.fuzzing;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.fuzzing.distribution.Distribution;
import be.uclouvain.solvercheck.fuzzing.distribution.UniformDistribution;

import java.util.stream.Stream;

public class AssignmentGenerator extends BaseGenerator<Assignment> {

    private final int valMin;
    private final int valMax;
    private final int szMin;
    private final int szMax;
    private final Distribution szDist;
    private final Distribution valDist;


    public AssignmentGenerator(final String name,
                         final int szMin,
                         final int szMax,
                         final int valMin,
                         final int valMax) {
        super(name);
        this.szMin   = szMin;
        this.szMax   = szMax;
        this.valMin  = valMin;
        this.valMax  = valMax;
        this.szDist  = UniformDistribution.getInstance();
        this.valDist = mkDist(valMin, valMax);
    }

    @Override
    public Assignment item(final Randomness prng) {
        int size = szDist.next(prng, szMin, szMax);
        return Stream.generate(() -> valDist.next(prng, valMin, valMax)).limit(size).collect(Assignment.collector());
    }

}
