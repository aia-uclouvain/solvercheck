package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.randomness.Distribution;
import be.uclouvain.solvercheck.randomness.Randomness;

/**
 * This generator produces streams of pseudo random integer. These are
 * distributed along a multimodal distribution.
 */
public final class IntGenerator extends BaseGenerator<Integer> {
    /** The lowest value that can be generated. */
    private final int from;
    /** The highest value that can be generated. */
    private final int to;
    /** The values distribution. */
    private final Distribution distribution;

    /**
     * A constructor that specifies all necessary information.
     *
     * @param name the name of this generator.
     * @param from the lowest value that can be generated.
     * @param to the highest value that can be generated.
     */
    public IntGenerator(final String name, final int from, final int to) {
        super(name);
        this.from = from;
        this.to   = to;
        this.distribution = mkDist(from, to);
    }

    /** {@inheritDoc} */
    @Override
    public Integer item(final Randomness randomness) {
        return distribution.next(randomness, from, to);
    }
}
