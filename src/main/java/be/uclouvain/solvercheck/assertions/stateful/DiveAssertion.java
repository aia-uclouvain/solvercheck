package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.assertions.TestConfiguration;
import be.uclouvain.solvercheck.checkers.Checkers;
import be.uclouvain.solvercheck.consistencies.ArcConsitency;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.StatefulFilter;
import be.uclouvain.solvercheck.core.task.impl.StatefulFilterAdapter;
import be.uclouvain.solvercheck.generators.Generators;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;
import org.quicktheories.generators.SourceDSL;
import org.quicktheories.impl.Distribution;
import org.quicktheories.impl.Distributions;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.quicktheories.QuickTheory.qt;

@SuppressWarnings("checkstyle:hiddenfield")
public final class DiveAssertion implements Assertion {
    /**
     * The actual test configuration to use when evaluating this assertion.
     */
    private final TestConfiguration config;

    /** The actual StatefulFilter being tested. */
    private final StatefulFilter actual;

    /**
     * The condition effectively being checked when testing some partial
     * assignment.
     */
    private Supplier<Boolean> check;

    /** The reference StatefulFilter with which to compare the resuts. */
    private StatefulFilter other;

    /**
     * The generator used to produce the randomly generated partial assignments.
     */
    private Gen<PartialAssignment> generator;

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     * @param actual the Filter whose property is being evaluated.
     */
    public DiveAssertion(final StatefulFilter actual) {
        this(new TestConfiguration(), actual);
    }

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     *
     * @param config the test configuration to use when evaluating this
     *               property.
     * @param actual the Filter whose property is being evaluated.
     */
    public DiveAssertion(final TestConfiguration config,
                         final StatefulFilter actual) {
        this.config    = config;
        this.actual    = actual;
        this.check     = () -> true;
        this.other     = defaultOther();
        this.generator = Generators.partialAssignments();
    }


    /**
     * Configure the assertion to check that both `actual` and `other` have
     * always the same propagating strengths.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public DiveAssertion isEquivalentTo(final StatefulFilter other) {
        this.other = other;
        this.check = this::checkEquivalent;

        return this;
    }

    /**
     * Configure the assertion to check that `actual` and `other` have
     * strictly incomparable propagating strengths.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public DiveAssertion isIncomparableTo(final StatefulFilter other) {
        this.other = other;
        this.check = this::checkIncomparable;

        return this;
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is weaker or equivalent to that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public DiveAssertion isWeakerThan(final StatefulFilter other) {
        this.other = other;
        this.check = this::checkWeaker;

        return this;
    }
    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is strictly weaker than that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public DiveAssertion isStrictlyWeakerThan(final StatefulFilter other) {
        this.other = other;
        this.check = this::checkStrictlyWeaker;

        return this;
    }
    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is stronger or equivalent to that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public DiveAssertion isStrongerThan(final StatefulFilter other) {
        this.other = other;
        this.check = this::checkStronger;

        return this;
    }
    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is strictly stronger than that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public DiveAssertion isStrictlyStrongerThan(final StatefulFilter other) {
        this.other = other;
        this.check = this::checkStrictlyStronger;

        return this;
    }

    /**
     * Returns a FilterAssertion (an assertion whose generator can be further
     * constrained with assumptions).
     *
     * @param generator the generator to use in order to generate random
     *                  partial assignements.
     * @return a filter assertion which whose generator can be configured.
     */
    public DiveAssertion forAll(final Gen<PartialAssignment> generator) {
        this.generator = generator;

        return this;
    }

    /**
     * Constrains the values generated by the generator with the given
     * assumption.
     *
     * @param assumption a predicate indicating whether or not a generated
     *                   partial assignment should be considered while
     *                   generating random tests.
     * @return this
     */
    public DiveAssertion assuming(final Predicate<PartialAssignment> assumption) {
        this.generator = generator.assuming(assumption);

        return this;
    }

    /** {@inheritDoc} */
    public void check() {
        final Strategy strategy = config.get();

        qt(() -> strategy)
            .forAll(generator)
            .checkAssert(root -> dive(strategy, root).run());
    }

    /**
     * Checks that `actual` and `other` have equivalent propagating strengths
     * for the given `domains` test case.
     *
     * @return true iff actual is equivalent to other
     */
    private boolean checkEquivalent() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.EQUIVALENT;
    }

    /**
     * Checks that `actual` and `other` have incomparable propagating strengths
     * for the given `domains` test case.
     *
     * @return true iff actual is incomparable to other
     */
    private boolean checkIncomparable() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.EQUIVALENT;
    }
    /**
     * Checks that `actual` is weaker or equivalent to `other` for the given
     * `domains` test case.
     *
     * @return true iff actual is weaker or equivalent to other
     */
    private boolean checkWeaker() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.WEAKER
            || comparison == PartialOrdering.EQUIVALENT;
    }
    /**
     * Checks that `actual` is weaker than `other` for the given `domains`
     * test case.
     *
     * @return true iff actual is weaker than other
     */
    private boolean checkStrictlyWeaker() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.WEAKER;
    }
    /**
     * Checks that `actual` is stronger or equivalent to `other` for the given
     * `domain` test case.
     *
     * @return true iff actual is stronger or equivalent to other
     */
    private boolean checkStronger() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.STRONGER
            || comparison == PartialOrdering.EQUIVALENT;
    }

    /**
     * Checks that `actual` is stronger than `other` for the given `domains`
     * test case.
     *
     * @return true iff actual is stronger than other
     */
    private boolean checkStrictlyStronger() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.STRONGER;
    }
    private Dive dive(final Strategy strategy, final PartialAssignment root) {
        return new Dive(
                actual,
                other,
                check,
                variablesSupplier(strategy, root),
                valuesSupplier(strategy, root),
                operatorSupplier(strategy),
                backtrackSupplier(strategy),
                root
        );
    }
    private Supplier<Integer> variablesSupplier(
            final Strategy strategy,
            final PartialAssignment forDomains) {

        final Distribution<Integer> distrib =
                Distributions.boundarySkewed(
                        strategy,
                        SourceDSL.integers().between(0, forDomains.size() - 1));

        return () -> Distributions.nextValue(distrib);
    }
    private Function<Integer, Integer> valuesSupplier(
            final Strategy strategy,
            final PartialAssignment forDomains) {

        return xi -> Distributions.nextValue(
                        valuesDistrib(strategy, forDomains, xi));
    }
    private Distribution<Integer> valuesDistrib(
            final Strategy strategy,
            final PartialAssignment inDomains,
            final int xi) {

        Domain dxi = inDomains.get(xi);
        return Distributions.random(
                strategy,
                SourceDSL.integers().between(dxi.minimum(), dxi.maximum()));
    }
    private Supplier<Operator> operatorSupplier(final Strategy strategy) {
        final Distribution<Operator> distrib =
                Distributions.random(strategy, Generators.operators());

        return () -> Distributions.nextValue(distrib);
    }
    private Supplier<Boolean> backtrackSupplier(final Strategy strategy) {
        final Distribution<Boolean> distrib =
                Distributions.random(strategy, SourceDSL.booleans().all());

        return () -> Distributions.nextValue(distrib);
    }


    private static StatefulFilter defaultOther() {
        return new StatefulFilterAdapter(
                new ArcConsitency(
                        Checkers.alwaysTrue()));
    }
}
