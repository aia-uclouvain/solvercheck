package be.uclouvain.solvercheck.consistencies.hybrid;

import be.uclouvain.solvercheck.assertions.ForAnyPartialAssignment;
import be.uclouvain.solvercheck.checkers.WithCheckers;
import be.uclouvain.solvercheck.consistencies.ArcConsitency;
import be.uclouvain.solvercheck.consistencies.BoundDConsistency;
import be.uclouvain.solvercheck.consistencies.BoundZConsistency;
import be.uclouvain.solvercheck.consistencies.RangeConsistency;
import be.uclouvain.solvercheck.consistencies.WithConsistencies;
import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.DomainFilter;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.generators.WithCpGenerators;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Before;
import org.junit.Test;
import org.quicktheories.QuickTheory;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestHybridConsistency
        implements WithQuickTheories,
        WithCpGenerators,
        WithConsistencies,
        WithCheckers {

    private static final List<DomainFilterProducer> DOMAIN_FILTERS =
        List.of(
           new ProducerAdapter("AC", ArcConsitency::domainFilter),
           new ProducerAdapter("BC(D)", BoundDConsistency::domainFilter),
           new ProducerAdapter("BC(Z)", BoundZConsistency::domainFilter),
           new ProducerAdapter("Range", RangeConsistency::domainFilter)
        );

    private QuickTheory qt;
    private Checker checker;

    @Before
    public void setUp() {
        qt = qt().withGenerateAttempts(10000);
        checker= allDiff();
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
        testHybridConsistency((domainFilters, partialAssignment) -> {
            Filter filter = hybrid(checker, domainFilters);
            PartialAssignment filtered = filter.filter(partialAssignment);

            assertTrue(List
                    .of(STRONGER, EQUIVALENT)
                    .contains(filtered.compareWith(partialAssignment)));
        });
    }

    /**
     * 2. It must be the least fixpoint (hence an extra round of propagation
     * will not prune any additional value).
     */
    @Test
    public void itMustBeTheLeastFixpoint() {
        testHybridConsistency((domainFilters, partialAssignment) -> {
            Filter filter = hybrid(checker, domainFilters);
            PartialAssignment filtered1 = filter.filter(partialAssignment);
            PartialAssignment filtered2 = filter.filter(filtered1);

            assertEquals(EQUIVALENT, filtered1.compareWith(filtered2));
        });
    }

    /**
     * 3. It removes no solution
     */
    @Test
    public void itRemovesNoSolution() {
        testHybridConsistency((domainFilters, partialAssignment) -> {
            Filter filter = hybrid(checker, domainFilters);
            PartialAssignment filtered  = filter.filter(partialAssignment);
            PartialAssignment solutions = allSolutions(partialAssignment);

            assertTrue(solutions.isError()
                || List.of(STRONGER, EQUIVALENT)
                       .contains(solutions.compareWith(filtered)));
        });
    }

    /**
     * 4. All values of all domains, must have a bound support
     */
    @Test
    public void testConsistencyDefinition() {
        testHybridConsistency((domainFilters, partialAssignment) -> {
            Filter filter = hybrid(checker, domainFilters);
            PartialAssignment filtered  = filter.filter(partialAssignment);

            for (int i = 0; i < domainFilters.length; i++) {
              DomainFilter df = domainFilters[i].apply(checker);

              // it can be stronger than DF (because of other consistencies)
              // but it can certainly never be any weaker (or incomparable
              // to) it.
              assertTrue(
                df.filter(i,partialAssignment).containsAll(filtered.get(i))
              );
            }
        });
    }

    /**
     * 5. Whenever it is passed a failed partial assignment, it should return
     * an empty one (of the right arity).
     */
    @Test
    public void wheneverItIsPassedAFailedPAItShouldReturnEmptySolution() {
        testHCForErrors((domainFilters, partialAssignment) -> {
            Filter filter = hybrid(checker, domainFilters);
            PartialAssignment filtered = filter.filter(partialAssignment);

            assertEquals(filtered.size(), partialAssignment.size());
            assertTrue(filtered.stream().allMatch(Domain::isEmpty));
        });
    }

    private void testHybridConsistency(final HybridConsistencyCheck actual) {
        testHCWithAssumptions(
            partialAssignment -> !partialAssignment.isError(),
            actual
        );
    }

    private void testHCForErrors(final HybridConsistencyCheck actual) {
        testHCWithAssumptions(
            partialAssignment -> partialAssignment.isError(),
            actual
        );
    }

    private void testHCWithAssumptions(
            final Predicate<PartialAssignment> assumptions,
            final HybridConsistencyCheck actual) {
        new ForAnyPartialAssignment()
             .assuming(assumptions)
             .checkAssert(partialAssignment ->
                qt//.withFixedSeed(23691073021520L)
                  .withExamples(10)
                  .forAll(
                     lists()
                       .of(domainFilters())
                       .ofSize(partialAssignment.size()))
                  .checkAssert(domainFilters ->
                     actual.test(
                       domainFilters.toArray(new DomainFilterProducer[0]),
                       partialAssignment)
                  )
             );
    }

    private Gen<DomainFilterProducer> domainFilters() {
        return integers()
                .between(0, DOMAIN_FILTERS.size()-1)
                .map(DOMAIN_FILTERS::get);
    }

    private PartialAssignment allSolutions(PartialAssignment pa) {
        return PartialAssignment.unionOf(pa.size(),
                    CartesianProduct.of(pa)
                        .stream()
                        .filter(a -> checker.test(Assignment.from(a)))
                        .collect(Collectors.toList()));
    }

    @FunctionalInterface
    private interface HybridConsistencyCheck {
        void test(
                DomainFilterProducer[] filters,
                PartialAssignment pa);
    }

    @FunctionalInterface
    private interface DomainFilterProducer
            extends Function<Checker, DomainFilter> {
    }

    private static final class ProducerAdapter implements DomainFilterProducer {

        private final String name;
        private final DomainFilterProducer decorated;

        /* default */ ProducerAdapter(
                final String name,
                final DomainFilterProducer producer) {
            this.name = name;
            this.decorated = producer;
        }

        @Override
        public DomainFilter apply(final Checker c) {
            return decorated.apply(c);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
