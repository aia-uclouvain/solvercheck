package be.uclouvain.solvercheck.consistencies.utils;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.consistencies.ConsistencyUtil;
import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.generators.WithGenerators;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import be.uclouvain.solvercheck.utils.collections.Range;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;

public class TestConsistencyUtil implements WithSolverCheck, WithGenerators {


    @Test
    public void supportsIsTheCartesianProductOfDomains() {
        forAnyPartialAssignment(partialAssignment ->
            ConsistencyUtil
                .support(partialAssignment)
                .equals(CartesianProduct.of(partialAssignment))
        );
    }

    @Test
    public void boundSupportIsTheSetOfSupportsWhereEachDomainIsReplacedByARange() {
        forAnyPartialAssignment(
                partialAssignment -> !partialAssignment.isError(),
                partialAssignment ->
          ConsistencyUtil
            .boundSupport(partialAssignment)
            .equals(ConsistencyUtil.support(
               partialAssignment.stream()
                 .map(dom -> Range.between(dom.minimum(), 1L + dom.maximum()))
                 .map(Domain::from)
                 .collect(Collectors.toList())
            ))
        );
    }

    @Test
    public void boundSupportThrowsAnExceptionWhenItsInputIsAnError() {
        forAnyPartialAssignment(
          partialAssignment -> partialAssignment.isError(),
          partialAssignment ->
              failsThrowing(
                NoSuchElementException.class,
                () -> ConsistencyUtil.boundSupport(partialAssignment)
              )
        );
    }

    @Test
    public void shrinkBounds() {
        assertThat(
            forAll(
               integer("X").between(0, 10),
               integer("Y").between(0, 10))
            .assuming((x, y) -> x <= y )
            .assertThat((x, y) -> randomness ->
                forAnyPartialAssignment(partialAssignment -> {
                    boolean ok = true;
                    int arity = partialAssignment.size();

                    for (int i = 0; ok && i < arity; i++) {
                        Domain original= partialAssignment.get(i);
                        Domain shrunk  = ConsistencyUtil.shrinkBounds(
                                i,
                                original,
                                a -> a.stream().allMatch(v -> x <= v && v <= y),
                                ConsistencyUtil.support(partialAssignment)
                        );

                        // it invents nothing
                        ok &= partialAssignment.get(i).containsAll(shrunk);
                        // it is weakly monotonic contracting
                        ok &= List.of(EQUIVALENT, STRONGER).contains(
                                shrunk.compareWith(original));
                        // all values in the domain satisfy the enforced
                        // condition
                        for (int value : shrunk) {
                            ok &= (x <= value && value <= y);
                        }
                    }
                    return ok;
                })
            )
        );
    }

    @Test
    public void testExistsSupport() {
        forAnyPartialAssignment(partialAssignment -> {
            boolean ok = true;
            int arity = partialAssignment.size();


            Collection<List<Integer>> supports =
                    ConsistencyUtil.support(partialAssignment);

            for (int i = 0; ok && i < arity; i++) {
                final int var = i;
                for (int j : partialAssignment.get(var)) {
                    final int val = j;
                    boolean hasSupport = ConsistencyUtil
                            .exists(supports)
                            .satisfying(allDiff())
                            .forVariable(var)
                            .assignedTo(val);

                    boolean foundSupport =
                            supports.stream().anyMatch(a ->
                                a.get(var) == val
                             && allDiff().test(Assignment.from(a)));

                    ok &= (hasSupport == foundSupport);
                }
            }
            return ok;
        });
    }

    private void forAnyPartialAssignment(final Predicate<PartialAssignment> actual) {
        forAnyPartialAssignment(x -> true, actual);
    }

    private void forAnyPartialAssignment(
            final Predicate<PartialAssignment> assumptions,
            final Predicate<PartialAssignment> actual) {

        assertThat(
           forAll(partialAssignment())
               .assuming(assumptions)
               .assertThat(pa -> rnd -> actual.test(pa))
        );
    }

}
