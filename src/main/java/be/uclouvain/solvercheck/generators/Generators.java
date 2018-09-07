package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.Domain;
import be.uclouvain.solvercheck.core.PartialAssignment;
import org.quicktheories.core.Gen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.quicktheories.generators.SourceDSL.*;

public final class Generators {
    /** utility class has no public constructor */
    private Generators(){}

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

    // --------- DOMAINS ----------------------------------------------------------------------------------------
    public static GenIntDomainBuilder intDomains() {
        return new GenIntDomainBuilder();
    }
    public static final class GenIntDomainBuilder {
        private int nbValMin = 0;
        private int nbValMax = 10;
        private int minValue = Integer.MIN_VALUE;
        private int maxValue = Integer.MAX_VALUE;

        public GenIntDomainBuilder ofSizeBetween(final int from, final int to) {
            nbValMin = from;
            nbValMax = to;

            return this;
        }
        public GenIntDomainBuilder ofSizeUpTo(final int n) {
            ofSizeBetween(0, n);

            return this;
        }

        public GenIntDomainBuilder withValuesBetween(final int from, final int to) {
            minValue = from;
            maxValue = to;

            return this;
        }

        public Gen<Domain> build() {
            return setsOf(nbValMin, nbValMax, integers().between(minValue, maxValue)).map(Domain::new);
        }
    }

    // --------- PARTIAL-ASSIGNMENT -----------------------------------------------------------------------------
    public static GenPartialAssignmentBuilder partialAssignments() {
        return new GenPartialAssignmentBuilder();
    }
    public static final class GenPartialAssignmentBuilder {
        private int nbVarsMin = 0;
        private int nbVarsMax = 10;

        private final GenIntDomainBuilder domainBuilder = intDomains();

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
                        .map(PartialAssignment::new);
        }
    }

}
