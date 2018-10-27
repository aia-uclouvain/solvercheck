package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.assertions.util.ForAnyPartialAssignment;
import be.uclouvain.solvercheck.core.data.impl.PartialAssignmentFactory;
import be.uclouvain.solvercheck.generators.WithGenerators;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;

public class TestPartialAssignmentFactory
        implements WithQuickTheories, WithGenerators {

    // FROM
    @Test
    public void testFromList() {
        qt().forAll(lists().of(domains()).ofSizeBetween(0, 1000))
            .check(lst ->
                lst.equals(PartialAssignmentFactory.from(lst))
            );
    }
    @Test
    public void testFromArray() {
        qt().forAll(
           arrays().ofClass(domains(), Domain.class).withLengthBetween(0, 1000))
           .check(array ->
              Arrays.asList(array).equals(PartialAssignmentFactory.from(array))
           );
    }

    // ERROR
    @Test
    public void testError() {
        qt().forAll(integers().between(0, 1000))
           .check(arity -> {
               PartialAssignment result = PartialAssignmentFactory.error(arity);

               return result.size() == arity
                   && result.stream().allMatch(Domain::isEmpty);
           });
    }
    @Test
    public void errorMustRejectNegativeArity() {
        qt().forAll(integers().between(-1000, -1))
           .check(arity ->
              failsThrowing(
                 IllegalArgumentException.class,
                 () -> PartialAssignmentFactory.error(arity)));
    }

    // RESTRICT
    @Test
    public void testRestrict() {
        qt().forAll(operators(), integers().all()).checkAssert((op, value) ->
          new ForAnyPartialAssignment().check(partialAssignment -> {

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

    // COLLECTOR
    @Test
    public void testCollector() {
        qt().forAll(lists().of(domains()).ofSizeBetween(0, 100))
            .check(lst -> {
                PartialAssignment d = lst.stream().collect(PartialAssignmentFactory.collector());

                return d.containsAll(lst) && lst.containsAll(d);
            });
    }

    // UNION OF
    @Test
    public void unionOfTheCartesianProductMustEqualOriginalPartialAssignmentWhenNoDomainIsEmpty() {
        qt().forAll(partialAssignments().build())
            .assuming(x -> x.stream().noneMatch(Domain::isEmpty))
            .check   (x ->
                x.equals(PartialAssignment.unionOf(
                        x.size(),
                        CartesianProduct.of(x)))
            );
    }
    @Test
    public void unionOfTheCartesianProductMustYieldAnEmptyPartialAssignmentOfTheGivenAriry() {
        new ForAnyPartialAssignment()
            .assuming(PartialAssignment::isError)
            .check(partialAssignment -> {
                    CartesianProduct<Integer> cp =
                        CartesianProduct.of(partialAssignment);

                    PartialAssignment pa =
                        PartialAssignmentFactory.unionOf(partialAssignment.size(), cp);

                    boolean sameSize = pa.size() == partialAssignment.size();
                    boolean allEmpty = pa.stream().allMatch(Domain::isEmpty);

                    return sameSize && allEmpty;
                }
            );
    }
}
