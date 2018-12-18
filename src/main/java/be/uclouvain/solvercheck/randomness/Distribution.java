package be.uclouvain.solvercheck.randomness;

import java.util.Random;

/**
 * Probability distribution. Used to drive a PRNG into a better behaved way.
 */
public interface Distribution {
    /**
     * Generates the next integer distributed somewhere along this distribution.
     *
     * @param prng an initialized source of randomness
     * @return a pseudo random number drawn from the current distribution.
     */
    default int next(Random prng) {
        return next(prng, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Generates the next integer distributed somewhere between the given two
     * bounds in this distribution.
     *
     * @param prng an initialized source of randomness.
     * @param l the lowest possible value.
     * @param h the highest possible value.
     * @return a pseudo random number drawn from the current distribution.
     */
    int next(Random prng, int l, int h);
}
