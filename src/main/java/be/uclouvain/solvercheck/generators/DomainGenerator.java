package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.randomness.Randomness;
import be.uclouvain.solvercheck.randomness.Distribution;
import be.uclouvain.solvercheck.randomness.UniformDistribution;

import java.util.HashSet;
import java.util.Set;

/**
 * This generator produces streams of random domains. The generated domains are
 * not purely random. The values occurring in the domains follow a multimodal
 * distribution and additional constraints are imposed on the domains. For
 * instance the minimum and maximum values, the maximum distance between any
 * two values of the domain (spread).
 */
public final class DomainGenerator extends BaseGenerator<Domain> {
    /** The distribution of the *size* of the generated domains. */
    private final Distribution szDist;
    /** The distribution of the values generated in the domains. */
    private final Distribution valDist;
    /** The minimum size of any generated domain. */
    private final int szMin;
    /** The maximum size of any generated domain. */
    private final int szMax;
    /** The minimum value that may occur in any generated domain. */
    private final int valMin;
    /** The maximum value that may occur in any generated domain. */
    private final int valMax;
    /** The maximum distance between any two values generated in a domain. */
    private final int spread;

    /**
     * A constructor that describes all the constraints imposed on the
     * generated domains.
     *
     * @param name the name of the generated domains.
     * @param szMin the minimum size of any generated domain.
     * @param szMax the maximum size of any generated domain.
     * @param valMin the minimum value that may occur in any generated domain.
     * @param valMax the maximum value that may occur in any generated domain.
     * @param spread the maximum distance between any two values generated in
     *               a domain.
     */
    public DomainGenerator(final String  name,
                           final int     szMin,
                           final int     szMax,
                           final int     valMin,
                           final int     valMax,
                           final int     spread) {
        super(name);
        this.szDist  = UniformDistribution.getInstance();
        this.valDist = mkDist(valMin, valMax);
        this.szMin   = szMin;
        this.szMax   = (int) min(szMax, szMin + spread, (long) valMax - (long) valMin);
        this.valMin  = valMin;
        this.valMax  = valMax;
        this.spread  = spread;

        if (szMin > spread) {
          throw new IllegalArgumentException(
            "The minimum size is inconsistent with the spread");
        }
        if (szMin > ((long) valMax - (long) valMin)) {
          throw new IllegalArgumentException(
            "The minimum size is inconsistent with the minimum/maximum values");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Domain item(final Randomness randomness) {
        final int size  = szDist.next(randomness, szMin, szMax);

        long minimum = Integer.MAX_VALUE;
        long maximum = Integer.MIN_VALUE;
        final Set<Integer> set = new HashSet<>();
        while (set.size()  < size) {
            if (set.isEmpty()) {
                int i = valDist.next(randomness, valMin, valMax);
                minimum = i;
                maximum = i;
                set.add(i);
            } else {
                long rem = spread - (maximum - minimum);
                int ll   = (int) Math.max(Integer.MIN_VALUE, minimum - rem);
                int mm   = (int) Math.min(Integer.MAX_VALUE, maximum + rem);
                int i = valDist.next(randomness, ll, mm);

                if (i > maximum) {
                    maximum = i;
                }
                if (i < minimum) {
                    minimum = i;
                }
                set.add(i);
            }
        }

        return Domain.from(set);
    }

    /**
     * Returns the minimum of three values.
     *
     * @param x the first value
     * @param y the 2nd value
     * @param z the third value
     * @return the smallest of the three value.
     */
    private static long min(final long x, final long y, final long z) {
        return Math.min(x, Math.min(y, z));
    }
}
