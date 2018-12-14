package be.uclouvain.solvercheck.fuzzing;

import be.uclouvain.solvercheck.fuzzing.distribution.MultiModalDistribution;

import java.util.ArrayList;

/**
 * Base class for a named generator. Encapsulates the logic relative to the
 * named part of the generator.
 *
 * @param <T> the type of generated object.
 */
public abstract class BaseGenerator<T> implements Generator<T> {
    /**
     * The name (description) associated to the generated values in an error
     * report.
     */
    private final String name;

    /**
     * Creates a new instance with a given name.
     *
     * @param name the name (description) associated to the generated values
     *             in an error report.
     */
    protected BaseGenerator(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public final String name() {
        return name;
    }

    /**
     * Creates a multimodal distribution with the usual values.
     * @param low the lowest possible value
     * @param high the max possible value
     * @return a multimodal distrib w/ the usual modes.
     */
    protected static MultiModalDistribution mkDist(final int low, final int high) {
        ArrayList<Integer> modes = new ArrayList<>();
        modes.add(low);
        modes.add(high);

        if (0 > low && 0 < high) {
            modes.add(0);
        }
        if (1 > low && 1 < high) {
            modes.add(1);
        }

        int[] imodes = modes.stream().mapToInt(Integer::intValue).toArray();

        return new MultiModalDistribution(imodes);
    }
}
