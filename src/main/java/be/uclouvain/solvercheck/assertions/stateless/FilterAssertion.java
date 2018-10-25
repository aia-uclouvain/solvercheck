package be.uclouvain.solvercheck.assertions.stateless;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.assertions.util.AbstractFluentConfig;
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
        extends AbstractFluentConfig<FilterAssertion>
        implements Predicate<PartialAssignment>, Assertion {

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
        super();
        this.actual = actual;
        this.other  = new ArcConsitency(Checkers.alwaysTrue());
        this.check  = x -> true;
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
        super(config);
        this.actual = actual;
        this.other  = new ArcConsitency(Checkers.alwaysTrue());
        this.check  = x -> true;
    }

    /** {@inheritDoc} */
    @Override
    protected FilterAssertion getThis() {
        return this;
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
        doCheck(this);
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
