package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.core.data.impl.AssignmentFactory;
import be.uclouvain.solvercheck.core.data.impl.DomainFactory;
import be.uclouvain.solvercheck.generators.Generators;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;
import org.junit.Before;
import org.junit.Test;
import org.quicktheories.QuickTheory;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.core.data.Operator.NE;
import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.WEAKER;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TestDomain implements WithQuickTheories {

    private QuickTheory qt;

    @Before
    public void setUp() {
        qt = qt().withGenerateAttempts(10000);
    }

    @Test
    public void minimumReturnsTheSmallestOfAllValuesInTheDomain(){
        qt.forAll(domains(1000))
                .assuming(d -> !d.isEmpty())
                .check(x ->
                        x.minimum().equals(Collections.min(x))
                );
    }
    @Test
    public void minimumFailsWhenDomainIsEmpty(){
        assertThat(catchThrowable(() -> Domain.from().minimum())).isInstanceOf(NoSuchElementException.class);
    }
    @Test
    public void maximumReturnsTheHighestOfAllValuesInTheDomain(){
        qt.forAll(domains(1000))
                .assuming(d -> d.size() >= 1)
                .check(x -> x.maximum().equals(Collections.max(x)));
    }
    @Test
    public void maximumFailsWhenDomainIsEmpty(){
        assertThat(catchThrowable(() -> Domain.from().maximum())).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void testIncreasing() {
        qt.forAll(domains(1000)).checkAssert(dom -> {
            long prev = Long.MIN_VALUE;

            Iterator<Integer> it = dom.increasing();
            while (it.hasNext()) {
                int value = it.next();
                assertTrue(prev < value);
                prev = value;
            }
        });
    }

    @Test
    public void testDecreasing() {
        qt.forAll(domains(1000)).checkAssert(dom -> {
            long prev = Long.MAX_VALUE;

            Iterator<Integer> it = dom.decreasing();
            while (it.hasNext()) {
                int value = it.next();
                assertTrue(prev > value);
                prev = value;
            }
        });
    }

    @Test
    public void testIncreasingStream() {
        qt.forAll(domains(1000)).checkAssert(dom -> {
            long prev = Long.MIN_VALUE;

            final List<Integer> lst = dom.increasingStream()
                    .collect(Collectors.toList());

            for (int value : lst) {
                assertTrue(prev < value);
                prev = value;
            };
        });
    }

    @Test
    public void testDecreasingStream() {
        qt.forAll(domains(1000)).checkAssert(dom -> {
            long prev = Long.MAX_VALUE;

            final List<Integer> lst = dom.decreasingStream()
                    .collect(Collectors.toList());

            for (int value : lst) {
                assertTrue(prev > value);
                prev = value;
            };
        });
    }

    @Test
    public void itIsFixedIffItHasOnlyOnePossibleValue() {
        qt.forAll(domains())
          .check(dom -> dom.isFixed() == (dom.size() == 1));
    }

    @Test
    public void emptyDomainProducesAnEmptyDomain() {
        assertTrue(Domain.emptyDomain().isEmpty());
        assertEquals(Domain.emptyDomain(), Domain.emptyDomain());
        assertSame(Domain.emptyDomain(), Domain.emptyDomain());

        assertTrue(failsThrowing(
                NoSuchElementException.class,
                () -> Domain.emptyDomain().minimum()
        ));
        assertTrue(failsThrowing(
                NoSuchElementException.class,
                () -> Domain.emptyDomain().maximum()
        ));
    }

    @Test
    public void singletonProducesAFixedDomain() {
        qt.forAll(integers().all())
          .checkAssert(value -> {
              Domain singleton = Domain.singleton(value);
              assertTrue(singleton.isFixed());
              assertEquals(singleton, Domain.singleton(value));
              assertEquals(value, singleton.minimum());
              assertEquals(value, singleton.maximum());
          });
    }

    @Test
    public void testFromExtension() {
        qt().forAll(
            lists()
                .of(integers().all())
                .ofSizeBetween(0, 1000))
            .check(lst -> {
                int[] array = lst
                        .stream()
                        .mapToInt(Integer::intValue)
                        .toArray();

                List<Integer> distinct = lst
                        .stream()
                        .distinct()
                        .collect(Collectors.toList());

                Domain domain = DomainFactory.from(array);

                return distinct.containsAll(domain)
                        && domain.containsAll(distinct);
            });
    }

    @Test
    public void testFromCollectionExtension() {
        qt().forAll(
               lists()
                .of(integers().all())
                .ofSizeBetween(0, 1000))
            .check(lst -> {
                HashSet<Integer> set = new HashSet<>(lst);
                return set.equals(Domain.from(lst));
            });
    }

    @Test
    public void domainFromAnOtherDomainIsIdentity() {
        qt().forAll(domains())
            .check(dom ->
                dom.equals(Domain.from(dom)) && dom == Domain.from(dom)
            );
    }

    @Test
    public void restrictReturnsTheSameInstanceWhenItemDoesNotBelongToDomain(){
        qt.forAll(domains(), integers().all())
                .assuming((domain, value) -> !domain.contains(value))
                .check   ((domain, value) -> Domain.restrict(domain, NE, value) == domain );
    }

    @Test
    public void restrictCreatesAProperSubdomainUponRemoval(){
        qt.forAll(domains()).check(dom ->
            dom.stream().allMatch(value -> {
                Domain modified = Domain.restrict(dom, NE, value);
                return !modified.contains(value)
                     && modified.stream().allMatch(vy -> value.equals(vy) || modified.contains(vy));
            })
        );
    }

    @Test
    public void testCollector() {
        qt().forAll(lists().of(integers().all()).ofSizeBetween(0, 1000))
            .check(lst ->
                new HashSet<>(lst).equals(lst.stream().collect(Domain.collector()))
            );
    }

    @Test
    public void twoDomainsWithSameValuesAreEquivalent() {
        qt.forAll(domains()).check(d -> {
            boolean isReflexive  = d.compareWith(d) == EQUIVALENT;
            boolean isSymmetric  = d.stream().allMatch(v -> {
                Domain a = Domain.restrict(d, NE, v);
                Domain b = Domain.restrict(d, NE, v);
                return a.compareWith(b) == EQUIVALENT
                    && b.compareWith(a) == EQUIVALENT;
            });
            boolean isTransitive = d.stream().allMatch(v -> {
                Domain a = Domain.restrict(d, NE, v);
                Domain b = Domain.restrict(d, NE, v);
                Domain c = Domain.restrict(d, NE, v);

                return a.compareWith(b) == EQUIVALENT
                    && b.compareWith(c) == EQUIVALENT
                    && a.compareWith(c) == EQUIVALENT;
            });

            return isReflexive && isSymmetric && isTransitive;
        });
    }

    @Test
    public void aSubDomainMustBeStrongerThanItsParent(){
        qt.forAll(domains()).check(d ->
                d.stream().allMatch(v -> Domain.restrict(d, NE, v).compareWith(d) == STRONGER)
        );
    }

    @Test
    public void aSuperDomainMustBeWeakerThanItsChild(){
        qt.forAll(domains()).check(d ->
            d.stream().allMatch(v -> d.compareWith(Domain.restrict(d, NE, v)) == WEAKER)
        );
    }

    @Test
    public void subDomainsAreIncomparableWhenNoneContainsTheOther() {
        qt.forAll(domains())
          .assuming(d -> d.size() >= 2)
            .check(domain -> {
                Iterator<Integer> it = domain.iterator();
                Domain a = Domain.restrict(domain, NE, it.next());
                Domain b = Domain.restrict(domain, NE, it.next());

                return a.compareWith(b) == PartialOrdering.INCOMPARABLE;
        });
    }

    @Test
    public void testEqualsIffEquivalent() {
        qt.forAll(domains(), domains())
            .check ((a, b) -> a.equals(b) == (a.compareWith(b) == EQUIVALENT));
    }
    @Test
    public void testEquals() {
        qt.forAll(domains()).check(d -> {
            boolean isReflexive  = d.equals(d);
            boolean isSymmetric  = d.stream().allMatch(v -> {
                Domain a = Domain.restrict(d, NE, v);
                Domain b = Domain.restrict(d, NE, v);

                return a.equals(b) && b.equals(a);
            });
            boolean isTransitive = d.stream().allMatch(v -> {
                Domain a = Domain.restrict(d, NE, v);
                Domain b = Domain.restrict(d, NE, v);
                Domain c = Domain.restrict(d, NE, v);

                return a.equals(b) && b.equals(c) && a.equals(c);
            });

            return isReflexive && isSymmetric && isTransitive;
        });
    }
    @Test
    public void testHashCode() {
        qt.forAll(domains()).check(d ->
            d.stream().allMatch(v -> {
                Domain a = Domain.restrict(d, NE, v);
                Domain b = Domain.restrict(d, NE, v);
                return a.hashCode() == b.hashCode();
            })
        );
    }

    @Test
    public void testToString() {
        assertEquals(
                Domain.from(1, 2, 3, 4).toString(),
                "{1,2,3,4}"
        );
    }

    private Gen<Domain> domains() {
        return domains(10);
    }
    private Gen<Domain> domains(int to) {
        return Generators.domains().ofSizeUpTo(to).build();
    }
}
