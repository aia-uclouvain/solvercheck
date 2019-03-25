package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.randomness.Randomness;
import be.uclouvain.solvercheck.randomness.Distribution;
import be.uclouvain.solvercheck.randomness.UniformDistribution;

import java.util.stream.Stream;

/**
 * This generator is used to create `Assignment` objects
 * (basically list of ints).
 */
public final class AssignmentGenerator extends BaseGenerator<Assignment> {
    /** The smallest value that can appear in one assignment. */
    private final int valMin;
    /** The greatest value that can appear in one assignment. */
    private final int valMax;
    /** The minimum size of a generated assignment. */
    private final int szMin;
    /** The maximum size of a generated assignment. */
    private final int szMax;
    /** The distribution for the size of the assignment (uniform). */
    private final Distribution szDist;
    /** The distribution for the values of the assignment (multi modal). */
    private final Distribution valDist;

    /**
     * A constructor with all arguments.
     *
     * @param name the name of the generator (used when explaining counter example).
     * @param szMin The minimum size of a generated assignment.
     * @param szMax The maximum size of a generated assignment.
     * @param valMin The smallest value that can appear in one assignment.
     * @param valMax The greatest value that can appear in one assignment.
     */
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

    /** {@inheritDoc} */
    @Override
    public Assignment item(final Randomness prng) {
        int size = szDist.next(prng, szMin, szMax);
        return Stream
           .generate(() -> valDist.next(prng, valMin, valMax))
           .limit(size)
           .collect(Assignment.collector());
    }

}
