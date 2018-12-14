package be.uclouvain.solvercheck.generators;

/**
 * This interface collects all the useful methods that let you plug
 * SolverCheck's DSL to generate CP-related objects into your code.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithGenerators {

    /**
     * @return a generator (builder) to create int values.
     */
    default GeneratorsDSL.GenIntBuilder integer() {
        return GeneratorsDSL.ints();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator (builder) to create int values.
     */
    default GeneratorsDSL.GenIntBuilder integer(final String name) {
        return GeneratorsDSL.ints(name);
    }

    /**
     * @return a generator (builder) to create boolean values.
     */
    default GeneratorsDSL.GenBoolBuilder bool() {
        return GeneratorsDSL.booleans();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator (builder) to create boolean values.
     */
    default GeneratorsDSL.GenBoolBuilder bool(final String name) {
        return GeneratorsDSL.booleans(name);
    }

    /**
     * @return a generator (builder) to create lists of int values.
     */
    default GeneratorsDSL.GenListBuilder list() {
        return GeneratorsDSL.lists();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator (builder) to create lists of int values.
     */
    default GeneratorsDSL.GenListBuilder list(final String name) {
        return GeneratorsDSL.lists(name);
    }

    /**
     * @return a generator (builder) to create int[] values.
     */
    default GeneratorsDSL.GenArrayBuilder array() {
        return GeneratorsDSL.arrays();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator (builder) to create int[] values.
     */
    default GeneratorsDSL.GenArrayBuilder array(final String name) {
        return GeneratorsDSL.arrays(name);
    }

    /**
     * @return a generator (builder) to create sets of int values.
     */
    default GeneratorsDSL.GenSetBuilder set() {
        return GeneratorsDSL.sets();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator (builder) to create sets of int values.
     */
    default GeneratorsDSL.GenSetBuilder set(final String name) {
        return GeneratorsDSL.sets(name);
    }

    /**
     * @return a generator meant to produce random `Operator` instances
     */
    default GeneratorsDSL.GenOperatorBuilder operator() {
        return GeneratorsDSL.operators();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator meant to produce random `Operator` instances
     */
    default GeneratorsDSL.GenOperatorBuilder operator(final String name) {
        return GeneratorsDSL.operators(name);
    }

    /**
     * @return a configurable generator meant to produce random
     * `Domain` instances
     */
    default GeneratorsDSL.GenDomainBuilder domain() {
        return GeneratorsDSL.domains();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a configurable generator meant to produce random
     * `Domain` instances
     */
    default GeneratorsDSL.GenDomainBuilder domain(final String name) {
        return GeneratorsDSL.domains(name);
    }

    /**
     * @return a configurable generator meant to produce random
     * `Assignment` instances
     */
    default GeneratorsDSL.GenAssignmentBuilder assignment() {
        return GeneratorsDSL.assignments();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a configurable generator meant to produce random
     * `Assignment` instances
     */
    default GeneratorsDSL.GenAssignmentBuilder assignment(final String name) {
        return GeneratorsDSL.assignments(name);
    }

    /**
     * @return a configurable generator meant to produce random
     * `PartialAssignment` instances
     */
    default GeneratorsDSL.GenSimplePartialAssignmentBuilder simplePartialAssignment() {
        return GeneratorsDSL.simplePartialAssignments();
    }
    /**
     * @param n the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a configurable generator meant to produce random
     * `PartialAssignment` instances
     */
    default GeneratorsDSL.GenSimplePartialAssignmentBuilder simplePartialAssignment(final String n) {
        return GeneratorsDSL.simplePartialAssignments(n);
    }

    default GeneratorsDSL.GenCompoundPartialAssignment partialAssignment() {
        return GeneratorsDSL.partialAssignment();
    }

    default GeneratorsDSL.GenCompoundPartialAssignment partialAssignment(final String n) {
        return GeneratorsDSL.partialAssignment(n);
    }
}
