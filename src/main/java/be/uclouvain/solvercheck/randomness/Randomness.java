package be.uclouvain.solvercheck.randomness;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * Randomness is an extension of the standard `Random` class. This extension
 * provides some convenient functionalities, like a memory of the seed in use
 * and the ability to draw a value from a given range
 */
public final class Randomness extends Random {
    /**
     * Because Randomness extends Random which is Serializable, Randomness
     * should declare its serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /** The seed used to initialize the prng. */
    private long seed;

    /**
     * Creates a new instance whose PRNG is initialized with the given seed.
     *
     * @param seed the random seed used to initialize the PRNG.
     */
    public Randomness(final long seed) {
        super(seed);
        this.seed = seed;
    }

    /**
     * Draws a random value from the interval [from, to].
     *
     * @param from the lowerbound (incl) of the possible random values.
     * @param to the upperbound (incl) of the possible random values.
     * @return any int value in the range [from, to].
     */
    public int randomInt(final int from, final int to) {
        return (int) (nextDouble() * ((long) to - (long) from) + from);
    }

    /**
     * Returns a stream of random integer values all drawn from
     * the interval [from, to].
     *
     * @param lowerBound (incl) of the possible random values.
     * @param upperBound (incl) of the possible random values.
     * @return any int value in the range [from, to].
     */
    public IntStream intsBetween(final int lowerBound, final int upperBound) {
        return super
           .longs((long) lowerBound,  1L + (long) upperBound)
           .mapToInt(l -> (int) l);
    }

    /** @return the seed that was used to initialize the PRNG. */
    public long getSeed() {
        return seed;
    }
}
