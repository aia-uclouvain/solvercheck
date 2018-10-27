package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Operator;

import java.util.stream.Stream;

/**
 * This interface collects all the useful methods that let you plug
 * SolverCheck's DSL to generate CP-related objects into your code.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithGenerators {

    default Stream<Integer> integers(final int from, final int to) {
        return GeneratorsDSL.ints(from, to).boxed();
    }

    /**
     * @return a configurable generator meant to produce random
     * `Assignment` instances
     */
    default GeneratorsDSL.GenAssignmentBuilder assignments() {
        return GeneratorsDSL.assignments();
    }

    /**
     * @return a configurable generator meant to produce random
     * `Domain` instances
     */
    default GeneratorsDSL.GenDomainBuilder domains() {
        return GeneratorsDSL.domains();
    }

    /**
     * @return a generator meant to produce random `Operator` instances
     */
    default Stream<Operator> operators() {
        return GeneratorsDSL.operators();
    }

    /**
     * @return a configurable generator meant to produce random
     * `PartialAssignment` instances
     */
    default GeneratorsDSL.GenPartialAssignmentBuilder partialAssignments() {
        return GeneratorsDSL.partialAssignments();
    }

    /**
     * @return a configurable generator meant to produce random tables. That
     * is to say, to produce random lists of `Assignment`.
     */
    default GeneratorsDSL.GenTableBuilder tables() {
        return GeneratorsDSL.tables();
    }

}
