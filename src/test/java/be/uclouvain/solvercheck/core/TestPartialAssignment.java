package be.uclouvain.solvercheck.core;

import be.uclouvain.solvercheck.generators.Generators;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static be.uclouvain.solvercheck.core.StrengthComparison.*;

public class TestPartialAssignment implements WithQuickTheories {

    @Test
    public void testSize() {
        qt().forAll(partialAssignments()).check(a ->
           a.size() == a.stream().collect(Collectors.toList()).size()
        );
    }

    @Test
    public void getReturnsTheIthElementIffItsAValidIndex(){
        qt().forAll(nonEmptyAssignments(), integers().between(-1, 10))
            .check((a, i) -> !isValidVarIndex(i, a) || a.get(i) == Iterables.get(a, i) );
    }

    @Test
    public void getFailsWithExceptionWhenItsNotAValidIndex() {
        qt().forAll(partialAssignments(), integers().between(-1, 10))
            .check((a,i) -> isValidVarIndex(i, a) || failsThrowing(IndexOutOfBoundsException.class, () -> a.get(i) ));
    }

    @Test
    public void removeReturnsSelfWhenGivenAWrongVariable() {
        qt().forAll(partialAssignments(), integers().between(-1, 10), integers().between(-10, 10))
            .check((ass, var, val) ->
                    isValidVarIndex(var, ass)
                 || ass.remove(var, val) == ass);
    }

    @Test
    public void removeReturnsSelfWhenGivenAWrongValue() {
        qt().withGenerateAttempts(10000)
            .forAll(partialAssignments(), integers().between(0, 10), integers().between(-10, 10))
            .assuming((ass, var, val) -> isValidVarIndex(var, ass) && !ass.get(var).contains(val))
            .check(   (ass, var, val) -> ass.remove(var, val) == ass );
    }

    @Test
    public void removeReturnsAProperSubAssignment() {
        qt().forAll(partialAssignments(), integers().between(-1, 10), integers().between(-10, 10))
                .check((ass, var, val) -> {
                    boolean isProper = true;
                    PartialAssignment modified  = ass.remove(var, val);
                    for(int i = 0; isProper && i < ass.size(); i++) {
                        if( i == var) {
                            isProper &= ass.get(i).remove(val).equals(modified.get(i));
                        } else {
                            isProper &= ass.get(i).equals(modified.get(i));
                        }
                    }
                    return isProper;
                });
    }

    @Test
    public void compareWithReturnsIncomparableWhenPAHaveDifferentArity() {
        qt().withGenerateAttempts(10000)
            .forAll  (partialAssignments(), partialAssignments())
            .assuming((a, b) -> a.size() != b.size())
            .check   ((a, b) -> a.compareWith(b) == INCOMPARABLE);
    }

    @Test
    public void testIncomparable() {
        qt().withGenerateAttempts(10000)
            .forAll  (partialAssignments(), partialAssignments())
            .assuming((a, b) -> a.size() == b.size() )
            .check   ((a, b) -> {
                boolean isIncomparable         = a.compareWith(b) == INCOMPARABLE;
                boolean hasIncomparableDomains = zip(a, b).stream().anyMatch(this::domainsAreIncomparable);
                boolean hasStrongerDomain      = zip(a, b).stream().anyMatch(this::domainIsStronger);
                boolean hasWeakerDomain        = zip(a, b).stream().anyMatch(this::domainIsWeaker);

                return isIncomparable == (hasIncomparableDomains || (hasStrongerDomain && hasWeakerDomain));
            });
    }

    @Test
    public void testEquivalent() {
        qt().withGenerateAttempts(10000)
            .forAll  (partialAssignments(), partialAssignments())
            .assuming((a, b) -> a.size() == b.size() )
            .check   ((a, b) -> {
                boolean isEquivalent  = a.compareWith(b) == EQUIVALENT;
                boolean allEquivalents= zip(a, b).stream().allMatch(this::domainsAreEquivalent);

                return isEquivalent == allEquivalents;
            });
    }
    @Test
    public void testStronger() {
        qt().withGenerateAttempts(10000)
            .forAll  (partialAssignments(), partialAssignments())
            .assuming((a, b) -> a.size() == b.size() )
            .check   ((a, b) -> {
                boolean isStronger         = a.compareWith(b) == STRONGER;
                boolean hasStrongerDomain  = zip(a, b).stream().anyMatch(this::domainIsStronger);
                boolean allEquivOrStronger = zip(a, b).stream().allMatch(e-> domainsAreEquivalent(e) || domainIsStronger(e));

                return isStronger == (hasStrongerDomain && allEquivOrStronger);
            });
    }
    @Test
    public void testWeaker() {
        qt().withGenerateAttempts(10000)
            .forAll  (partialAssignments(), partialAssignments())
            .assuming((a, b) -> a.size() == b.size() )
            .check   ((a, b) -> {
                boolean isWeaker         = a.compareWith(b) == WEAKER;
                boolean hasWeakerDomain  = zip(a, b).stream().anyMatch(this::domainIsWeaker);
                boolean allEquivOrWeaker = zip(a, b).stream().allMatch(e-> domainsAreEquivalent(e) || domainIsWeaker(e));

                return isWeaker == (hasWeakerDomain && allEquivOrWeaker);
            });
    }

