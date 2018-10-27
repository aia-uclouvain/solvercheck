package be.uclouvain.solvercheck.consistencies.arc;

import be.uclouvain.solvercheck.checkers.WithCheckers;
import be.uclouvain.solvercheck.consistencies.WithConsistencies;
import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.DomainFilter;
import be.uclouvain.solvercheck.generators.WithGenerators;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quicktheories.QuickTheory;
import org.quicktheories.WithQuickTheories;

import java.util.List;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;

public class TestArcConsistencyDomainFilter
        implements WithQuickTheories,
   WithGenerators,
        WithConsistencies,
                   WithCheckers {

    private QuickTheory qt;
    private Checker checker;
    private DomainFilter filter;

    @Before
    public void setUp() {
        qt = qt().withGenerateAttempts(10000);
        checker= allDiff();
        filter = acDomain().apply(checker);
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
     */
    @Test
    public void itMustBeWeaklyMonotonic() {
        qt.forAll(partialAssignments())
          .assuming(pa -> !pa.isError())
          .checkAssert(pa -> {
              for (int var = 0; var < pa.size(); var++) {
                  Domain filtered = filter.filter(var, pa);

                  Assert.assertTrue(List.of(STRONGER, EQUIVALENT)
                          .contains(filtered.compareWith(pa.get(var))));
              }
          });
    }

    /**
     * 2. It removes no solution
     */
    @Test
    public void itRemovesNoSolution() {
        qt.forAll(partialAssignments())
          .assuming(pa -> !pa.isError())
          .checkAssert(pa -> {
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
          });
    }

    /**
     * 3. For all values of the filtered domains, there must exist a support.
     */
    @Test
    public void testConsistencyDefinition() {
        qt.forAll(partialAssignments())
          .assuming(pa -> !pa.isError())
          .checkAssert(pa -> {

              CartesianProduct<Integer> possibilities = CartesianProduct.of(pa);

              for (int i = 0; i < pa.size(); i++) {
                  final int var = i;
                  final Domain filtered = filter.filter(var, pa);

                  if (!filtered.isEmpty()) {
                      for (int value : filtered) {
                          boolean support = possibilities.stream().anyMatch(ass ->
                                  ass.get(var) == value && checker.test(Assignment.from(ass))
                          );

                          Assert.assertTrue(support);
                      }
                  }
              }
          });
    }
}
