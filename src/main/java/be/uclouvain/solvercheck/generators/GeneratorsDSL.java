package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.fuzzing.BaseGenerator;
import be.uclouvain.solvercheck.fuzzing.Generator;
import be.uclouvain.solvercheck.fuzzing.Generators;
import be.uclouvain.solvercheck.fuzzing.Randomness;

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
    private static final int DEFAULT_NB_VAL_MAX = 10;

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
    public static GenListBuilder lists() {
        return lists("List");
    }
    public static GenListBuilder lists(final String name) {
        return new GenListBuilder(name);
    }
    public static GenArrayBuilder arrays() {
        return arrays("Array");
    }
    public static GenArrayBuilder arrays(final String name) {
        return new GenArrayBuilder(name);
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
    public static GenPartialAssignmentBuilder partialAssignments() {
        return partialAssignments("Partial Assignment");
    }
    public static GenPartialAssignmentBuilder partialAssignments(final String name) {
        return new GenPartialAssignmentBuilder(name);
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

    // --------- TABLES -------------------------------------------------------
    /**
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random table constraints.
     */
    public static GenTableBuilder tables() {
        return tables("Table");
    }
    public static GenTableBuilder tables(final String name) {
        return new GenTableBuilder(name);
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
            return new BaseGenerator<>(name()) {
                /** @inheritDoc} */
                @Override
                public Stream<Integer> generate(final Randomness rnd) {
                    return Generators.ints(rnd, low, high);
                }
            };
        }
    }

    public static final class GenBoolBuilder extends GenBuilder<Boolean> {

        public GenBoolBuilder(final String name) {
            super(name);
        }

        @Override
        public Generator<Boolean> build() {
            return new BaseGenerator<>(name()) {
                /** @inheritDoc} */
                @Override
                public Stream<Boolean> generate(final Randomness rnd) {
                    return Generators.booleans(rnd);
                }
            };
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of lists of
     * integers.
     */
    public static final class GenListBuilder extends GenBuilder<List<Integer>> {

        /**
         * The minimum number of items in the list. (default: 0)
         */
        private int nbItemsMin = DEFAULT_NB_VARS_MIN;
        /**
         * The maximum number of items in the list. (default: 5)
         */
        private int nbItemsMax = DEFAULT_NB_VARS_MAX;
        /**
         * The minimum value that may occur in the list.
         */
        private int valueMin  = DEFAULT_VALUE_MIN;
        /**
         * The maximum value that may occur in the list.
         */
        private int valueMax  = DEFAULT_VALUE_MAX;

        public GenListBuilder(final String name) {
            super(name);
        }

        /**
         * Tells that generator will produce lists having exactly n items.
         *
         * @param n the number of items in the generated list.
         * @return this
         */
        public GenListBuilder ofSize(final int n) {
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
        public GenListBuilder ofSizeUpTo(final int n) {
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
        public GenListBuilder ofSizeBetween(final int from, final int to) {
            this.nbItemsMin = from;
            this.nbItemsMax = to;
            return this;
        }

        /**
         * Specifies the allowed range of value to be comprised between `from`
         * and `to` (inclusive).
         *
         * @param from a lower bound on the possible values.
         * @param to an upper bound on the possible values.
         * @return this
         */
        public GenListBuilder withValuesRanging(final int from, final int to) {
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
        public Generator<List<Integer>> build() {
            return new BaseGenerator<>(name()) {
                /** {@inheritDoc} */
                @Override
                public Stream<List<Integer>> generate(final Randomness rnd) {
                    return Generators
                       .lists(rnd, nbItemsMin, nbItemsMax, valueMin, valueMax);
                }
            };
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of lists of
     * integers.
     */
    public static final class GenArrayBuilder extends GenBuilder<int[]> {

        /**
         * The minimum number of items in the list. (default: 0)
         */
        private int nbItemsMin = DEFAULT_NB_VARS_MIN;
        /**
         * The maximum number of items in the list. (default: 5)
         */
        private int nbItemsMax = DEFAULT_NB_VARS_MAX;
        /**
         * The minimum value that may occur in the list.
         */
        private int valueMin  = DEFAULT_VALUE_MIN;
        /**
         * The maximum value that may occur in the list.
         */
        private int valueMax  = DEFAULT_VALUE_MAX;

        public GenArrayBuilder(final String name) {
            super(name);
        }

        /**
         * Tells that generator will produce lists having exactly n items.
         *
         * @param n the number of items in the generated list.
         * @return this
         */
        public GenArrayBuilder ofSize(final int n) {
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
        public GenArrayBuilder ofSizeUpTo(final int n) {
            this.nbItemsMin = 0;
            this.nbItemsMax = n;
            return this;
        }
        /**
         * Tells that generator will produce arrays having between from
         * and to items.
         *
         * @param from a lower bound on the number of items in the list.
         * @param to an upper bound on the number of items in the list.
         *
         * @return this
         */
        public GenArrayBuilder ofSizeBetween(final int from, final int to) {
            this.nbItemsMin = from;
            this.nbItemsMax = to;
            return this;
        }

        /**
         * Specifies the allowed range of value to be comprised between `from`
         * and `to` (inclusive).
         *
         * @param from a lower bound on the possible values.
         * @param to an upper bound on the possible values.
         * @return this
         */
        public GenArrayBuilder withValuesRanging(final int from, final int to) {
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
        public Generator<int[]> build() {
            return new BaseGenerator<>(name()) {
                /** {@inheritDoc} */
                @Override
                public Stream<int[]> generate(final Randomness rnd) {
                    return Generators
                       .lists(rnd, nbItemsMin, nbItemsMax, valueMin, valueMax)
                       .map(lst -> lst.stream().mapToInt(Integer::intValue).toArray());
                }
            };
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of domains.
     */
    public static final class GenSetBuilder extends GenBuilder<Set<Integer>> {
        /**
         * a flag telling whether or not we allow the generator to produce
         * empty sets.
         */
        private boolean allowEmpty = false;
        /** the maximum number of different values a domain should contain. */
        private int nbValMax = DEFAULT_NB_VAL_MAX;
        /** the lowest value that can be contained in the domain. */
        private int minValue = DEFAULT_VALUE_MIN;
        /** the highest value that can be contained in the domain. */
        private int maxValue = DEFAULT_VALUE_MAX;

        public GenSetBuilder(final String name) {
            super(name);
        }

        /**
         * Configures the builder to create stream of sets which may be empty.
         *
         * @return this
         */
        public GenSetBuilder possiblyEmpty() {
            allowEmpty = true;
            return this;
        }

        /**
         * Sets the size range of the generated sets (from 0, to n).
         *
         * @param n the maximum number of different values a generated
         *           set should hold
         * @return this
         */
        public GenSetBuilder ofSizeUpTo(final int n) {
            nbValMax = n;

            return this;
        }

        /**
         * Sets the values range of the generated sets.
         *
         * @param from the minimum value that a generated set can hold
         * @param to the maximum value that a generated set can hold
         * @return this
         */
        public GenSetBuilder withValuesBetween(final int from, final int to) {
            minValue = from;
            maxValue = to;

            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Generator<Set<Integer>> build() {
            return new BaseGenerator<>(name()) {
                /** @inheritDoc} */
                @Override
                public Stream<Set<Integer>> generate(final Randomness rnd) {
                    return Generators.sets(rnd, allowEmpty, nbValMax, minValue, maxValue);
                }
            };
        }
    }



    public static final class GenOperatorBuilder extends GenBuilder<Operator> {

        public GenOperatorBuilder(final String name) {
            super(name);
        }

        @Override
        public Generator<Operator> build() {
            return new BaseGenerator<>(name()) {
                /** @inheritDoc} */
                @Override
                public Stream<Operator> generate(final Randomness rnd) {
                    return Generators.operators(rnd);
                }
            };
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of domains.
     */
    public static final class GenDomainBuilder extends GenBuilder<Domain> {
        /**
         * a flag telling whether or not we allow the generator to produce
         * error domains.
         */
        private boolean allowErrors = false;
        /** the maximum number of different values a domain should contain. */
        private int nbValMax = DEFAULT_NB_VAL_MAX;
        /** the lowest value that can be contained in the domain. */
        private int minValue = DEFAULT_VALUE_MIN;
        /** the highest value that can be contained in the domain. */
        private int maxValue = DEFAULT_VALUE_MAX;

        public GenDomainBuilder(final String name) {
            super(name);
        }

        /**
         * Configures the builder to create stream of domains which may be
         * empty (erroneous).
         *
         * @return this
         */
        public GenDomainBuilder allowingErrors() {
            allowErrors = true;
            return this;
        }

        /**
         * Sets the size range of the generated domains (from 0, to n).
         *
         * @param n the maximum number of different values a generated
         *           domain should hold
         * @return this
         */
        public GenDomainBuilder ofSizeUpTo(final int n) {
            nbValMax = n;

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
          return new BaseGenerator<>(name()) {
             /** @inheritDoc} */
             @Override
             public Stream<Domain> generate(final Randomness rnd) {
                 return Generators.domains(rnd, allowErrors, nbValMax, minValue, maxValue);
             }
          };
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of partial
     * assignments.
     */
    public static final class GenPartialAssignmentBuilder
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
        /**
         * a flag telling whether or not we allow the generator to produce
         * error domains.
         */
        private boolean allowErrors = false;
        /** the maximum number of different values a domain should contain. */
        private int domSzMax = DEFAULT_NB_VAL_MAX;
        /** the lowest value that can be contained in the domain. */
        private int minValue = DEFAULT_VALUE_MIN;
        /** the highest value that can be contained in the domain. */
        private int maxValue = DEFAULT_VALUE_MAX;

        public GenPartialAssignmentBuilder(final String name) {
            super(name);
        }

        /**
         * Tells that generator will produce partial assignments having
         * exactly n variables.
         *
         * @param n the number of variables in the generated partial assignments
         * @return this
         */
        public GenPartialAssignmentBuilder withVariables(final int n) {
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
        public GenPartialAssignmentBuilder withUpToVariables(final int n) {
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
        public GenPartialAssignmentBuilder withVariablesBetween(
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
        public GenPartialAssignmentBuilder withDomainsOfSizeUpTo(final int n) {
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
        public GenPartialAssignmentBuilder withValuesRanging(
                final int from,
                final int to) {
            this.minValue = from;
            this.maxValue = to;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Generator<PartialAssignment> build() {
            return new BaseGenerator<>(name()) {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public Stream<PartialAssignment> generate(final Randomness rnd) {
                    return Generators.partialAssignments(
                       rnd, nbVarsMin, nbVarsMax, allowErrors, domSzMax, minValue, maxValue);
                }
            };
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
            return new BaseGenerator<>(name()) {
                /** {@inheritDoc} */
                @Override
                public Stream<Assignment> generate(final Randomness rnd) {
                  return Generators
                    .assignments(rnd, nbVarsMin, nbVarsMax, valueMin, valueMax);
                }
            };
        }
    }

    /**
     * A builder which acts as a micro DSL to produce generators of table
     * constraints.
     */
    public static final class GenTableBuilder
            extends GenBuilder<List<Assignment>> {

        /** the minimum number of lines in the generated tables. */
        private int nbLinesMin = DEFAULT_NB_LINES_MIN;
        /** the maximum number of lines in the generated tables. */
        private int nbLinesMax = DEFAULT_NB_LINES_MAX;
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

        public GenTableBuilder(final String name) {
            super(name);
        }

        /**
         * Tells that generator will produce tables made of assignments having
         * exactly n variables.
         *
         * @param n the number of variables in the generated tables
         * @return this
         */
        public GenTableBuilder withVariables(final int n) {
            this.nbVarsMin = n;
            this.nbVarsMax = n;
            return this;
        }

        /**
         * Tells that in the generated table, the values assigned to
         * variables, will range between `from` and `to`.
         *
         * @param from a lower bound on the value of variables in any of the
         *             generated assignments
         * @param to an upper bound on the value of variables in any of the
         *           generated assignments
         * @return this
         */
        public GenTableBuilder withValuesRanging(
                final int from,
                final int to) {
            this.valueMin = from;
            this.valueMax = to;
            return this;
        }

        /**
         * Tells that the generated tables will have exactly `n` lines.
         *
         * @param n the number of lines in the generated tables
         * @return this
         */
        public GenTableBuilder withLines(final int n) {
            nbLinesMin = n;
            nbLinesMax = n;
            return this;
        }
        /**
         * Tells that the generated tables will have up to `n` lines.
         *
         * @param n the maximum number of lines in the generated tables
         * @return this
         */
        public GenTableBuilder withUpToLines(final int n) {
            nbLinesMin = 0;
            nbLinesMax = n;
            return this;
        }
        /**
         * Tells that the number of lines in the generated tables range
         * between `from` and `to`.
         *
         * @param from the minimum number of lines in the generated tables
         * @param to the maximum number of lines in the generated tables
         *
         * @return this
         */
        public GenTableBuilder withLinesRanging(
                final int from,
                final int to) {
            nbLinesMin = from;
            nbLinesMax = to;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @return an table constraints generator that corresponds to the
         * configuration specified with the other methods.
         */
        @Override
        public Generator<List<Assignment>> build() {
            return new BaseGenerator<>(name()) {
                /** {@inheritDoc} */
                @Override
                public Stream<List<Assignment>> generate(final Randomness rnd) {
                    return Generators
                       .tables(rnd,
                               nbLinesMin, nbLinesMax,
                               nbVarsMin, nbVarsMax,
                               valueMin, valueMax);
                }
            };
        }
    }
}