    @Test
    public void testIsComplete() {
        qt().forAll(partialAssignments()).check(a ->
                a.isComplete() == a.stream().allMatch(Domain::isFixed)
        );
    }
    @Test
    public void testIsLeaf() {
        qt().forAll(partialAssignments()).check(a ->
                a.isLeaf() == (a.stream().allMatch(Domain::isFixed) || a.stream().anyMatch(Domain::isEmpty))
        );
    }

    @Test
    public void testToString() {
        qt().forAll(partialAssignments())
            .check(a -> a.toString().equals(a.stream().collect(Collectors.toList()).toString()));
    }
    @Test
    public void testEqualsIffEquivalent() {
        qt().forAll(partialAssignments(), partialAssignments())
                .check ((a, b) -> a.equals(b) == (a.compareWith(b) == EQUIVALENT));
    }

    @Test
    public void testHashCode() {
        qt().forAll(partialAssignments(), partialAssignments())
            .check ((a, b) -> a.equals(b) == (a.hashCode() == b.hashCode()));
    }

    private boolean domainsAreIncomparable(ZipEntry<Domain, Domain> entry) {
        return domainsAre(entry, INCOMPARABLE);
    }
    private boolean domainsAreEquivalent(ZipEntry<Domain, Domain> entry) {
        return domainsAre(entry, EQUIVALENT);
    }
    private boolean domainIsStronger(ZipEntry<Domain, Domain> entry) {
        return domainsAre(entry, STRONGER);
    }
    private boolean domainIsWeaker(ZipEntry<Domain, Domain> entry) {
        return domainsAre(entry, WEAKER);
    }
    private boolean domainsAre(ZipEntry<Domain, Domain> entry, StrengthComparison cmp) {
        return entry.apply(Domain::compareWith) == cmp;
    }

    private boolean isValidVarIndex(int i, PartialAssignment a) {
        return 0 <= i && i < a.size();
    }

    private <T extends Throwable> boolean  failsThrowing(Class<T> clazz, Runnable r) {
        try { r.run(); return false; } catch (Exception e) { return clazz.isInstance(e); }
    }

    private Gen<PartialAssignment> nonEmptyAssignments() {
        return Generators.partialAssignments()
                .withVariablesRanging(1, 10)
                .withDomainsOfSizeUpTo(10)
                .withValuesRanging(-10, 10)
                .build();
    }
    private Gen<PartialAssignment> partialAssignments() {
        return Generators.partialAssignments()
                .withUpToVariables(10)
                .withDomainsOfSizeUpTo(10)
                .withValuesRanging(-10, 10)
                .build();
    }
    private <A, B> Zip<A, B> zip(final Iterable<A> a, final Iterable<B> b) {
        return new Zip<>(a, b);
    }

    private static class Zip<A, B> implements Iterable<ZipEntry<A, B>> {
        private final Iterable<A> first;
        private final Iterable<B> second;

        public Zip(final Iterable<A> a, final Iterable<B> b) {
            first = a;
            second= b;
        }

        @Override
        public Iterator<ZipEntry<A, B>> iterator() {
            return new ZipIterator<>(first.iterator(), second.iterator());
        }

        public Stream<ZipEntry<A, B>> stream() {
            return StreamSupport.stream(spliterator(), false);
        }
    }

    private static class ZipIterator<A, B> implements Iterator<ZipEntry<A, B>>{
        private final Iterator<A> first;
        private final Iterator<B> second;

        public ZipIterator(final Iterator<A> a, final Iterator<B> b) {
            first = a;
            second= b;
        }

        @Override
        public boolean hasNext() {
            return first.hasNext() || second.hasNext();
        }
        @Override
        public ZipEntry<A, B> next() {
            A nextA = first.hasNext() ? first.next() : null;
            B nextB = second.hasNext()? second.next(): null;

            return new ZipEntry<>(nextA, nextB);
        }
    }
    private static class ZipEntry<A, B> {
        public final A first;
        public final B second;
        public ZipEntry(final A a, final B b) {
            first = a;
            second= b;
        }

        public <R> R apply(final BiFunction<A, B, R> func) {
            return func.apply(first, second);
        }
    }
}
