package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.core.data.impl.DomainFactory;
import be.uclouvain.solvercheck.generators.WithGenerators;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class TestDomainFactory implements WithQuickTheories, WithGenerators {

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
                 dom.equals(DomainFactory.from(dom)) && dom == DomainFactory.from(dom)
            );
    }

    @Test
    public void testRestrict() {
        qt().forAll(operators(), integers().all()).checkAssert((op, value) ->
            qt().forAll(domains()).check(dom -> {
                Domain restricted = DomainFactory.restrict(dom, op, value);
                Domain computed   = dom
                        .stream()
                        .filter(v -> satisfiesRestriction(op, value, v))
                        .collect(DomainFactory.collector());

                return restricted.equals(computed);
            })
        );
    }

    private boolean satisfiesRestriction(
            final Operator op, final int limit, final int value) {
        switch (op) {
            case GT:
                return value > limit;
            case GE:
                return value >= limit;
            case LT:
                return value < limit;
            case LE:
                return value <= limit;
            case EQ:
                return value == limit;
            case NE:
                return value != limit;
            default:
                throw new RuntimeException("Unreachable code");
        }
    }

    @Test
    public void testCollector() {
        qt().forAll(lists().of(integers().all()).ofSizeBetween(0, 1000))
           .check(lst -> {
               Domain d = lst.stream().collect(DomainFactory.collector());

                return d.containsAll(lst) && lst.containsAll(d);
           });
    }
}
