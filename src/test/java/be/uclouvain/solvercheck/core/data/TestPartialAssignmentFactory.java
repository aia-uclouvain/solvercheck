package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.impl.PartialAssignmentFactory;
import be.uclouvain.solvercheck.generators.GenBuilder;
import be.uclouvain.solvercheck.fuzzing.Generator;
import be.uclouvain.solvercheck.fuzzing.Randomness;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static java.util.stream.Collectors.toList;

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
        assertThat(forAll(integers("arity").between(0, 100))
       .itIsTrueThat(arity -> {
           PartialAssignment result = PartialAssignmentFactory.error(arity);

           return result.size() == arity
               && result.stream().allMatch(Domain::isEmpty);
       }));
    }

    @Test
    public void errorMustRejectNegativeArity() {
        assertThat(forAll(integers("arity").between(-100, -1))
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
           forAll(operators("op"), integers("value"))
           .assertThat((op, value) -> forAnyPartialAssignment().itIsTrueThat(
              partialAssignment -> {

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
        assertThat(forAll(partialAssignments())
        .assuming(x -> x.stream().noneMatch(Domain::isEmpty))
        .itIsTrueThat(x ->
            x.equals(PartialAssignment.unionOf(x.size(),CartesianProduct.of(x)))
        ));
    }
    @Test
    public void unionOfTheCartesianProductMustYieldAnEmptyPartialAssignmentOfTheGivenAriry() {
        assertThat(forAnyPartialAssignment()
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
        return new GenBuilder<List<Domain>>("List of domains") {
            @Override
            public Generator<List<Domain>> build() {
                return new Generator<List<Domain>>() {
                    @Override
                    public String name() {
                        return null;
                    }

                    @Override
                    public Stream<List<Domain>> generate(Randomness randomness) {
                        return randomness.intsBetween(0, 10)
                           .mapToObj(size ->
                              domains()
                                 .build()
                                 .generate(randomness)
                                 .limit(size)
                                 .collect(toList()));
                    }
                };
            }
        };
    }
}
