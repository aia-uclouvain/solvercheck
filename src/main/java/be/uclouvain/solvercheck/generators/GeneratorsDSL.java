package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.data.impl.BasicPartialAssignment;
import be.uclouvain.solvercheck.fuzzing.ArrayGenerator;
import be.uclouvain.solvercheck.fuzzing.AssignmentGenerator;
import be.uclouvain.solvercheck.fuzzing.BaseGenerator;
import be.uclouvain.solvercheck.fuzzing.BooleanGenerator;
import be.uclouvain.solvercheck.fuzzing.DomainGenerator;
import be.uclouvain.solvercheck.fuzzing.Generator;
import be.uclouvain.solvercheck.fuzzing.IntGenerator;
import be.uclouvain.solvercheck.fuzzing.ListGenerator;
import be.uclouvain.solvercheck.fuzzing.OperatorGenerator;
import be.uclouvain.solvercheck.fuzzing.PartialAssignmentGenerator;
import be.uclouvain.solvercheck.fuzzing.Randomness;
import be.uclouvain.solvercheck.fuzzing.SetGenerator;

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

    /** the default minimum number of lines in the generated tables. */
    private static final int DEFAULT_NB_LINES_MIN = 0;
    /** the default maximum number of lines in the generated tables. */
    private static final int DEFAULT_NB_LINES_MAX = 5;

    /** the default minimum value assignable to some variable. */
    private static final int DEFAULT_VALUE_MIN = Integer.MIN_VALUE;
    /** the default maximum value assignable to some variable. */
    private static final int DEFAULT_VALUE_MAX = Integer.MAX_VALUE;

    /**
     * Utility class has no public constructor.
     */
    private GeneratorsDSL() { }

    // --------- INTEGERS -----------------------------------------------------
    public static GenIntBuilder ints() {
        return ints("Integer");
    }
    public static GenIntBuilder ints(final String name) {
        return new GenIntBuilder(name);
    }

    // --------- BOOLEANS -----------------------------------------------------
    public static GenBoolBuilder booleans() {
        return booleans("Bool");
    }
    public static GenBoolBuilder booleans(final String name) {
        return new GenBoolBuilder(name);
    }

    // --------- LISTS --------------------------------------------------------
    public static <T> GenListBuilder<T> listOf(final GenBuilder<T> bldr) {
        return listOf("List", bldr);
    }
    public static <T> GenListBuilder<T> listOf(final String name, final GenBuilder<T> bldr) {
        return new GenListBuilder<>(name, bldr);
    }
    public static <T> GenArrayBuilder<T> arrays() {
        return arrays("Array");
    }
    public static <T> GenArrayBuilder<T> arrays(final String name) {
        return new GenArrayBuilder<>(name);
    }
    // --------- SETS --------------------------------------------------------
    public static GenSetBuilder sets() {
        return sets("Set");
    }
    public static GenSetBuilder sets(final String name) {
        return new GenSetBuilder(name);
    }

    // --------- OPERATORS ----------------------------------------------------
    /**
     * @return  generator pseudo-randomly returns one of the existing operators
     */
    public static GenOperatorBuilder operators() {
        return operators("Operator");
    }
    public static GenOperatorBuilder operators(final String name) {
        return new GenOperatorBuilder(name);
    }

    // --------- DOMAINS ------------------------------------------------------

    /**
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random domains.
     */
    public static GenDomainBuilder domains() {
        return domains("Domain");
    }
    public static GenDomainBuilder domains(final String name) {
        return new GenDomainBuilder(name);
    }

    // --------- PARTIAL-ASSIGNMENT -------------------------------------------
    /**
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random partial assignments.
     */
    public static GenSimplePartialAssignmentBuilder simplePartialAssignments() {
        return simplePartialAssignments("Partial Assignment");
    }
    public static GenSimplePartialAssignmentBuilder simplePartialAssignments(final String name) {
        return new GenSimplePartialAssignmentBuilder(name);
    }

    public static GenCompoundPartialAssignment partialAssignment() {
        return partialAssignment("Partial Assignment");
    }
    public static GenCompoundPartialAssignment partialAssignment(final String name) {
        return new GenCompoundPartialAssignment(name);
    }


    // --------- ASSIGNMENT ---------------------------------------------------
    /**
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random assignments.
     */
    public static GenAssignmentBuilder assignments() {
        return assignments("Assignment");
    }
    public static GenAssignmentBuilder assignments(final String name) {
        return new GenAssignmentBuilder(name);
    }

    // --------- BUILDERS -----------------------------------------------------

    public static final class GenIntBuilder extends GenBuilder<Integer> {
        private int low  = Integer.MIN_VALUE;
        private int high = Integer.MAX_VALUE;


        public GenIntBuilder(final String name) {
            super(name);
        }

        public GenIntBuilder positive() {
            this.low = 0;
            this.high = Integer.MAX_VALUE;
            return this;
        }

        @SuppressWarnings("checkstyle:hiddenfield")
        public GenIntBuilder between(final int low, final int high) {
            this.low  = low;
            this.high = high;
            return this;
        }

        @Override
        public Generator<Integer> build() {
            return new IntGenerator(name(), low, high);
        }
    }

    public static final class GenBoolBuilder extends GenBuilder<Boolean> {

        public GenBoolBuilder(final String name) {
            super(name);
        }

        @Override
        public Generator<Boolean> build() {
            return new BooleanGenerator(name());
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of lists of
     * integers.
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
         * The generator that actually produces the items
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
     * A builder which acts as a micro DSL to produce generators of lists of
     * integers.
     *
     * @param <T> the kind if items in the generated lists.
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
         * Contructor with a name
         * @param name the name of the generator
         */
        public GenArrayBuilder(final String name) {
            super(name);
        }

        /**
         * Tells what is going to be generated as items in the array.
         *
         * @param clazz the class of the generated elements.
         * @param what a generator for the kind of items in the array.
         * @return this.
         */
        public GenArrayBuilder<T> of(final Class<T> clazz, final Generator<T> what) {
            this.clazz = clazz;
            this.generator = what;
            return this;
        }

        /**
         * Tells what is going to be generated as items in the list.
         *
         * @param clazz the class of the generated elements.
         * @param what a generator builder for the kind of items in the array.
         * @return this.
         */
        public GenArrayBuilder<T> of(final Class<T> clazz, final GenBuilder<T> what) {
            this.clazz = clazz;
            this.generator = what.build();
            return this;
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
     * A builder which acts as a micro DSL to produce generators of domains.
     */
    public static final class GenSetBuilder<T> extends GenBuilder<Set<T>> {
        /**
         * The minimum number of items in the list. (default: 0)
         */
        private boolean canBeEmpty = false;
        /**
         * The maximum number of items in the list. (default: 5)
         */
        private int nbItemsMax = DEFAULT_NB_VARS_MAX;
        /**
         * The generator that actually produces the items
         */
        private Generator<T> generator;

        /**
         * Contructor with a name
         * @param name the name of the generator
         */
        public GenSetBuilder(final String name) {
            super(name);
        }

        /**
         * Tells what is going to be generated as items in the set.
         * @param what a generator for the kind of items in the set.
         * @return this.
         */
        public GenSetBuilder of(final Generator<T> what) {
            generator = what;
            return this;
        }

        /**
         * Tells what is going to be generated as items in the set.
         * @param what a generator builder for the kind of items in the set.
         * @return this.
         */
        public GenSetBuilder of(final GenBuilder<T> what) {
            generator = what.build();
            return this;
        }

        /**
         * Tells that generator will produce lists having &lt;= n items.
         *
         * @param n the maximum number of items in the generated list
         * @return this
         */
        public GenSetBuilder ofSizeUpTo(final int n) {
            this.nbItemsMax = n;
            return this;
        }

        /**
         * Tells that the generated set can possibly be empty.
         *
         * @return this
         */
        public GenSetBuilder possiblyEmpty() {
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



    public static final class GenOperatorBuilder extends GenBuilder<Operator> {

        public GenOperatorBuilder(final String name) {
            super(name);
        }

        @Override
        public Generator<Operator> build() {
            return new OperatorGenerator(name());
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
     * A builder which acts as a micro DSL to produce generators of partial
     * assignments.
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
     * A builder which acts as a micro DSL to produce generators of partial
     * assignments.
     */
    public static final class GenSimplePartialAssignmentBuilder
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
        /** the max distance between any two values */
        private int spread   = DEFAULT_SPREAD;

        public GenSimplePartialAssignmentBuilder(final String name) {
            super(name);
        }

        /**
         * Tells that generator will produce partial assignments having
         * exactly n variables.
         *
         * @param n the number of variables in the generated partial assignments
         * @return this
         */
        public GenSimplePartialAssignmentBuilder withVariables(final int n) {
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
        public GenSimplePartialAssignmentBuilder withUpToVariables(final int n) {
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
        public GenSimplePartialAssignmentBuilder withVariablesBetween(
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
        public GenSimplePartialAssignmentBuilder withDomainsOfSizeUpTo(final int n) {
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
        public GenSimplePartialAssignmentBuilder withValuesRanging(
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
        public GenSimplePartialAssignmentBuilder spreading(final int s) {
            this.spread = s;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Generator<PartialAssignment> build() {
            Generator<Domain> domains = new DomainGenerator("", 1, domSzMax, minValue, maxValue, spread);
            return new PartialAssignmentGenerator(name())
               .addListComponent(new ListGenerator<>(domains, nbVarsMin, nbVarsMax));
        }
    }

    public static class GenCompoundPartialAssignment extends GenBuilder<PartialAssignment> {
        private final BasicPartialAssignment instance;

        /**
         * Creates a new Generator builder with the given name.
         *
         * @param name the name (description) of the items to be generated. This
         *             is useful to be able to provide a meaningful information
         *             in the error reports.
         */
        public GenCompoundPartialAssignment(String name) {
            super(name);
            instance = new BasicPartialAssignment();
        }


        public GenCompoundPartialAssignment with(final List<Domain> component) {
            instance.addComponent(component);
            return this;
        }

        public GenCompoundPartialAssignment with(final Domain[] component) {
            instance.addComponent(component);
            return this;
        }

        public GenCompoundPartialAssignment with(final Domain component) {
            instance.addComponent(component);
            return this;
        }

        public GenCompoundPartialAssignment with(final int component) {
            instance.addComponent(component);
            return this;
        }

        public GenCompoundPartialAssignment then(final List<Domain> component) {
            instance.addComponent(component);
            return this;
        }

        public GenCompoundPartialAssignment then(final Domain[] component) {
            instance.addComponent(component);
            return this;
        }

        public GenCompoundPartialAssignment then(final Domain component) {
            instance.addComponent(component);
            return this;
        }

        public GenCompoundPartialAssignment then(final int component) {
            instance.addComponent(component);
            return this;
        }

        @Override
        public Generator<PartialAssignment> build() {
            return new BaseGenerator<PartialAssignment>(name()) {
                @Override
                public Stream<PartialAssignment> generate(final Randomness randomness) {
                    return Stream.of(item(randomness));
                }

                @Override
                public PartialAssignment item(final Randomness randomness) {
                    return instance;
                }
            };
        }
    }
}
