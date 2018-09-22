package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Operator;
import org.quicktheories.core.Gen;

/**
 * This interface collects all the useful methods that let you plug
 * SolverCheck's DSL to generate CP-related objects into your code.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithCpGenerators {

    /**
     * @return a configurable generator meant to produce random
     * `Assignment` instances
     */
    default Generators.GenAssignmentBuilder assignments() {
        return Generators.assignments();
    }

    /**
     * @return a configurable generator meant to produce random
     * `Domain` instances
     */
    default Generators.GenDomainBuilder domains() {
        return Generators.domains();
    }

    /**
     * @return a generator meant to produce random `Operator` instances
     */
    default Gen<Operator> operators() {
        return Generators.operators();
    }

    /**
     * @return a configurable generator meant to produce random
     * `PartialAssignment` instances
     */
    default Generators.GenPartialAssignmentBuilder partialAssignments() {
        return Generators.partialAssignments();
    }

    /**
     * @return a configurable generator meant to produce random tables. That
     * is to say, to produce random lists of `Assignment`.
     */
    default Generators.GenTableBuilder tables() {
        return Generators.tables();
    }

}
