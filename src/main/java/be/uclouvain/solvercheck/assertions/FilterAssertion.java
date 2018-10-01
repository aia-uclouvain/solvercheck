package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.checkers.Checkers;
import be.uclouvain.solvercheck.consistencies.ArcConsitency;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;
import org.quicktheories.core.Strategy;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This is the class that implements the assertions relative to the various
 * Filter algorithm.
 */
// Given that this class behaves as a builder, it is ok to shadow fields in
// certain (setter) methods.
@SuppressWarnings("checkstyle:hiddenfield")
public final class FilterAssertion
        implements Predicate<PartialAssignment>, Assertion {

    /**
     * The configuration telling how to explore the set of possible partial
     * assignments.
     */
    private final ForAnyPartialAssignment config;

    /** The actual filter being tested. */
    private final Filter actual;

    /**
     * The reference filter with which to compare the resuts.
     */
    private Filter other;

    /**
     * The condition effectively being checked when testing some partial
     * assignment.
     */
    private Predicate<PartialAssignment> check;

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     *
     * @param actual the Filter whose property is being evaluated.
     */
    public FilterAssertion(final Filter actual) {
        this(new ForAnyPartialAssignment(), actual);
    }

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     *
     * @param config the initial configuration of the test case (can be
     *               customized)
     * @param actual the Filter whose property is being evaluated.
     */
    public FilterAssertion(final Supplier<Strategy> config,
                           final Filter actual) {
        this.config = new ForAnyPartialAssignment(config);
        this.actual = actual;
        this.other  = new ArcConsitency(Checkers.alwaysTrue());
        this.check  = x -> true;
    }

    /**
     * Configure the assertion to check that both `actual` and `other` have
     * always the same propagating strengths.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public FilterAssertion isEquivalentTo(final Filter other) {
        this.other = other;
        this.check = this::checkEquivalent;

        return this;
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is weaker or equivalent to that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public FilterAssertion isWeakerThan(final Filter other) {
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
    public FilterAssertion isStrictlyWeakerThan(final Filter other) {
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
    public FilterAssertion isStrongerThan(final Filter other) {
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
    public FilterAssertion isStrictlyStrongerThan(final Filter other) {
        this.other = other;
        this.check = this::checkStrictlyStronger;

        return this;
    }

    /** {@inheritDoc} */
    public boolean test(final PartialAssignment domains) {
        return this.check.test(domains);
    }

    /** {@inheritDoc} */
    public void check() {
        config.check(this);
    }

    /**
     * Makes the interface of the filter assertion more fluent.
     *
     * @return this.
     */
    public FilterAssertion forAnyPartialAssignment() {
        return this;
    }

    /**
     * Configures the seed of the PRNG used to pseudo-randomly generate partial
     * assignments, anchors and values.
     *
     * @param seed the seed to use to initialize the PRNG
     * @return this
     */
    public FilterAssertion withFixedSeed(final long seed) {
        config.withFixedSeed(seed);

        return this;
    }

    /**
     * Configures the underlying quicktheories layer to try to generate a
     * partial assignment satisfying the assumptions at least `attempts` time
     * before failing on value exhaustion.
     *
     * @param attempts the number of attempts to try before value exhaustion.
     * @return this
     */
    public FilterAssertion withGenerateAttempts(final int attempts) {
        config.withGenerateAttempts(attempts);

        return this;
    }

    /**
     * Configures the desired number of anchors which are picked to seed a
     * round of partial assignment tests.
     *
     * @param n the number of anchor values to generate.
     * @return this
     */
    public FilterAssertion withAnchorSamples(final int n) {
        config.withAnchorSamples(n);

        return this;
    }

    /**
     * Configures the number of tests which are generated for each anchor value.
     *
     * @param n the number of example partial assignments produced for each
     *          anchor value.
     * @return this
     */
    public FilterAssertion withExamples(final int n) {
        config.withAnchorSamples(n);

        return this;
    }

    /**
     * Configures the number of shrink cycles used by the underlying
     * quicktheories layer in order to determine the smallest possible
     * violation instances.
     *
     * @param cycles the number of shrink cycles to use.
     * @return this
     */
    public FilterAssertion withShrinkCycles(final int cycles) {
        config.withShrinkCycles(cycles);

        return this;
    }

    /**
     * Configures the range of values which can appear in the partial
     * assignments.
     *
     * <div>
     *     <h1>Note</h1>
     *     The range of values must be expressed with x being smaller or
     *     equal to y. Any other combination will be rejected.
     * </div>
     *
     * @param x the lowest value that can possibly appear in a partial
     *          assignment.
     * @param y the highest value that can possibly appear in a partial
     *          assignment.
     * @return this
     */
    public FilterAssertion withValuesBetween(final int x, final int y) {
        config.withValuesBetween(x, y);

        return this;
    }

    /**
     * Configures the maximum spread between any two values appearing in the
     * partial assignment.
     *
     * @param n the maximum allowed spread (must be positive)
     * @return this
     */
    public FilterAssertion spreading(final int n) {
        config.spreading(n);

        return this;
    }

    /**
     * Configures the maximum size of the domains composing the partial
     * assignments.
     *
     * @param n the maximum allowed domain size
     * @return this
     */
    public FilterAssertion withDomainsOfSizeUpTo(final int n) {
        config.withDomainsOfSizeUpTo(n);

        return this;
    }

    /**
     * Configures the size of the generated partial assignments. All
     * resulting PAs will have that exact size.
     *
     * @param x the exact target size (must be positive).
     * @return this
     */
    public FilterAssertion ofSize(final int x) {
        config.ofSize(x);

        return this;
    }

    /**
     * Configures the minimum and maximum size of the generated partial
     * assignments. All resulting PAs will comprise at least x variables and
     * at most y.
     *
     * @param x minimum number of variables in the generated partial
     *          assignments.
     * @param y maximum number of variables in the generated partial
     *          assignments.
     * @return this
     */
    public FilterAssertion ofSizeBetween(final int x, final int y) {
        config.ofSizeBetween(x, y);

        return this;
    }

    /**
     * Enforces the given predicate as an assumption on the generated partial
     * assignments.
     *
     * @param assumption the assumption which must be satisfied
     * @return this
     */
    public FilterAssertion assuming(final Predicate<PartialAssignment> assumption) {
        config.assuming(assumption);

        return this;
    }

    /**
     * Checks that `actual` and `other` have equivalent propagating strengths
     * for the given `domains` test case.
     *
     * @param domains the randomly generated test case which is to be fed to
     *                the compared filters.
     * @return true iff actual is equivalent to other
     */
    private boolean checkEquivalent(final PartialAssignment domains) {
        final PartialOrdering comparison =
                actual.filter(domains).compareWith(other.filter(domains));

        return comparison == PartialOrdering.EQUIVALENT;
    }

    /**
     * Checks that `actual` is weaker or equivalent to `other` for the given
     * `domains` test case.
     *
     * @param domains the randomly generated test case which is to be fed to
     *                the compared filters.
     * @return true iff actual is weaker or equivalent to other
     */
    private boolean checkWeaker(final PartialAssignment domains) {
        final PartialOrdering comparison =
                actual.filter(domains).compareWith(other.filter(domains));

        return comparison == PartialOrdering.WEAKER
            || comparison == PartialOrdering.EQUIVALENT;
    }
    /**
     * Checks that `actual` is weaker than `other` for the given `domains`
     * test case.
     *
     * @param domains the randomly generated test case which is to be fed to
     *                the compared filters.
     * @return true iff actual is weaker than other
     */
    private boolean checkStrictlyWeaker(final PartialAssignment domains) {
        final PartialOrdering comparison =
                actual.filter(domains).compareWith(other.filter(domains));

        return comparison == PartialOrdering.WEAKER;
    }
    /**
     * Checks that `actual` is stronger or equivalent to `other` for the given
     * `domain` test case.
     *
     * @param domains the randomly generated test case which is to be fed to
     *                the compared filters.
     * @return true iff actual is stronger or equivalent to other
     */
    private boolean checkStronger(final PartialAssignment domains) {
        final PartialOrdering comparison =
                actual.filter(domains).compareWith(other.filter(domains));

        return comparison == PartialOrdering.STRONGER
            || comparison == PartialOrdering.EQUIVALENT;
    }

    /**
     * Checks that `actual` is stronger than `other` for the given `domains`
     * test case.
     *
     * @param domains the randomly generated test case which is to be fed to
     *                the compared filters.
     * @return true iff actual is stronger than other
     */
    private boolean checkStrictlyStronger(final PartialAssignment domains) {
        final PartialOrdering comparison =
                actual.filter(domains).compareWith(other.filter(domains));

        return comparison == PartialOrdering.STRONGER;
    }
}
