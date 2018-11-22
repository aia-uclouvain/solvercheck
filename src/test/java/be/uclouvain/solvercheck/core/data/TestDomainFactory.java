package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.impl.DomainFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class TestDomainFactory implements WithSolverCheck {

    @Test
    public void testFromExtension() {
        assertThat(forAll(arrays()).itIsTrueThat(array -> {
                List<Integer> distinct = Arrays.stream(array)
                        .boxed()
                        .distinct()
                        .collect(Collectors.toList());

                Domain domain = DomainFactory.from(array);

                return distinct.containsAll(domain)
                        && domain.containsAll(distinct);
            })
        );
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
           dom.equals(DomainFactory.from(dom)) && dom == DomainFactory.from(dom)
        ));
    }

    @Test
    public void testRestrict() {
        assertThat(
           forAll(operators(), integers()).assertThat((op, value) ->
           forAll(domains()).itIsTrueThat(dom -> {
                Domain restricted = DomainFactory.restrict(dom, op, value);
                Domain computed   = dom
                        .stream()
                        .filter(v -> satisfiesRestriction(op, value, v))
                        .collect(DomainFactory.collector());

                return restricted.equals(computed);
            }))
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
        assertThat(forAll(lists()).itIsTrueThat(lst -> {
           Domain d = lst.stream().collect(DomainFactory.collector());

           return d.containsAll(lst) && lst.containsAll(d);
       }));
    }
}
