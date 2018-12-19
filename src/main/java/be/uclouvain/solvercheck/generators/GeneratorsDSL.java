package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.data.impl.BasicPartialAssignment;
import be.uclouvain.solvercheck.randomness.Randomness;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class provides utility method to create generators that will be used
 * to generate fuzzing data in the various test cases.
 */
public final class GeneratorsDSL {

    /** The default minimum number of variables in (partial) assignments. */
    private static final int DEFAULT_NB_VARS_MIN = 0;
    /** The default maximum number of variables in (partial) assignment. */
    private static final int DEFAULT_NB_VARS_MAX = 5;

    /** The default maximum number of values in the domains. */
    private static final int DEFAULT_NB_VAL_MAX  = 10;

    /** The default maximum number of variables in (partial) assignment. */
    private static final int DEFAULT_SPREAD      = 10;

    /** the default minimum value assignable to some variable. */
    private static final int DEFAULT_VALUE_MIN = Integer.MIN_VALUE;
    /** the default maximum value assignable to some variable. */
    private static final int DEFAULT_VALUE_MAX = Integer.MAX_VALUE;

    /**
     * Utility class has no public constructor.
     */
    private GeneratorsDSL() { }

    // --------- INTEGERS -----------------------------------------------------
    /**
     * Creates an int generator builder. That is, an object that facilitates
     * the creation of generator of streams of integers.
     *
     * @return an int generator builder.
     */
    public static GenIntBuilder integer() {
        return integer("Integer");
    }
    /**
     * Creates a named int generator builder. That is, an object that
     * facilitates the creation of generator of streams of integers.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @return an int generator builder.
     */
    public static GenIntBuilder integer(final String name) {
        return new GenIntBuilder(name);
    }

