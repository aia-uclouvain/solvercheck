package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import org.quicktheories.core.Gen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.generators.SourceDSL.lists;

public final class Generators {
    /**
     * utility class has no public constructor
     */
    private Generators() {
    }

    // --------- LISTS ------------------------------------------------------------------------------------------
    public static <T> Gen<List<T>> listsOf(final int n, final Gen<T> values) {
        return lists().of(values).ofSize(n);
    }

    public static <T> Gen<List<T>> listsOf(final int from, final int to, final Gen<T> values) {
        return lists().of(values).ofSizeBetween(from, to);
    }

    public static <T> Gen<List<T>> listsOfUpTo(final int n, final Gen<T> values) {
        return listsOf(0, n, values);
    }

    // --------- SETS -------------------------------------------------------------------------------------------
    public static <T> Gen<Set<T>> setsOfUpTo(final int n, final Gen<T> values) {
        return listsOfUpTo(n, values).map(toSet());
    }

    public static <T> Gen<Set<T>> setsOf(final int from, final int to, final Gen<T> values) {
        return listsOf(from, to, values).map(toSet());
    }

    public static <T> Function<List<T>, Set<T>> toSet() {
        return HashSet::new;
    }


    // --------- OPERATORS --------------------------------------------------------------------------------------
    public static Gen<Operator> operators() {
        return integers().between(0, 5).map(Generators::operatorFrom);
    }

    private static Operator operatorFrom(int i) {
        return Operator.values()[i%Operator.values().length];
    }

    // --------- DOMAINS ----------------------------------------------------------------------------------------
    public static GenDomainBuilder domains() {
        return new GenDomainBuilder();
    }

    // --------- PARTIAL-ASSIGNMENT -----------------------------------------------------------------------------
    public static GenPartialAssignmentBuilder partialAssignments() {
        return new GenPartialAssignmentBuilder();
    }

    // --------- PARTIAL-ASSIGNMENT -----------------------------------------------------------------------------
    public static GenAssignmentBuilder assignments() {
        return new GenAssignmentBuilder();
    }

    // --------- TABLES -----------------------------------------------------------------------------------------
    public static GenTableBuilder tables() {
        return new GenTableBuilder();
    }

    public static final class GenDomainBuilder {
        private int nbValMin = 0;
        private int nbValMax = 10;
        private int minValue = Integer.MIN_VALUE;
        private int maxValue = Integer.MAX_VALUE;

        public GenDomainBuilder ofSizeBetween(final int from, final int to) {
            nbValMin = from;
            nbValMax = to;

            return this;
        }

        public GenDomainBuilder ofSizeUpTo(final int n) {
            ofSizeBetween(0, n);

            return this;
        }

        public GenDomainBuilder withValuesBetween(final int from, final int to) {
            minValue = from;
            maxValue = to;

            return this;
        }

        public Gen<Domain> build() {
            return setsOf(nbValMin, nbValMax, integers().between(minValue, maxValue))
                    .map(set -> Domain.from(set));
        }
    }

    public static final class GenPartialAssignmentBuilder {
        private final GenDomainBuilder domainBuilder = domains();
        private int nbVarsMin = 0;
        private int nbVarsMax = 5;

        public GenPartialAssignmentBuilder withVariables(int n) {
            this.nbVarsMin = n;
            this.nbVarsMax = n;
            return this;
        }

        public GenPartialAssignmentBuilder withUpToVariables(int n) {
            this.nbVarsMin = 0;
            this.nbVarsMax = n;
            return this;
        }

        public GenPartialAssignmentBuilder withVariablesRanging(int from, int to) {
            this.nbVarsMin = from;
            this.nbVarsMax = to;
            return this;
        }

        public GenPartialAssignmentBuilder withDomainsOfSizeBetween(int x, int y) {
            domainBuilder.ofSizeBetween(x, y);
            return this;
        }

        public GenPartialAssignmentBuilder withDomainsOfSizeUpTo(int n) {
            domainBuilder.ofSizeUpTo(n);
            return this;
        }

        public GenPartialAssignmentBuilder withValuesRanging(int from, int to) {
            domainBuilder.withValuesBetween(from, to);
            return this;
        }

        public Gen<PartialAssignment> build() {
            return lists().of(domainBuilder.build())
                    .ofSizeBetween(nbVarsMin, nbVarsMax)
                    .map(PartialAssignment::from);
        }
    }

    public static final class GenAssignmentBuilder {
        private int nbVarsMin =   0;
        private int nbVarsMax =   5;
        private int valueMin  = -10;
        private int valueMax  =  10;

        public GenAssignmentBuilder withVariables(int n) {
            this.nbVarsMin = n;
            this.nbVarsMax = n;
            return this;
        }

        public GenAssignmentBuilder withUpToVariables(int n) {
            this.nbVarsMin = 0;
            this.nbVarsMax = n;
            return this;
        }

        public GenAssignmentBuilder withVariablesRanging(int from, int to) {
            this.nbVarsMin = from;
            this.nbVarsMax = to;
            return this;
        }

        public GenAssignmentBuilder withValuesRanging(int from, int to) {
            this.valueMin = from;
            this.valueMax = to;
            return this;
        }

        public Gen<Assignment> build() {
            return lists().of(integers().between(valueMin, valueMax))
                    .ofSizeBetween(nbVarsMin, nbVarsMax)
                    .map(Assignment::from);
        }
    }

    public static final class GenTableBuilder {
        private int nbLinesMin= 0;
        private int nbLinesMax= 5;

        private final GenAssignmentBuilder builder = new GenAssignmentBuilder();


        public GenTableBuilder withVariables(int n) {
            builder.withVariables(n);
            return this;
        }

        public GenTableBuilder withUpToVariables(int n) {
            builder.withUpToVariables(n);
            return this;
        }

        public GenTableBuilder withVariablesRanging(int from, int to) {
            builder.withVariablesRanging(from, to);
            return this;
        }

        public GenTableBuilder withValuesRanging(int from, int to) {
            builder.withValuesRanging(from, to);
            return this;
        }

        public GenTableBuilder withLines(int n) {
            nbLinesMin = n;
            nbLinesMax = n;
            return this;
        }

        public GenTableBuilder withUpToLines(int n) {
            nbLinesMin = 0;
            nbLinesMax = n;
            return this;
        }
        public GenTableBuilder withLinesRanging(int from, int to) {
            nbLinesMin = from;
            nbLinesMax = to;
            return this;
        }

        public Gen<List<Assignment>> build() {
            return lists().of(builder.build()).ofSizeBetween(nbLinesMin, nbLinesMax);
        }
    }
}
