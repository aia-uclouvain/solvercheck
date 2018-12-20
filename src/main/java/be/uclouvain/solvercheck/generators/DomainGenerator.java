package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.randomness.Distribution;
import be.uclouvain.solvercheck.randomness.Randomness;
import be.uclouvain.solvercheck.randomness.UniformDistribution;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This generator produces streams of random domains. The generated domains are
 * not purely random. The values occurring in the domains follow a multimodal
 * distribution and additional constraints are imposed on the domains. For
 * instance the minimum and maximum values, the maximum distance between any
 * two values of the domain (spread).
 */
public final class DomainGenerator extends BaseGenerator<Domain> {
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
     * @param canBeEmpty may the generated domain be empty (be an error) ?
     * @param szMax the maximum size of any generated domain.
     * @param valMin the minimum value that may occur in any generated domain.
     * @param valMax the maximum value that may occur in any generated domain.
     * @param spread the maximum distance between any two values generated in
     *               a domain.
     */
    @SuppressWarnings("checkstyle:avoidinlineconditionals")
    public DomainGenerator(final String  name,
                           final boolean canBeEmpty,
                           final int     szMax,
                           final int     valMin,
                           final int     valMax,
                           final int     spread) {
        super(name);
        this.valDist = mkDist(valMin, valMax);
        this.szMin   = canBeEmpty ? 0 : 1;
        this.szMax   = (int) min(szMax, szMin + spread, (long) valMax - (long) valMin);
        this.valMin  = valMin;
        this.valMax  = valMax;
        this.spread  = spread;
    }

    /** {@inheritDoc} */
    @Override
    public Domain item(final Randomness randomness) {
        final int size   = uniform().next(randomness, szMin, szMax);
        final int anchor = valDist  .next(randomness, valMin, valMax);

        final int lb = (int) Math.max(valMin, (long) anchor - (long) spread / 2);
        final int ub = (int) Math.min(valMax, (long) anchor + (long) spread / 2);

        return Stream.concat(
           Stream.of(anchor),
           Stream.generate(() -> uniform().next(randomness, lb, ub)).limit(size - 1)
        ).collect(Domain.collector());
    }

    /**
     * @return An uniform distribution.
     */
    private static Distribution uniform() {
        return UniformDistribution.getInstance();
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



    public static void main(String[] args) throws FileNotFoundException {
        Randomness r = new Randomness(System.currentTimeMillis());
        DomainGenerator dg = new DomainGenerator("", false, 5, -100, 100, 10);

        HashMap<Integer, Integer> cnt = new HashMap<>();
        dg.generate(r)
           .limit(10000)
           .forEach(d -> d.stream().forEach(value ->
              cnt.put(value, 1 + cnt.getOrDefault(value, 0))));

        PrintStream o = null;
        try {
            PrintStream out = new PrintStream(new FileOutputStream("/Users/user/Desktop/domains.csv"));
            o = out;

            out.println("Value,Count");
            cnt.entrySet()
               .stream()
               .sorted(Comparator.comparing(Map.Entry::getKey))
               .forEach(e -> out.println(e.getKey() + "," + e.getValue()));
        } finally {
            if (o != null) {
                o.close();
            }
        }
    }

}
