package be.uclouvain.solvercheck.generators;

/**
 * This interface collects all the useful methods that let you plug
 * SolverCheck's DSL to generate CP-related objects into your code.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithGenerators {

    default GeneratorsDSL.GenIntBuilder integers() {
        return GeneratorsDSL.ints();
    }
    default GeneratorsDSL.GenIntBuilder integers(final String name) {
        return GeneratorsDSL.ints(name);
    }

    default GeneratorsDSL.GenBoolBuilder booleans() {
        return GeneratorsDSL.booleans();
    }
    default GeneratorsDSL.GenBoolBuilder booleans(final String name) {
        return GeneratorsDSL.booleans(name);
    }
    default GeneratorsDSL.GenListBuilder lists() {
        return GeneratorsDSL.lists();
    }
    default GeneratorsDSL.GenListBuilder lists(final String name) {
        return GeneratorsDSL.lists(name);
    }
    default GeneratorsDSL.GenArrayBuilder arrays() {
        return GeneratorsDSL.arrays();
    }
    default GeneratorsDSL.GenArrayBuilder arrays(final String name) {
        return GeneratorsDSL.arrays(name);
    }
    /**
     * @return a generator meant to produce random `Operator` instances
     */
    default GeneratorsDSL.GenOperatorBuilder operators() {
        return GeneratorsDSL.operators();
    }
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
    default GeneratorsDSL.GenTableBuilder tables(final String name) {
        return GeneratorsDSL.tables(name);
    }

}
