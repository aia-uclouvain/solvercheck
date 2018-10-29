package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Operator;

import java.util.stream.Stream;

public final class UniformOperatorDistribution {

    /** Utility class has no public constructor. */
    private UniformOperatorDistribution() { }

    public static Stream<Operator> stream(final Randomness randomness) {
        return UniformIntDistribution
           .stream(randomness, 0, Operator.values().length)
           .map(i -> Operator.values()[i]);
    }
}
