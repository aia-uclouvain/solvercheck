package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import org.quicktheories.core.Gen;

import java.util.List;

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
    default Gen<Assignment> assignements() {
        return Generators.assignments();
    }

    /**
     * @return a configurable generator meant to produce random
     * `Domain` instances
     */
    default Gen<Domain> domains() {
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
    default Gen<PartialAssignment> partialAssignments() {
        return Generators.partialAssignments();
    }

    /**
     * @return a configurable generator meant to produce random tables. That
     * is to say, to produce random lists of `Assignment`.
     */
    default Gen<List<Assignment>> tables() {
        return Generators.tables();
    }

}
