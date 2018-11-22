package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.impl.DomainFactory;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;
import org.junit.Test;

import java.util.Arrays;
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
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDomain implements WithSolverCheck {

    @Test
    public void minimumReturnsTheSmallestOfAllValuesInTheDomain(){
        assertThat(
           forAll(domains())
           .assuming(d -> !d.isEmpty())
           .itIsTrueThat(x -> x.minimum().equals(Collections.min(x)))
        );
    }

    @Test
    public void minimumFailsWhenDomainIsEmpty(){
        assertTrue(
           failsThrowing(
              NoSuchElementException.class,
              () -> Domain.from().minimum()
           )
        );
    }

    @Test
    public void maximumReturnsTheHighestOfAllValuesInTheDomain(){
        assertThat(
           forAll(domains())
          .assuming(d -> d.size() >= 1)
          .itIsTrueThat(x -> x.maximum().equals(Collections.max(x)))
        );
    }

    @Test
    public void maximumFailsWhenDomainIsEmpty(){
        assertTrue(
           failsThrowing(
              NoSuchElementException.class,
              () -> Domain.from().maximum()
           )
        );
    }

    @Test
    public void testIncreasing() {
        assertThat(
           forAll(domains())
           .itIsTrueThat(dom -> {
            long prev = Long.MIN_VALUE;

            Iterator<Integer> it = dom.increasing();
            while (it.hasNext()) {
                int value = it.next();
                assertTrue(prev < value);
                prev = value;
            }

            // loop assertions would have failed in case of violation
            return true;
        })
      );
    }

    @Test
    public void testDecreasing() {
        assertThat(forAll(domains()).itIsTrueThat(dom -> {
            long prev = Long.MAX_VALUE;

            Iterator<Integer> it = dom.decreasing();
            while (it.hasNext()) {
                int value = it.next();
                assertTrue(prev > value);
                prev = value;
            }

            // loop assertions fail in case of error
            return true;
        }));
    }

    @Test
    public void testIncreasingStream() {
        assertThat(forAll(domains()).itIsTrueThat(dom -> {
            long prev = Long.MIN_VALUE;

            final List<Integer> lst = dom.increasingStream()
                    .collect(Collectors.toList());

            for (int value : lst) {
                assertTrue(prev < value);
                prev = value;
            }

            // loop assertions fail in case of error
            return true;
        }));
    }

    @Test
    public void testDecreasingStream() {
        assertThat(forAll(domains()).itIsTrueThat(dom -> {
            long prev = Long.MAX_VALUE;

            final List<Integer> lst = dom.decreasingStream()
                    .collect(Collectors.toList());

            for (int value : lst) {
                assertTrue(prev > value);
                prev = value;
            }
            // loop assertions fail in case of error
            return true;
        }));
    }

    @Test
    public void itIsFixedIffItHasOnlyOnePossibleValue() {
        assertThat(forAll(domains()).itIsTrueThat(dom ->
          dom.isFixed() == (dom.size() == 1)
        ));
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
        assertThat(forAll(integers()).itIsTrueThat(value -> {
          Domain singleton = Domain.singleton(value);
          assertTrue(singleton.isFixed());
          assertEquals(singleton, Domain.singleton(value));
          assertEquals(value, singleton.minimum());
          assertEquals(value, singleton.maximum());

          // prev assertions fail in case of error
          return true;
        }));
    }

    @Test
    public void testFromExtension() {
        assertThat(
           forAll(arrays()).itIsTrueThat(array -> {
                List<Integer> distinct = Arrays.stream(array)
                        .distinct()
                        .boxed()
                        .collect(Collectors.toList());

                Domain domain = DomainFactory.from(array);

                return distinct.containsAll(domain)
                        && domain.containsAll(distinct);
            }));
    }

    @Test
    public void testFromCollectionExtension() {
        assertThat(forAll(lists()).itIsTrueThat(lst -> {
            HashSet<Integer> set = new HashSet<>(lst);
            return set.equals(Domain.from(lst));
        }));
    }

    @Test
    public void domainFromAnOtherDomainIsIdentity() {
        assertThat(forAll(domains()).itIsTrueThat(dom ->
            dom.equals(Domain.from(dom)) && dom == Domain.from(dom)
        ));
    }

    @Test
    public void restrictReturnsTheSameInstanceWhenItemDoesNotBelongToDomain(){
        assertThat(
           forAll(domains(), integers())
           .assuming((domain, value) -> !domain.contains(value))
           .itIsTrueThat((domain, value) -> Domain.restrict(domain, NE, value) == domain )
        );
    }

    @Test
    public void restrictCreatesAProperSubdomainUponRemoval(){
        assertThat(forAll(domains()).itIsTrueThat(dom ->
            dom.stream().allMatch(value -> {
                Domain modified = Domain.restrict(dom, NE, value);
                return !modified.contains(value)
                     && modified.stream().allMatch(vy -> value.equals(vy) || modified.contains(vy));
            })
        ));
    }

    @Test
    public void testCollector() {
        assertThat(forAll(lists()).itIsTrueThat(lst ->
            new HashSet<>(lst).equals(lst.stream().collect(Domain.collector()))
        ));
    }

    @Test
    public void twoDomainsWithSameValuesAreEquivalent() {
        assertThat(forAll(domains()).itIsTrueThat(d -> {
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
        }));
    }

    @Test
    public void aSubDomainMustBeStrongerThanItsParent(){
        assertThat(forAll(domains()).itIsTrueThat(d ->
            d.stream().allMatch(v -> Domain.restrict(d, NE, v).compareWith(d) == STRONGER)
        ));
    }

    @Test
    public void aSuperDomainMustBeWeakerThanItsChild(){
        assertThat(forAll(domains()).itIsTrueThat(d ->
            d.stream().allMatch(v -> d.compareWith(Domain.restrict(d, NE, v)) == WEAKER)
        ));
    }

    @Test
    public void subDomainsAreIncomparableWhenNoneContainsTheOther() {
        assertThat(
           forAll(domains())
              .assuming(d -> d.size() >= 2)
              .itIsTrueThat(domain -> {
                Iterator<Integer> it = domain.iterator();
                Domain a = Domain.restrict(domain, NE, it.next());
                Domain b = Domain.restrict(domain, NE, it.next());

                return a.compareWith(b) == PartialOrdering.INCOMPARABLE;
        }));
    }

    @Test
    public void testEqualsIffEquivalent() {
        assertThat(
           forAll(domains("A"), domains("B"))
           .itIsTrueThat((a, b) ->
              a.equals(b) == (a.compareWith(b) == EQUIVALENT)
       ));
    }
    @Test
    public void testEquals() {
        assertThat(forAll(domains()).itIsTrueThat(d -> {
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
        }));
    }
    @Test
    public void testHashCode() {
        assertThat(forAll(domains()).itIsTrueThat(d ->
            d.stream().allMatch(v -> {
                Domain a = Domain.restrict(d, NE, v);
                Domain b = Domain.restrict(d, NE, v);
                return a.hashCode() == b.hashCode();
            })
        ));
    }

    @Test
    public void testToString() {
        assertEquals(
                Domain.from(1, 2, 3, 4).toString(),
                "{1,2,3,4}"
        );
    }
}
