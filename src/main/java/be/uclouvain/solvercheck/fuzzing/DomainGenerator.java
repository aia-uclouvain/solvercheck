package be.uclouvain.solvercheck.fuzzing;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.fuzzing.distribution.Distribution;
import be.uclouvain.solvercheck.fuzzing.distribution.UniformDistribution;

import java.util.HashSet;
import java.util.Set;

public final class DomainGenerator extends BaseGenerator<Domain> {

    private final Distribution szDist;
    private final Distribution valDist;
    private final int szMin;
    private final int szMax;
    private final int valMin;
    private final int valMax;
    private final int spread;

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
        this.szMax   = (int) Math.min(Math.min(szMax, szMin + spread), (long) valMax - (long) valMin);
        this.valMin  = valMin;
        this.valMax  = valMax;
        this.spread  = spread;

        if (szMin > spread) {
            throw new IllegalArgumentException("The minimum size is inconsistent with the spread");
        }
        if (szMin > ((long) valMax - (long) valMin)) {
            throw new IllegalArgumentException("The minimum size is inconsistent with the minimum/maximum values");
        }
    }

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
}
