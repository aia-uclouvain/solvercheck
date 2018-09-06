package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.Domain;
import com.google.common.collect.ImmutableSet;
import org.quicktheories.core.Gen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.quicktheories.generators.Generate.*;

import org.quicktheories.generators.ListsDSL;

public final class Generators {
    /** utility class has no public constructor */
    private Generators(){}

    // --------- LISTS ------------------------------------------------------------------------------------------
    public static <T> Gen<List<T>> listsOf(int n, Gen<T> values) {
        return new ListsDSL().of(values).ofSize(n);
    }
    public static <T> Gen<List<T>> listsOf(int from, int to, Gen<T> values) {
        return new ListsDSL().of(values).ofSizeBetween(from, to);
    }
    public static <T> Gen<List<T>> listsOfUpTo(int n, Gen<T> values) {
        return listsOf(0, n, values);
    }

    // --------- SETS -------------------------------------------------------------------------------------------
    public static <T> Gen<Set<T>> setsOfUpTo(int n, Gen<T> values) {
        return listsOfUpTo(n, values).map(toSet());
    }
    public static <T> Gen<Set<T>> setsOf(int from, int to, Gen<T> values) {
        return listsOf(from, to, values).map(toSet());
    }
    public static <T> Function<List<T>, Set<T>> toSet() {
        return HashSet::new;
    }

    // --------- DOMAINS ----------------------------------------------------------------------------------------
    public static <T> Gen<Domain<T>> domainsOfUpTo(int n, Gen<T> values) {
        return setsOfUpTo(n, values).map(Domain::new);
    }
    public static <T> Gen<Domain<T>> domainsOf(int from, int to, Gen<T> values) {
        return setsOf(from, to, values).map(Domain::new);
    }

    // --------- INT-DOMAINS -----------------------------------------------------------------------------------
    public static GenIntDomainBuilder intDomains() {
        return new GenIntDomainBuilder();
    }
    public static final class GenIntDomainBuilder {
        private int nbValMin = 0;
        private int nbValMax = 10;
        private int minValue = Integer.MIN_VALUE;
        private int maxValue = Integer.MAX_VALUE;

        public GenIntDomainBuilder ofSizeBetween(int from, int to) {
            nbValMin = from;
            nbValMax = to;

            return this;
        }
        public GenIntDomainBuilder ofSizeUpTo(int n) {
            ofSizeBetween(n, n);

            return this;
        }

        public GenIntDomainBuilder withValuesBetween(int from, int to) {
            minValue = from;
            maxValue = to;

            return this;
        }

        public Gen<Domain<Integer>> build() {
            return domainsOf(nbValMin, nbValMax, range(minValue, maxValue));
        }
    }
}
