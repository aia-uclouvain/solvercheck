package be.uclouvain.solvercheck.fuzzing;

import be.uclouvain.solvercheck.fuzzing.distribution.Distribution;

public final class IntGenerator extends BaseGenerator<Integer> {

    private final int from;
    private final int to;
    private final Distribution distribution;

    public IntGenerator(final String name, final int from, final int to) {
        super(name);
        this.from = from;
        this.to   = to;
        this.distribution = mkDist(from, to);
    }

    @Override
    public Integer item(final Randomness randomness) {
        return distribution.next(randomness, from, to);
    }
}
