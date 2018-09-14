package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.generators.Generators;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;
import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static be.uclouvain.solvercheck.core.data.Operator.NE;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

public class TestDomain implements WithQuickTheories {

    @Test
    public void restrictReturnsTheSameInstanceWhenItemDoesNotBelongToDomain(){
        qt().forAll(domains(), integers().all())
                .assuming((domain, value) -> !domain.contains(value))
                .check   ((domain, value) -> Domain.restrict(domain, NE, value) == domain );
    }

    @Test
    public void restrictCreatesAProperSubdomainUponRemoval(){
        qt().forAll(domains()).check(dom ->
            dom.stream().allMatch(value -> {
                Domain modified = Domain.restrict(dom, NE, value);
                return !modified.contains(value)
                     && modified.stream().allMatch(vy -> value.equals(vy) || modified.contains(vy));
            })
        );
    }

    @Test
    public void twoDomainsWithSameValuesAreEquivalent() {
        qt().forAll(domains()).check(d -> {
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
        qt().forAll(domains()).check(d ->
                d.stream().allMatch(v -> Domain.restrict(d, NE, v).compareWith(d) == STRONGER)
        );
    }

    @Test
    public void aSuperDomainMustBeWeakerThanItsChild(){
        qt().forAll(domains()).check(d ->
            d.stream().allMatch(v -> d.compareWith(Domain.restrict(d, NE, v)) == WEAKER)
        );
    }

    @Test
    public void subDomainsAreIncomparableWhenNoneContainsTheOther() {
        qt().forAll(domains(5, 10)).check(domain -> {
            if( domain.size() < 2 ) return true;

            Iterator<Integer> it = domain.iterator();
            Domain a = Domain.restrict(domain, NE, it.next());
            Domain b = Domain.restrict(domain, NE, it.next());

            return a.compareWith(b) == PartialOrdering.INCOMPARABLE;
        });
    }

    @Test
    public void testEqualsIffEquivalent() {
        qt().forAll(domains(), domains())
            .check ((a, b) -> a.equals(b) == (a.compareWith(b) == EQUIVALENT));
    }
    @Test
    public void testEquals() {
        qt().forAll(domains()).check(d -> {
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
        qt().forAll(domains()).check(d ->
            d.stream().allMatch(v -> {
                Domain a = Domain.restrict(d, NE, v);
                Domain b = Domain.restrict(d, NE, v);
                return a.hashCode() == b.hashCode();
            })
        );
    }

    @Test
    public void minimumReturnsTheSmallestOfAllValuesInTheDomain(){
        qt().forAll(domains(1, 20))
            .check(x -> x.minimum().equals(Collections.min(x)));
    }
    @Test
    public void minimumFailsWhenDomainIsEmpty(){
        assertThat(catchThrowable(() -> Domain.from().minimum())).isInstanceOf(NoSuchElementException.class);
    }
    @Test
    public void maximimReturnsTheHighestOfAllValuesInTheDomain(){
        qt().forAll(domains(1, 20))
                .check(x -> x.maximum().equals(Collections.max(x)));
    }
    @Test
    public void maximumFailsWhenDomainIsEmpty(){
        assertThat(catchThrowable(() -> Domain.from().maximum())).isInstanceOf(NoSuchElementException.class);
    }

    private Gen<Domain> domains() {
        return domains(0, 10);
    }
    private Gen<Domain> domains(int from, int to) {
        return Generators.domains().ofSizeBetween(from, to).build();
    }
}