    // --------- BOOLEANS -----------------------------------------------------
    /**
     * Creates an boolean generator builder. That is, an object that facilitates
     * the creation of generator of streams of boolean values.
     *
     * @return a boolean generator builder.
     */
    public static GenBoolBuilder bool() {
        return bool("Bool");
    }
    /**
     * Creates a named boolean generator builder. That is, an object that
     * facilitates the creation of generator of streams of boolean values.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @return a boolean generator builder.
     */
    public static GenBoolBuilder bool(final String name) {
        return new GenBoolBuilder(name);
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
    public static <T> GenListBuilder<T> listOf(final GenBuilder<T> bldr) {
        return listOf("List", bldr);
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
    public static <T> GenListBuilder<T> listOf(final String name,
                                               final GenBuilder<T> bldr) {
        return new GenListBuilder<>(name, bldr);
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
    public static <T> GenArrayBuilder<T> arrayOf(final Class<T> clazz,
                                                 final GenBuilder<T> bldr) {
        return arrayOf("Array", clazz, bldr);
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
    public static <T> GenArrayBuilder<T> arrayOf(final String name,
                                                 final Class<T> clazz,
                                                 final GenBuilder<T> bldr) {
        return new GenArrayBuilder<>(name, clazz, bldr);
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
    public static <T> GenSetBuilder<T> setOf(final GenBuilder<T> bldr) {
        return setOf("Set", bldr);
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
    public static <T> GenSetBuilder<T> setOf(final String name,
                                             final GenBuilder<T> bldr) {
        return new GenSetBuilder<>(name, bldr);
    }

    // --------- OPERATORS ----------------------------------------------------
    /**
     * Creates an operator generator builder. That is, an object that
     * facilitates the creation of generator of streams of operators.
     *
     * @return  generator pseudo-randomly returns one of the existing operators
     */
    public static GenOperatorBuilder operator() {
        return operator("Operator");
    }
    /**
     * Creates a named operator generator builder. That is, an object that
     * facilitates the creation of generator of streams of operators.
     *
     * @param name the name of the generator. (used when reporting a
     *             counter example).
     * @return  generator pseudo-randomly returns one of the existing operators
     */
    public static GenOperatorBuilder operator(final String name) {
        return new GenOperatorBuilder(name);
    }

    // --------- DOMAINS ------------------------------------------------------

    /**
     * Creates a domain generator builder. That is, an object that
     * facilitates the creation of generator of streams of domains.
     *
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random domains.
     */
    public static GenDomainBuilder domain() {
        return domain("Domain");
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
    public static GenDomainBuilder domain(final String name) {
        return new GenDomainBuilder(name);
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
    public static GenAssignmentBuilder assignment() {
        return assignment("Assignment");
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
    public static GenAssignmentBuilder assignment(final String name) {
        return new GenAssignmentBuilder(name);
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
    public static GenMonolithicPartialAssignmentBuilder monolithicPartialAssignment() {
        return monolithicPartialAssignment("Partial Assignment");
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
    public static GenMonolithicPartialAssignmentBuilder
                                monolithicPartialAssignment(final String name) {
        return new GenMonolithicPartialAssignmentBuilder(name);
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
    public static GenSinglePartialAssignment singlePartialAssignment() {
        return singlePartialAssignment("Partial Assignment");
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
    public static GenSinglePartialAssignment singlePartialAssignment(final String name) {
        return new GenSinglePartialAssignment(name);
    }

    // --------- BUILDERS -----------------------------------------------------

    /**
     * A builder which acts as a micro DSL to produce generators of integers.
     */
    public static final class GenIntBuilder extends GenBuilder<Integer> {
        /** The minimum value that can be generated. */
        private int low  = Integer.MIN_VALUE;
        /** The maximum value that can be generated. */
        private int high = Integer.MAX_VALUE;

        /**
         * Constructor with a name.
         *
         * @param name the name of the generator. (used when reporting a
         *             counter example).
         */
        public GenIntBuilder(final String name) {
            super(name);
        }

        /**
         * Specifies the range of values which may be generated.
         *
         * @param low the minimum value that can be generated (inclusive).
         * @param high the maximum value that can be generated (inclusive).
         * @return this.
         */
        @SuppressWarnings("checkstyle:hiddenfield")
        public GenIntBuilder between(final int low, final int high) {
            this.low  = low;
            this.high = high;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Generator<Integer> build() {
            return new IntGenerator(name(), low, high);
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of booleans.
     */
    public static final class GenBoolBuilder extends GenBuilder<Boolean> {
        /**
         * Constructor with a name.
         *
         * @param name the name of the generator. (used when reporting a
         *             counter example).
         */
        public GenBoolBuilder(final String name) {
            super(name);
        }

        /** {@inheritDoc} */
        @Override
        public Generator<Boolean> build() {
            return new BooleanGenerator(name());
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of operators.
     */
    public static final class GenOperatorBuilder extends GenBuilder<Operator> {
        /**
         * Constructor with a name.
         *
         * @param name the name of the generator. (used when reporting a
         *             counter example).
         */
        public GenOperatorBuilder(final String name) {
            super(name);
        }

        /** {@inheritDoc} */
        @Override
        public Generator<Operator> build() {
            return new OperatorGenerator(name());
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of lists of
     * objects.
     *
     * @param <T> the kind if items in the generated lists.
     */
    public static final class GenListBuilder<T> extends GenBuilder<List<T>> {
        /**
         * The minimum number of items in the list. (default: 0)
         */
        private int nbItemsMin = DEFAULT_NB_VARS_MIN;
        /**
         * The maximum number of items in the list. (default: 5)
         */
        private int nbItemsMax = DEFAULT_NB_VARS_MAX;
        /**
         * The generator that actually produces the items.
         */
        private Generator<T> generator;

        /**
         * Contructor with a name.
         *
         * @param name the name of the generator
         * @param bldr a builder to create the delegate generator
         */
        public GenListBuilder(final String name, final GenBuilder<T> bldr) {
            super(name);
            generator = bldr.build();
        }

        /**
         * Contructor with a name.
         *
         * @param name the name of the generator
         * @param gen the delegate generator
         */
        public GenListBuilder(final String name, final Generator<T> gen) {
            super(name);
            generator = gen;
        }

        /**
         * Tells that generator will produce lists having exactly n items.
         *
         * @param n the number of items in the generated list.
         * @return this
         */
        public GenListBuilder<T> ofSize(final int n) {
            this.nbItemsMin = n;
            this.nbItemsMax = n;
            return this;
        }
        /**
         * Tells that generator will produce lists having &lt;= n items.
         *
         * @param n the maximum number of items in the generated list
         * @return this
         */
        public GenListBuilder<T> ofSizeUpTo(final int n) {
            this.nbItemsMin = 0;
            this.nbItemsMax = n;
            return this;
        }
        /**
         * Tells that generator will produce lists having between from
         * and to items.
         *
         * @param from a lower bound on the number of items in the list.
         * @param to an upper bound on the number of items in the list.
         *
         * @return this
         */
        public GenListBuilder<T> ofSizeBetween(final int from, final int to) {
            this.nbItemsMin = from;
            this.nbItemsMax = to;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @return an assignment generator that corresponds to the
         * configuration specified with the other methods
         */
        @Override
        public Generator<List<T>> build() {
            return new ListGenerator<T>(name(), generator, nbItemsMin, nbItemsMax);
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of arrays of
     * objects.
     *
     * @param <T> the kind if items in the generated arrays.
     */
    public static final class GenArrayBuilder<T> extends GenBuilder<T[]> {
        /**
         * The minimum number of items in the list. (default: 0)
         */
        private int nbItemsMin = DEFAULT_NB_VARS_MIN;
        /**
         * The maximum number of items in the list. (default: 5)
         */
        private int nbItemsMax = DEFAULT_NB_VARS_MAX;
        /**
         * The class of the items being generated.
         */
        private Class<T> clazz;
        /**
         * The generator that actually produces the items.
         */
        private Generator<T> generator;

        /**
         * Contructor with a name.
         *
         * @param name the name of the generator.
         * @param clazz the class of the items being generated.
         * @param bldr a builder to create the delegate generator.
         */
        public GenArrayBuilder(final String name,
                               final Class<T> clazz,
                               final GenBuilder<T> bldr) {
            super(name);
            this.clazz = clazz;
            this.generator = bldr.build();
        }

        /**
         * Contructor with a name.
         *
         * @param name the name of the generator.
         * @param clazz the class of the items being generated.
         * @param gen the delegate generator.
         */
        public GenArrayBuilder(final String name,
                               final Class<T> clazz,
                               final Generator<T> gen) {
            super(name);
            this.clazz = clazz;
            this.generator = gen;
        }

        /**
         * Tells that generator will produce lists having exactly n items.
         *
         * @param n the number of items in the generated list.
         * @return this
         */
        public GenArrayBuilder<T> ofSize(final int n) {
            this.nbItemsMin = n;
            this.nbItemsMax = n;
            return this;
        }
        /**
         * Tells that generator will produce lists having &lt;= n items.
         *
         * @param n the maximum number of items in the generated list
         * @return this
         */
        public GenArrayBuilder<T> ofSizeUpTo(final int n) {
            this.nbItemsMin = 0;
            this.nbItemsMax = n;
            return this;
        }
        /**
         * Tells that generator will produce lists having between from
         * and to items.
         *
         * @param from a lower bound on the number of items in the list.
         * @param to an upper bound on the number of items in the list.
         *
         * @return this
         */
        public GenArrayBuilder<T> ofSizeBetween(final int from, final int to) {
            this.nbItemsMin = from;
            this.nbItemsMax = to;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @return an assignment generator that corresponds to the
         * configuration specified with the other methods
         */
        @Override
        public Generator<T[]> build() {
            return new ArrayGenerator<>(name(), generator, clazz, nbItemsMin, nbItemsMax);
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of sets of
     * objects.
     *
     * @param <T> the kind if items in the generated sets.
     */
    public static final class GenSetBuilder<T> extends GenBuilder<Set<T>> {
        /**
         * Is it allowed to generated an empty set ? (default: false)
         */
        private boolean canBeEmpty = false;
        /**
         * The maximum number of items in the list. (default: 5)
         */
        private int nbItemsMax = DEFAULT_NB_VARS_MAX;
        /**
         * The generator that actually produces the items.
         */
        private Generator<T> generator;

        /**
         * Contructor with a name.
         *
         * @param name the name of the generator
         * @param bldr a builder to create the delegate generator
         */
        public GenSetBuilder(final String name, final GenBuilder<T> bldr) {
            super(name);
            generator = bldr.build();
        }

        /**
         * Contructor with a name.
         *
         * @param name the name of the generator
         * @param gen the delegate generator
         */
        public GenSetBuilder(final String name, final Generator<T> gen) {
            super(name);
            generator = gen;
        }

        /**
         * Tells that generator will produce lists having &lt;= n items.
         *
         * @param n the maximum number of items in the generated list
         * @return this
         */
        public GenSetBuilder<T> ofSizeUpTo(final int n) {
            this.nbItemsMax = n;
            return this;
        }

        /**
         * Tells that the generated set can possibly be empty.
         *
         * @return this
         */
        public GenSetBuilder<T> possiblyEmpty() {
            this.canBeEmpty = true;
            return this;
        }


        /**
         * {@inheritDoc}
         *
         * @return an assignment generator that corresponds to the
         * configuration specified with the other methods
         */
        @Override
        public Generator<Set<T>> build() {
            return new SetGenerator<>(name(), generator, canBeEmpty, nbItemsMax);
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of domains.
     */
    public static final class GenDomainBuilder extends GenBuilder<Domain> {
        /** the minimum number of different values a domain should contain. */
        private int nbValMin = 1;
        /** the maximum number of different values a domain should contain. */
        private int nbValMax = DEFAULT_NB_VAL_MAX;
        /** the lowest value that can be contained in the domain. */
        private int minValue = DEFAULT_VALUE_MIN;
        /** the highest value that can be contained in the domain. */
        private int maxValue = DEFAULT_VALUE_MAX;
        /** the highest value that can be contained in the domain. */
        private int spread   = DEFAULT_SPREAD;

        /**
         * Contructor with a name.
         *
         * @param name the name of the generator
         */
        public GenDomainBuilder(final String name) {
            super(name);
        }

        /**
         * Tells that generator will produce lists having exactly n items.
         *
         * @param n the number of items in the generated list.
         * @return this
         */
        public GenDomainBuilder ofSize(final int n) {
            this.nbValMin = n;
            this.nbValMax = n;
            return this;
        }
        /**
         * Tells that generator will produce lists having &lt;= n items.
         *
         * @param n the maximum number of items in the generated list
         * @return this
         */
        public GenDomainBuilder ofSizeUpTo(final int n) {
            this.nbValMin = 1;
            this.nbValMax = n;
            return this;
        }
        /**
         * Tells that generator will produce lists having between from
         * and to items.
         *
         * @param from a lower bound on the number of items in the list.
         * @param to an upper bound on the number of items in the list.
         *
         * @return this
         */
        public GenDomainBuilder ofSizeBetween(final int from, final int to) {
            this.nbValMin = from;
            this.nbValMax = to;
            return this;
        }

        /**
         * Sets the values range of the generated domains.
         *
         * @param from the minimum value that a generated domain can hold
         * @param to the maximum value that a generated domain can hold
         * @return this
         */
        public GenDomainBuilder withValuesBetween(final int from, final int to) {
            minValue = from;
            maxValue = to;

            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Generator<Domain> build() {
          return new DomainGenerator(name(), nbValMin, nbValMax, minValue, maxValue, spread);
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of assignments.
     */
    public static final class GenAssignmentBuilder
       extends GenBuilder<Assignment> {

        /**
         * The minimum number of variables in the assignment. (default: 0)
         */
        private int nbVarsMin = DEFAULT_NB_VARS_MIN;
        /**
         * The maximum number of variables in the assignment. (default: 5)
         */
        private int nbVarsMax = DEFAULT_NB_VARS_MAX;
        /**
         * The minimum value of a variable in the assignment. (default: -10)
         */
        private int valueMin  = DEFAULT_VALUE_MIN;
        /**
         * The maximum value of a variable in the assignment. (default: 10)
         */
        private int valueMax  = DEFAULT_VALUE_MAX;

        /**
         * Contructor with a name.
         *
         * @param name the name of the generator
         */
        public GenAssignmentBuilder(final String name) {
            super(name);
        }

        /**
         * Tells that generator will produce assignments having exactly n
         * variables.
         *
         * @param n the number of variables in the generated assignments
         * @return this
         */
        public GenAssignmentBuilder withVariables(final int n) {
            this.nbVarsMin = n;
            this.nbVarsMax = n;
            return this;
        }
        /**
         * Tells that generator will produce assignments having &lt;= n
         * variables.
         *
         * @param n the maximum number of variables in the generated assignments
         * @return this
         */
        public GenAssignmentBuilder withUpToVariables(final int n) {
            this.nbVarsMin = 0;
            this.nbVarsMax = n;
            return this;
        }
        /**
         * Tells that generator will produce assignments having between from
         * and to variables.
         *
         * @param from a lower bound on the number of variables in the
         *             generated assignments
         * @param to an upper bound on the number of variables in the generated
         *          assignments
         * @return this
         */
        public GenAssignmentBuilder withVariablesBetween(
           final int from,
           final int to) {
            this.nbVarsMin = from;
            this.nbVarsMax = to;
            return this;
        }
        /**
         * Tells that in the generated assignments, the values assigned to
         * variables, will range between `from` and `to`.
         *
         * @param from a lower bound on the value of variables in the
         *             generated assignments
         * @param to an upper bound on the value of variables in the generated
         *          assignments
         * @return this
         */
        public GenAssignmentBuilder withValuesRanging(
           final int from,
           final int to) {
            this.valueMin = from;
            this.valueMax = to;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @return an assignment generator that corresponds to the
         * configuration specified with the other methods
         */
        @Override
        public Generator<Assignment> build() {
            return new AssignmentGenerator(name(), nbVarsMin, nbVarsMax, valueMin, valueMax);
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of monolithic
     * partial assignments. That is generators of partial assignments having
     * one single component.
     */
    public static final class GenMonolithicPartialAssignmentBuilder
            extends GenBuilder<PartialAssignment> {
        /**
         * The minimum number of variables in the partial assignment.
         * (default: 0)
         */
        private int nbVarsMin = DEFAULT_NB_VARS_MIN;
        /**
         * The maximum number of variables in the partial assignment.
         * (default: 5)
         */
        private int nbVarsMax = DEFAULT_NB_VARS_MAX;
        /** the maximum number of different values a domain should contain. */
        private int domSzMax = DEFAULT_NB_VAL_MAX;
        /** the lowest value that can be contained in the domain. */
        private int minValue = DEFAULT_VALUE_MIN;
        /** the highest value that can be contained in the domain. */
        private int maxValue = DEFAULT_VALUE_MAX;
        /** the max distance between any two values. */
        private int spread   = DEFAULT_SPREAD;

        /**
         * Contructor with a name.
         *
         * @param name the name of the generator
         */
        public GenMonolithicPartialAssignmentBuilder(final String name) {
            super(name);
        }

        /**
         * Tells that generator will produce partial assignments having
         * exactly n variables.
         *
         * @param n the number of variables in the generated partial assignments
         * @return this
         */
        public GenMonolithicPartialAssignmentBuilder withVariables(final int n) {
            this.nbVarsMin = n;
            this.nbVarsMax = n;
            return this;
        }

        /**
         * Tells that generator will produce partial assignments having
         * &lt;= n variables.
         *
         * @param n an upper bound on the number of variables in the generated
         *          partial assignments
         * @return this
         */
        public GenMonolithicPartialAssignmentBuilder withUpToVariables(final int n) {
            this.nbVarsMin = 0;
            this.nbVarsMax = n;
            return this;
        }
        /**
         * Tells that generator will produce partial assignments having
         * between from and to variables.
         *
         * @param from a lower bound on the number of variables in the
         *             generated partial assignments
         * @param to an upper bound on the number of variables in the generated
         *          partial assignments
         * @return this
         */
        public GenMonolithicPartialAssignmentBuilder withVariablesBetween(
                final int from,
                final int to) {
            this.nbVarsMin = from;
            this.nbVarsMax = to;
            return this;
        }

        /**
         * Sets the size range of the generated domains.
         *
         * @param n an upper bound on the size of the generated domains in the
         *           partial assignments
         * @return this
         */
        public GenMonolithicPartialAssignmentBuilder withDomainsOfSizeUpTo(final int n) {
            this.domSzMax = n;
            return this;
        }

        /**
         * Sets the value range of the generated domains.
         * @param from a lower bound on the values of the generated domains in
         *            the partial assignments
         * @param to an upper bound on the values of the generated domains in
         *          the partial assignments
         * @return this
         */
        public GenMonolithicPartialAssignmentBuilder withValuesRanging(
           final int from,
           final int to) {
            this.minValue = from;
            this.maxValue = to;
            return this;
        }

        /**
         * Sets the maximum spread for any generated domain.
         * @param s the new spread
         * @return this
         */
        public GenMonolithicPartialAssignmentBuilder spreading(final int s) {
            this.spread = s;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Generator<PartialAssignment> build() {
            Generator<Domain> domains =
               new DomainGenerator("", 1, domSzMax, minValue, maxValue, spread);
            return new PartialAssignmentGenerator(name())
               .addListComponent(new ListGenerator<>(domains, nbVarsMin, nbVarsMax));
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of partial
     * assignments which may have more than one component. Because the utility
     * methods of this builder specify the actual values of each component
     * of the partial assignment, a generator created with this builder
     * will always generate streams of size ONE. That is, it only generates
     * ONE SINGLE PARTIAL ASSIGNMENT INSTANCE.
     *
     * This builder is mostly useful to implement the "forAnyPartialAssignment"
     * idiom on the Stateless- and Stateful- assertions.
     */
    public static final class GenSinglePartialAssignment
       extends GenBuilder<PartialAssignment> {

        /** The actual instance being generated. */
        private final BasicPartialAssignment instance;

        /**
         * Creates a new Generator builder with the given name.
         *
         * @param name the name (description) of the items to be generated. This
         *             is useful to be able to provide a meaningful information
         *             in the error reports.
         */
        public GenSinglePartialAssignment(final String name) {
            super(name);
            instance = new BasicPartialAssignment();
        }

        /**
         * Adds a component, a list of arguments to the partial assignment.
         * One such component is really meant to represent a set of arguments passed
         * to the constructor of an actual constraint. The element constraint is
         * a typical example that illustrates this. The element constraint
         * `X[Y] = Z` has three components:
         * <ul>
         *     <li>X, an array of variables</li>
         *     <li>Y, an index variable</li>
         *     <li>Z, a value variable</li>
         * </ul>
         *
         * A partial assignment generator for the element constraint can thus be
         * configured with:
         * <code>
         *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
         *  pa.addListComponent(x);
         *  pa.addSingleComponent(y);
         *  pa.addSingleComponent(z);
         * </code>
         *
         * @param component the component to add to the current partial assignment.
         * @return this
         */
        public GenSinglePartialAssignment with(final List<Domain> component) {
            instance.addComponent(component);
            return this;
        }

        /**
         * Adds a component, a list of arguments to the partial assignment.
         * One such component is really meant to represent a set of arguments passed
         * to the constructor of an actual constraint. The element constraint is
         * a typical example that illustrates this. The element constraint
         * `X[Y] = Z` has three components:
         * <ul>
         *     <li>X, an array of variables</li>
         *     <li>Y, an index variable</li>
         *     <li>Z, a value variable</li>
         * </ul>
         *
         * A partial assignment generator for the element constraint can thus be
         * configured with:
         * <code>
         *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
         *  pa.addListComponent(x);
         *  pa.addSingleComponent(y);
         *  pa.addSingleComponent(z);
         * </code>
         *
         * @param component the component to add to the current partial assignment.
         * @return this
         */
        public GenSinglePartialAssignment with(final Domain[] component) {
            instance.addComponent(component);
            return this;
        }

        /**
         * Adds a component, a list of arguments to the partial assignment.
         * One such component is really meant to represent a set of arguments passed
         * to the constructor of an actual constraint. The element constraint is
         * a typical example that illustrates this. The element constraint
         * `X[Y] = Z` has three components:
         * <ul>
         *     <li>X, an array of variables</li>
         *     <li>Y, an index variable</li>
         *     <li>Z, a value variable</li>
         * </ul>
         *
         * A partial assignment generator for the element constraint can thus be
         * configured with:
         * <code>
         *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
         *  pa.addListComponent(x);
         *  pa.addSingleComponent(y);
         *  pa.addSingleComponent(z);
         * </code>
         *
         * @param component the component to add to the current partial assignment.
         * @return this
         */
        public GenSinglePartialAssignment with(final Domain component) {
            instance.addComponent(component);
            return this;
        }

        /**
         * Adds a component, a list of arguments to the partial assignment.
         * One such component is really meant to represent a set of arguments passed
         * to the constructor of an actual constraint. The element constraint is
         * a typical example that illustrates this. The element constraint
         * `X[Y] = Z` has three components:
         * <ul>
         *     <li>X, an array of variables</li>
         *     <li>Y, an index variable</li>
         *     <li>Z, a value variable</li>
         * </ul>
         *
         * A partial assignment generator for the element constraint can thus be
         * configured with:
         * <code>
         *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
         *  pa.addListComponent(x);
         *  pa.addSingleComponent(y);
         *  pa.addSingleComponent(z);
         * </code>
         *
         * @param component the component to add to the current partial assignment.
         * @return this
         */
        public GenSinglePartialAssignment with(final int component) {
            instance.addComponent(component);
            return this;
        }

        /**
         * Adds a component, a list of arguments to the partial assignment.
         * One such component is really meant to represent a set of arguments passed
         * to the constructor of an actual constraint. The element constraint is
         * a typical example that illustrates this. The element constraint
         * `X[Y] = Z` has three components:
         * <ul>
         *     <li>X, an array of variables</li>
         *     <li>Y, an index variable</li>
         *     <li>Z, a value variable</li>
         * </ul>
         *
         * A partial assignment generator for the element constraint can thus be
         * configured with:
         * <code>
         *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
         *  pa.addListComponent(x);
         *  pa.addSingleComponent(y);
         *  pa.addSingleComponent(z);
         * </code>
         *
         * @param component the component to add to the current partial assignment.
         * @return this
         */
        public GenSinglePartialAssignment then(final List<Domain> component) {
            instance.addComponent(component);
            return this;
        }

        /**
         * Adds a component, a list of arguments to the partial assignment.
         * One such component is really meant to represent a set of arguments passed
         * to the constructor of an actual constraint. The element constraint is
         * a typical example that illustrates this. The element constraint
         * `X[Y] = Z` has three components:
         * <ul>
         *     <li>X, an array of variables</li>
         *     <li>Y, an index variable</li>
         *     <li>Z, a value variable</li>
         * </ul>
         *
         * A partial assignment generator for the element constraint can thus be
         * configured with:
         * <code>
         *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
         *  pa.addListComponent(x);
         *  pa.addSingleComponent(y);
         *  pa.addSingleComponent(z);
         * </code>
         *
         * @param component the component to add to the current partial assignment.
         * @return this
         */
        public GenSinglePartialAssignment then(final Domain[] component) {
            instance.addComponent(component);
            return this;
        }

        /**
         * Adds a component, a list of arguments to the partial assignment.
         * One such component is really meant to represent a set of arguments passed
         * to the constructor of an actual constraint. The element constraint is
         * a typical example that illustrates this. The element constraint
         * `X[Y] = Z` has three components:
         * <ul>
         *     <li>X, an array of variables</li>
         *     <li>Y, an index variable</li>
         *     <li>Z, a value variable</li>
         * </ul>
         *
         * A partial assignment generator for the element constraint can thus be
         * configured with:
         * <code>
         *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
         *  pa.addListComponent(x);
         *  pa.addSingleComponent(y);
         *  pa.addSingleComponent(z);
         * </code>
         *
         * @param component the component to add to the current partial assignment.
         * @return this
         */
        public GenSinglePartialAssignment then(final Domain component) {
            instance.addComponent(component);
            return this;
        }

        /**
         * Adds a component, a list of arguments to the partial assignment.
         * One such component is really meant to represent a set of arguments passed
         * to the constructor of an actual constraint. The element constraint is
         * a typical example that illustrates this. The element constraint
         * `X[Y] = Z` has three components:
         * <ul>
         *     <li>X, an array of variables</li>
         *     <li>Y, an index variable</li>
         *     <li>Z, a value variable</li>
         * </ul>
         *
         * A partial assignment generator for the element constraint can thus be
         * configured with:
         * <code>
         *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
         *  pa.addListComponent(x);
         *  pa.addSingleComponent(y);
         *  pa.addSingleComponent(z);
         * </code>
         *
         * @param component the component to add to the current partial assignment.
         * @return this
         */
        public GenSinglePartialAssignment then(final int component) {
            instance.addComponent(component);
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Generator<PartialAssignment> build() {
            return new BaseGenerator<PartialAssignment>(name()) {
                /** {@inheritDoc} */
                @Override
                public Stream<PartialAssignment> generate(final Randomness randomness) {
                    return Stream.of(item(randomness));
                }

                /** {@inheritDoc} */
                @Override
                public PartialAssignment item(final Randomness randomness) {
                    return instance;
                }
            };
        }
    }
}
