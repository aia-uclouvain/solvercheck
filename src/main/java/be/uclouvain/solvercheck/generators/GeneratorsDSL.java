package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.pbt.Generators;

import java.util.List;
import java.util.stream.IntStream;
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
    public static IntStream ints(final int from, final int to) {
        return Generators.ints(from, to);
    }

    // --------- BOOLEANS -----------------------------------------------------
    public static Stream<Boolean> booleans() {
        return Generators.booleans();
    }

    // --------- OPERATORS ----------------------------------------------------
    /**
     * @return  generator pseudo-randomly returns one of the existing operators
     */
    public static Stream<Operator> operators() {
        return Generators.operators();
    }

    // --------- DOMAINS ------------------------------------------------------

    /**
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random domains.
     */
    public static GenDomainBuilder domains() {
        return new GenDomainBuilder();
    }

    // --------- PARTIAL-ASSIGNMENT -------------------------------------------
    /**
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random partial assignments.
     */
    public static GenPartialAssignmentBuilder partialAssignments() {
        return new GenPartialAssignmentBuilder();
    }

    // --------- ASSIGNMENT ---------------------------------------------------
    /**
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random assignments.
     */
    public static GenAssignmentBuilder assignments() {
        return new GenAssignmentBuilder();
    }

    // --------- TABLES -------------------------------------------------------
    /**
     * @return a builder meant to act as a micro DSL to instantiate
     * generators that produce random table constraints.
     */
    public static GenTableBuilder tables() {
        return new GenTableBuilder();
    }

    // --------- BUILDERS -----------------------------------------------------

    /**
     * This abstract class serves as a basis for the implementation of
     * fluent generator builders.
     *
     * @param <T> the type of the objects to generate.
     */
    private abstract static class GenBuilder<T> {
        /**
         * Returns the actual generator (stream) instance, based on the current
         * configuration state.
         *
         * @return the actual generator.
         */
        protected abstract Stream<T> build();
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
        public Stream<Domain> build() {
            return Generators.domains(allowErrors, nbValMax, minValue, maxValue);
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
        public Stream<PartialAssignment> build() {
            return Generators.partialAssignments(
               nbVarsMin, nbVarsMax, allowErrors, domSzMax, minValue, maxValue);
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
        public Stream<Assignment> build() {
            return Generators
               .assignments(nbVarsMin, nbVarsMax, valueMin, valueMax);
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
        public Stream<List<Assignment>> build() {
            return Generators.tables(
              nbLinesMin, nbLinesMax, nbVarsMin, nbVarsMax, valueMin, valueMax);
        }
    }
}
