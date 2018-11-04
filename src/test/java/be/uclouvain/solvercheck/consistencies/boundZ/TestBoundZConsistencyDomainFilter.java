package be.uclouvain.solvercheck.consistencies.boundZ;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.DomainFilter;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import be.uclouvain.solvercheck.utils.collections.Range;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;

public class TestBoundZConsistencyDomainFilter implements WithSolverCheck {

    private Checker checker;
    private DomainFilter filter;

    @Before
    public void setUp() {
        checker= allDiff();
        filter = bcZDomain().apply(checker);
    }

    /**
     * 1. It must be weakly-monotonic
     * <i>
     * A function p over domains is called weakly monotonic iff
     *
     *     a ∈ d ⇒ p({a}) ⊆ p(d) for all assignments a and domains d.
     *
     * A propagator is a contracting and weakly monotonic function over domains
     * </i>
     *
     * ``Weakly Monotonic Propagators'' (Schulte, Tack, 2009)
     *
     * @throws Exception when it fails
     */
    @Test
    public void itMustBeWeaklyMonotonic() throws Exception {
        assertThat(
           forAnyPartialAssignment().itIsTrueThat(pa -> randomness -> {
              for (int var = 0; var < pa.size(); var++) {
                  Domain filtered = filter.filter(var, pa);

                  Assert.assertTrue(List.of(STRONGER, EQUIVALENT)
                        .contains(filtered.compareWith(pa.get(var))));
              }
          })
        );
    }

    /**
     * 2. It removes no solution
     */
    @Test
    public void itRemovesNoSolution() {
        assertThat(
           forAnyPartialAssignment().itIsTrueThat(pa -> randomness -> {
                   PartialAssignment solutions =
                      PartialAssignment.unionOf(pa.size(),
                         CartesianProduct.of(pa)
                            .stream()
                            .filter(a -> checker.test(Assignment.from(a)))
                            .collect(Collectors.toList())
                      );
                   for (int i = 0; i < pa.size(); i++) {
                       Domain filtered = filter.filter(i, pa);
                       Assert.assertTrue(filtered.containsAll(solutions.get(i)));
                   }
               })
          );
    }

    /**
     * 3. The min and max values of all domains, must each have a support
     */
    @Test
    public void testConsistencyDefinition() {
        assertThat(
           forAnyPartialAssignment().itIsTrueThat(pa -> randomness -> {
                   CartesianProduct<Integer> boundSupports =
                           CartesianProduct.of(
                               pa.stream().map(d ->
                                   Range.between(
                                           (long) d.minimum(),
                                           (long) d.maximum() + 1))
                           .collect(Collectors.toList()));

                   for (int i = 0; i < pa.size(); i++) {
                       final int var = i;
                       final Domain filtered = filter.filter(var, pa);

                       if (!filtered.isEmpty()) {
                           boolean minHasSupport =
                                   boundSupports.stream().anyMatch(ass ->
                                         ass.get(var).equals(filtered.minimum())
                                      && checker.test(Assignment.from(ass))
                                   );
                           Assert.assertTrue(minHasSupport);

                           boolean maxHasSupport =
                                   boundSupports.stream().anyMatch(ass ->
                                         ass.get(var).equals(filtered.minimum())
                                      && checker.test(Assignment.from(ass))
                                   );
                           Assert.assertTrue(maxHasSupport);
                       }
                   }
               })
      );
    }
}
