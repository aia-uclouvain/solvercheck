package be.uclouvain.solvercheck.fuzzing.distribution;

import java.util.Arrays;
import java.util.Random;

/**
 * A simple multi-modal distribution.
 */
public final class MultiModalDistribution implements Distribution {
    /** The value at the center of the 'peaks'. */
    private final int[]  modes;
    /** The likelihood of picking one of the modes */
    private final double likelihood;

    /**
     * Fully configurable plain multimodal distribution constructor.
     *
     * @param modes      the modes of the distribution.
     * @param likelihood the likelihood of picking one of the modes.
     */
    public MultiModalDistribution(final int[] modes, final double likelihood) {
        this.modes      = modes;
        this.likelihood = likelihood;
    }

    /**
     * Constructor with default values
     *
     * @param modes the modes of the distribution.
     */
    public MultiModalDistribution(final int[] modes) {
        this(modes, .2);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final Random prng) {
        return next(prng, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final Random prng, final int l, final int h) {
        if (prng.nextDouble() < likelihood) { // extreme ?
            int[] feasible = Arrays.stream(modes).filter(x -> x >= l && x <= h).toArray();

            if (feasible.length > 0) {
                int extreme = prng.nextInt(feasible.length);
                return feasible[extreme];
            } else {
                double d = prng.nextDouble();
                return (int) (d * ((long) h - (long) l) + l);
            }
        } else {
            double d = prng.nextDouble();
            return (int) (d * ((long) h - (long) l) + l);
        }
    }
}
