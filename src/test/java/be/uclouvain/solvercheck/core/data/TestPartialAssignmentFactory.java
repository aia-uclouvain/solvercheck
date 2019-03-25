package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.impl.PartialAssignmentFactory;
import be.uclouvain.solvercheck.generators.GenBuilder;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;

public class TestPartialAssignmentFactory implements WithSolverCheck {

    // FROM
    @Test
    public void testFromList() {
        assertThat(forAll(listsOfDomains()).itIsTrueThat(lst ->
            lst.equals(PartialAssignmentFactory.from(lst))
        ));
    }
    @Test
    public void testFromArray() {
        assertThat(forAll(listsOfDomains()).itIsTrueThat(lst -> {
            Domain[] array = lst.toArray(new Domain[0]);
            return Arrays.asList(array).equals(PartialAssignmentFactory.from(array));
        }));
    }

    // ERROR
    @Test
    public void testError() {
        assertThat(forAll(integer("arity").between(0, 100))
       .itIsTrueThat(arity -> {
           PartialAssignment result = PartialAssignmentFactory.error(arity);

           return result.size() == arity
               && result.stream().allMatch(Domain::isEmpty);
       }));
    }

    @Test
    public void errorMustRejectNegativeArity() {
        assertThat(forAll(integer("arity").between(-100, -1))
        .itIsTrueThat(arity ->
              failsThrowing(
                 IllegalArgumentException.class,
                 () -> PartialAssignmentFactory.error(arity)))
        );
    }

    // RESTRICT
    @Test
    public void testRestrict() {
        assertThat(
           forAll(operator("op"), integer("value")).assertThat((op, value) ->
           forAll(partialAssignment()).itIsTrueThat(partialAssignment -> {

             int arity = partialAssignment.size();

             boolean ok = true;
             for (int var = 0; ok && var < arity; var++) {
                 PartialAssignment restricted = PartialAssignmentFactory.restrict(
                               partialAssignment, var, op, value);

                 List<Domain> computed =
                         new ArrayList<>(partialAssignment);

                 computed.set(
                         var,
                         Domain.restrict(partialAssignment.get(var), op, value));

                 ok &= computed.equals(restricted);
             }

             return ok;
          })));
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

    // COLLECTOR
    @Test
    public void testCollector() {
        assertThat(forAll(listsOfDomains()).itIsTrueThat(lst -> {
            PartialAssignment d = lst.stream().collect(PartialAssignmentFactory.collector());

            return d.containsAll(lst) && lst.containsAll(d);
        }));
    }

    // UNION OF
    @Test
    public void unionOfTheCartesianProductMustEqualOriginalPartialAssignmentWhenNoDomainIsEmpty() {
        assertThat(forAll(partialAssignment())
        .assuming(x -> x.stream().noneMatch(Domain::isEmpty))
        .itIsTrueThat(x ->
            x.equals(PartialAssignment.unionOf(x.size(),CartesianProduct.of(x)))
        ));
    }
    @Test
    public void unionOfTheCartesianProductMustYieldAnEmptyPartialAssignmentOfTheGivenAriry() {
        assertThat(forAll(partialAssignment())
            .assuming(PartialAssignment::isError)
            .itIsTrueThat(partialAssignment -> {
                    CartesianProduct<Integer> cp =
                        CartesianProduct.of(partialAssignment);

                    PartialAssignment pa =
                        PartialAssignmentFactory.unionOf(partialAssignment.size(), cp);

                    boolean sameSize = pa.size() == partialAssignment.size();
                    boolean allEmpty = pa.stream().allMatch(Domain::isEmpty);

                    return sameSize && allEmpty;
                }
            ));
    }


    private GenBuilder<List<Domain>> listsOfDomains() {
        return listOf("List of domains", domain().withValuesBetween(0, 10));
    }
}
