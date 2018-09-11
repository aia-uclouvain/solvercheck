package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.core.data.impl.BasicDomain;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.Iterator;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.*;
import static be.uclouvain.solvercheck.generators.Generators.intDomains;

public class TestBasicDomain implements WithQuickTheories {


    /* FIXME: y a des remove partout !
    @Test
    public void removeReturnsTheSameInstanceWhenItemDoesNotBelongToDomain(){
        qt().forAll(domains(), integers().all())
                .assuming((domain, value) -> !domain.contains(value))
                .check   ((domain, value) -> domain.remove(value) == domain );
    }

    @Test
    public void removeCreatesAProperSubdomainUponRemoval(){
        qt().forAll(domains()).check(d ->
            d.stream().allMatch(vx -> {
                BasicDomain modified = d.remove(vx);
                return modified.stream().allMatch(vy -> vx.equals(vy) || modified.contains(vy));
            })
        );
    }

    @Test
    public void twoDomainsWithSameValuesAreEquivalent() {
        qt().forAll(domains()).check(d -> {
            boolean isReflexive  = d.compareWith(d) == EQUIVALENT;
            boolean isSymmetric  = d.stream().allMatch(v -> {
                BasicDomain a = d.remove(v); BasicDomain b = d.remove(v);
                return a.compareWith(b) == EQUIVALENT && b.compareWith(a) == EQUIVALENT;
            });
            boolean isTransitive = d.stream().allMatch(v -> {
                BasicDomain a = d.remove(v); BasicDomain b = d.remove(v); BasicDomain c = d.remove(v);

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
            d.stream().allMatch(v -> d.remove(v).compareWith(d) == STRONGER)
        );
    }

    @Test
    public void aSuperDomainMustBeWeakerThanItsChild(){
        qt().forAll(domains()).check(d ->
            d.stream().allMatch(v -> d.compareWith(d.remove(v)) == WEAKER)
        );
    }

    @Test
    public void subDomainsAreIncomparableWhenNoneContainsTheOther() {
        qt().forAll(domains(5, 10)).check(domain -> {
            if( domain.size() < 2 ) return true;

            Iterator<Integer> it = domain.iterator();
            BasicDomain a = domain.remove(it.next());
            BasicDomain b = domain.remove(it.next());

            return a.compareWith(b) == PartialOrdering.INCOMPARABLE;
        });
    }

    // I hesistated to write these tests... but given they're easy
    @Test
    public void testContains() {
        qt().forAll(domains(), integers().all()).check((d, v) ->
            d.contains(v) == d.asSet().contains(v)
        );
    }
    @Test
    public void testSize() {
        qt().forAll(domains()).check(d -> d.size() == d.asSet().size());
    }
    @Test
    public void testIsEmpty() {
        qt().forAll(domains()).check(d -> d.isEmpty() == (d.size() == 0));
    }
    @Test
    public void testIsFixed() {
        qt().forAll(domains()).check(d -> d.isFixed() == (d.size() == 1));
    }
    @Test
    public void testToString() {
        qt().forAll(domains()).check(d -> d.toString().equals(d.asSet().toString()));
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
                BasicDomain a = d.remove(v); BasicDomain b = d.remove(v);
                return a.equals(b) && b.equals(a);
            });
            boolean isTransitive = d.stream().allMatch(v -> {
                BasicDomain a = d.remove(v); BasicDomain b = d.remove(v); BasicDomain c = d.remove(v);
                return a.equals(b) && b.equals(c) && a.equals(c);
            });

            return isReflexive && isSymmetric && isTransitive;
        });
    }
    @Test
    public void testHashCode() {
        qt().forAll(domains()).check(d ->
            d.stream().allMatch(v -> {
                BasicDomain a = d.remove(v); BasicDomain b = d.remove(v);
                return a.hashCode() == b.hashCode();
            })
        );
    }

    private Gen<BasicDomain> domains() {
        return domains(1, 10);
    }
    private Gen<BasicDomain> domains(int from, int to) {
        return intDomains().ofSizeBetween(from, to).build();
    }
    */
}
