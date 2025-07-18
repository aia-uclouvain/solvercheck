package be.uclouvain.solvercheck.consistencies.arc;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;

public class TestArcConsistency
        implements WithSolverCheck {

    private Checker checker;
    private Filter  filter;

    @Before
    public void setUp() {
        checker= allDiff();
        filter = arcConsistent(checker);
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
        assertThat(
           forAll(partialAssignment()).assertThat(pa -> rnd -> {
               PartialAssignment filtered = filter.filter(pa);

               // subseteq test
               Assert.assertTrue(List.of(STRONGER, EQUIVALENT).contains(filtered.compareWith(pa)));
           })
        );
    }

    /**
     * 2. It must be the least fixpoint (hence an extra round of propagation
     * will not prune any additional value).
     */
    @Test
    public void itMustBeTheLeastFixpoint() {
        assertThat(
           forAll(partialAssignment()).assertThat(pa -> rnd -> {
              PartialAssignment filtered  = filter.filter(pa);
              PartialAssignment filtered2 = filter.filter(filtered);

              Assert.assertEquals(EQUIVALENT, filtered.compareWith(filtered2));
          })
        );
    }

    /**
     * 3. It removes no solution
     */
    @Test
    public void itRemovesNoSolution() {
        assertThat(
           forAll(partialAssignment()).assertThat(pa -> rnd -> {
              PartialAssignment filtered = filter.filter(pa);

              PartialAssignment solutions =
                      PartialAssignment.unionOf(pa.size(),
                              CartesianProduct.of(pa)
                                      .stream()
                                      .filter(a -> checker.test(Assignment.from(a)))
                                      .collect(Collectors.toList())
                      );

              boolean error = solutions.isError();
              boolean solsOk= List.of(STRONGER, EQUIVALENT)
                      .contains(solutions.compareWith(filtered));

              Assert.assertTrue(error || solsOk);
          })
        );
    }

    /**
     * 4. For all values of all domains, there must exist a support.
     */
    @Test
    public void testConsistencyDefinition() {
        assertThat(
           forAll(partialAssignment()).assertThat(pa -> rnd -> {
              PartialAssignment filtered  = filter.filter(pa);

              CartesianProduct<Integer> possibilities =
                      CartesianProduct.of(filtered);

              if (!filtered.isError()) {
                  int arity = filtered.size();

                  for (int i = 0; i < arity; i++) {
                      final int var = i;
                      final Domain domain = filtered.get(var);
                      for (int value : domain) {
                          boolean support = possibilities.stream().anyMatch(ass ->
                                  ass.get(var) == value && checker.test(Assignment.from(ass))
                          );
                          Assert.assertTrue(support);
                      }
                  }
              }
          })
        );
    }
}
