package be.uclouvain.solvercheck.generators;

/**
 * This interface collects all the useful methods that let you plug
 * SolverCheck's DSL to generate CP-related objects into your code.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithGenerators {
    // --------- INTEGERS -----------------------------------------------------
    /**
     * Creates an int generator builder. That is, an object that facilitates
     * the creation of generator of streams of integers.
     *
     * @return an int generator builder.
     */
    default GeneratorsDSL.GenIntBuilder integer() {
        return GeneratorsDSL.integer();
    }
    /**
     * Creates a named int generator builder. That is, an object that
     * facilitates the creation of generator of streams of integers.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @return an int generator builder.
     */
    default GeneratorsDSL.GenIntBuilder integer(final String name) {
        return GeneratorsDSL.integer(name);
    }

    // --------- BOOLEANS -----------------------------------------------------
    /**
     * Creates an boolean generator builder. That is, an object that facilitates
     * the creation of generator of streams of boolean values.
     *
     * @return a boolean generator builder.
     */
    default GeneratorsDSL.GenBoolBuilder bool() {
        return GeneratorsDSL.bool();
    }
    /**
     * Creates a named boolean generator builder. That is, an object that
     * facilitates the creation of generator of streams of boolean values.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @return a boolean generator builder.
     */
    default GeneratorsDSL.GenBoolBuilder bool(final String name) {
        return GeneratorsDSL.bool(name);
    }

    // --------- LISTS --------------------------------------------------------
    /**
     * Creates a list of Ts generator builder. That is, an object that
     * facilitates the creation of streams of lists of T.
     *
     * @param bldr the generator builder used to instantiate the generator of T.
     * @param <T>  the type of the objects constituting the payload of the
     *             generated lists.
     * @return a builder for a generator of lists of T.
     */
    default <T> GeneratorsDSL.GenListBuilder<T> listOf(final GenBuilder<T> bldr) {
        return GeneratorsDSL.listOf(bldr);
    }
    /**
     * Creates a named list of Ts generator builder. That is, an object that
     * facilitates the creation of streams of lists of T.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @param bldr the generator builder used to instanciate the generator of T.
     * @param <T>  the type of the objects constituting the payload of the
     *             generated lists.
     * @return a builder for a generator of lists of T.
     */
    default <T> GeneratorsDSL.GenListBuilder<T> listOf(final String name,
                                                       final GenBuilder<T> bldr) {
        return GeneratorsDSL.listOf(name, bldr);
    }

    // --------- ARRAYS -------------------------------------------------------
    /**
     * Creates an array of Ts generator builder. That is, an object that
     * facilitates the creation of streams of arrays of T.
     *
     * @param clazz the class of the items constituting the payload of the
     *              generated arrays.
     * @param bldr the generator builder used to instantiate the generator of T.
     * @param <T>  the type of the objects constituting the payload of the
     *             generated lists.
     * @return a builder for a generator of arrays of T.
     */
    default <T> GeneratorsDSL.GenArrayBuilder<T> arrayOf(final Class<T> clazz,
                                                               final GenBuilder<T> bldr) {
        return GeneratorsDSL.arrayOf(clazz, bldr);
    }
    /**
     * Creates a named array of Ts generator builder. That is, an object that
     * facilitates the creation of streams of arrays of T.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @param clazz the class of the items constituting the payload of the
     *              generated arrays.
     * @param bldr the generator builder used to instantiate the generator of T.
     * @param <T>  the type of the objects constituting the payload of the
     *             generated arrays.
     * @return a builder for a generator of arrays of T.
     */
    default <T> GeneratorsDSL.GenArrayBuilder<T> arrayOf(final String name,
                                                         final Class<T> clazz,
                                                         final GenBuilder<T> bldr) {
        return GeneratorsDSL.arrayOf(name, clazz, bldr);
    }

    // --------- SETS ---------------------------------------------------------
    /**
     * Creates a setf of Ts generator builder. That is, an object that
     * facilitates the creation of streams of sets of T.
     *
     * @param bldr the generator builder used to instantiate the generator of T.
     * @param <T>  the type of the objects constituting the payload of the
     *             generated sets.
     * @return a builder for a generator of sets of T.
     */
    default <T> GeneratorsDSL.GenSetBuilder<T> setOf(final GenBuilder<T> bldr) {
        return GeneratorsDSL.setOf(bldr);
    }
    /**
     * Creates a setf of Ts generator builder. That is, an object that
     * facilitates the creation of streams of sets of T.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @param bldr the generator builder used to instantiate the generator of T.
     * @param <T>  the type of the objects constituting the payload of the
     *             generated sets.
     * @return a builder for a generator of sets of T.
     */
    default <T> GeneratorsDSL.GenSetBuilder<T> setOf(final String name,
                                                     final GenBuilder<T> bldr) {
        return GeneratorsDSL.setOf(name, bldr);
    }

    // --------- OPERATORS ----------------------------------------------------
    /**
     * Creates an operator generator builder. That is, an object that
     * facilitates the creation of generator of streams of operators.
     *
     * @return  generator pseudo-randomly returns one of the existing operators
     */
    default GeneratorsDSL.GenOperatorBuilder operator() {
        return GeneratorsDSL.operator();
    }
    /**
     * Creates a named operator generator builder. That is, an object that
     * facilitates the creation of generator of streams of operators.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @return  generator pseudo-randomly returns one of the existing operators
     */
    default GeneratorsDSL.GenOperatorBuilder operator(final String name) {
        return GeneratorsDSL.operator(name);
    }

    // --------- DOMAINS ------------------------------------------------------
    /**
     * Creates a domain generator builder. That is, an object that
     * facilitates the creation of generator of streams of domains.
     *
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random domains.
     */
    default GeneratorsDSL.GenDomainBuilder domain() {
        return GeneratorsDSL.domain();
    }
    /**
     * Creates a named domain generator builder. That is, an object that
     * facilitates the creation of generator of streams of domains.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @return a named builder meant to act as a micro DSL to instantiate
     * generators that produce random domains.
     */
    default GeneratorsDSL.GenDomainBuilder domain(final String name) {
        return GeneratorsDSL.domain(name);
    }

    // --------- ASSIGNMENT ---------------------------------------------------
    /**
     * Creates a assignment generator builder. That is, an object that
     * facilitates the creation of generator of streams of assignments
     * (strongly typed lists of integer).
     *
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random assignments.
     */
    default GeneratorsDSL.GenAssignmentBuilder assignment() {
        return GeneratorsDSL.assignment();
    }
    /**
     * Creates a named assignment generator builder. That is, an object that
     * facilitates the creation of generator of streams of assignments
     * (strongly typed lists of integer).
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random assignments.
     */
    default GeneratorsDSL.GenAssignmentBuilder assignment(final String name) {
        return GeneratorsDSL.assignment(name);
    }

    // --------- PARTIAL-ASSIGNMENT -------------------------------------------
    /**
     * Creates a monolithic partial assignment generator builder. That is, an
     * object that facilitates the creation of generator of streams of partial
     * assignments having one single component.
     *
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random partial assignments.
     */
    default GeneratorsDSL.GenMonolithicPartialAssignmentBuilder partialAssignment() {
        return GeneratorsDSL.monolithicPartialAssignment();
    }
    /**
     * Creates a named monolithic partial assignment generator builder.
     * That is, an object that facilitates the creation of generator of streams
     * of partial assignments having one single component.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random partial assignments.
     */
    default GeneratorsDSL.GenMonolithicPartialAssignmentBuilder
                                          partialAssignment(final String name) {
        return GeneratorsDSL.monolithicPartialAssignment(name);
    }
    /**
     * Creates a builder for a generator that produces one unique partial
     * assignment. That is, an object that facilitates the creation of
     * generator of streams of partial assignments possibly having more than one
     * component. The resulting streams all have size one and all yield the one
     * same unique PartialAssignment instance.
     *
     * The point of this generator is simply to concatenate different components
     * (actual values) into a partial assignment.
     *
     * This factory method is mostly useful to implement the
     * `forAnyPartialAssignment` idiom of the Stateless and Stateful assertions.
     *
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random partial assignments.
     */
    default GeneratorsDSL.GenSinglePartialAssignment singlePartialAssignment() {
        return GeneratorsDSL.singlePartialAssignment();
    }
    /**
     * Creates a builder for a generator that produces one unique partial
     * assignment. That is, an object that facilitates the creation of
     * generator of streams of partial assignments possibly having more than one
     * component. The resulting streams all have size one and all yield the one
     * same unique PartialAssignment instance.
     *
     * The point of this generator is simply to concatenate different components
     * (actual values) into a partial assignment.
     *
     * This factory method is mostly useful to implement the
     * `forAnyPartialAssignment` idiom of the Stateless and Stateful assertions.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random partial assignments.
     */
    default GeneratorsDSL.GenSinglePartialAssignment
                                    singlePartialAssignment(final String name) {
        return GeneratorsDSL.singlePartialAssignment(name);
    }
}
