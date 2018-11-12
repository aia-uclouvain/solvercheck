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
    default GeneratorsDSL.GenIntBuilder integers() {
        return GeneratorsDSL.ints();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator (builder) to create int values.
     */
    default GeneratorsDSL.GenIntBuilder integers(final String name) {
        return GeneratorsDSL.ints(name);
    }

    /**
     * @return a generator (builder) to create boolean values.
     */
    default GeneratorsDSL.GenBoolBuilder booleans() {
        return GeneratorsDSL.booleans();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator (builder) to create boolean values.
     */
    default GeneratorsDSL.GenBoolBuilder booleans(final String name) {
        return GeneratorsDSL.booleans(name);
    }

    /**
     * @return a generator (builder) to create lists of int values.
     */
    default GeneratorsDSL.GenListBuilder lists() {
        return GeneratorsDSL.lists();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator (builder) to create lists of int values.
     */
    default GeneratorsDSL.GenListBuilder lists(final String name) {
        return GeneratorsDSL.lists(name);
    }

    /**
     * @return a generator (builder) to create int[] values.
     */
    default GeneratorsDSL.GenArrayBuilder arrays() {
        return GeneratorsDSL.arrays();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator (builder) to create int[] values.
     */
    default GeneratorsDSL.GenArrayBuilder arrays(final String name) {
        return GeneratorsDSL.arrays(name);
    }

    /**
     * @return a generator (builder) to create sets of int values.
     */
    default GeneratorsDSL.GenSetBuilder sets() {
        return GeneratorsDSL.sets();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator (builder) to create sets of int values.
     */
    default GeneratorsDSL.GenSetBuilder sets(final String name) {
        return GeneratorsDSL.sets(name);
    }

    /**
     * @return a generator meant to produce random `Operator` instances
     */
    default GeneratorsDSL.GenOperatorBuilder operators() {
        return GeneratorsDSL.operators();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a generator meant to produce random `Operator` instances
     */
    default GeneratorsDSL.GenOperatorBuilder operators(final String name) {
        return GeneratorsDSL.operators(name);
    }

    /**
     * @return a configurable generator meant to produce random
     * `Domain` instances
     */
    default GeneratorsDSL.GenDomainBuilder domains() {
        return GeneratorsDSL.domains();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a configurable generator meant to produce random
     * `Domain` instances
     */
    default GeneratorsDSL.GenDomainBuilder domains(final String name) {
        return GeneratorsDSL.domains(name);
    }

    /**
     * @return a configurable generator meant to produce random
     * `Assignment` instances
     */
    default GeneratorsDSL.GenAssignmentBuilder assignments() {
        return GeneratorsDSL.assignments();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a configurable generator meant to produce random
     * `Assignment` instances
     */
    default GeneratorsDSL.GenAssignmentBuilder assignments(final String name) {
        return GeneratorsDSL.assignments(name);
    }

    /**
     * @return a configurable generator meant to produce random
     * `PartialAssignment` instances
     */
    default GeneratorsDSL.GenPartialAssignmentBuilder partialAssignments() {
        return GeneratorsDSL.partialAssignments();
    }
    /**
     * @param n the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a configurable generator meant to produce random
     * `PartialAssignment` instances
     */
    default GeneratorsDSL.GenPartialAssignmentBuilder partialAssignments(final String n) {
        return GeneratorsDSL.partialAssignments(n);
    }

    /**
     * @return a configurable generator meant to produce random tables. That
     * is to say, to produce random lists of `Assignment`.
     */
    default GeneratorsDSL.GenTableBuilder tables() {
        return GeneratorsDSL.tables();
    }
    /**
     * @param name the name of the generator. (used to provide meaningful
     *             information in case of error reporting).
     * @return a configurable generator meant to produce random tables. That
     * is to say, to produce random lists of `Assignment`.
     */
    default GeneratorsDSL.GenTableBuilder tables(final String name) {
        return GeneratorsDSL.tables(name);
    }

}
