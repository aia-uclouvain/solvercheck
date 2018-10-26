package be.uclouvain.solvercheck.pbt;

import be.uclouvain.solvercheck.core.data.Operator;

import java.util.stream.Stream;

public final class UniformOperatorDistribution {

    /** Utility class has no public constructor. */
    private UniformOperatorDistribution() { }

    public static Stream<Operator> stream() {
        return UniformIntDistribution.stream(0, Operator.values().length)
           .mapToObj(i -> Operator.values()[i]);
    }
}
