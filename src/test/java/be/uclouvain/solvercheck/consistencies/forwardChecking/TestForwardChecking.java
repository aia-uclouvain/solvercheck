package be.uclouvain.solvercheck.consistencies.forwardChecking;

import be.uclouvain.solvercheck.checkers.WithCheckers;
import be.uclouvain.solvercheck.consistencies.WithConsistencies;
import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.Filter;
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

/**
 * This class tests the forward checking consistency propagator.
 */
public class TestForwardChecking
            implements WithQuickTheories,
   WithGenerators,
                       WithCheckers,
                       WithConsistencies {


    private QuickTheory qt;
    private Checker checker;
    private Filter filter;

    @Before
    public void setUp() {
        qt = qt().withGenerateAttempts(10000);
        checker= allDiff();
        filter = forwardChecking(checker);
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
               PartialAssignment filtered = filter.filter(pa);

               // subseteq test
               Assert.assertTrue(List.of(STRONGER, EQUIVALENT).contains(filtered.compareWith(pa)));
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
           });
    }

    /**
     * 3. It is only effective when all but one variables are fixed
     */
    @Test
    public void isIsOnlyEffectiveWhenAllButOneVariablesAreFixed() {
        qt.forAll(partialAssignments())
           .assuming(pa -> !pa.isError())
           .checkAssert(pa -> {
               final long nbUnassigned =
                  pa.stream().filter(dom -> dom.size() > 1).count();

               PartialAssignment actual = filter.filter(pa);

               PartialAssignment expected = null;
               if (nbUnassigned > 1) {
                   expected = pa;
               } else {
                   expected = arcConsistent(checker).filter(pa);
               }
               Assert.assertEquals(expected, actual);
           });
    }
}
