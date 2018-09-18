package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.generators.Generators;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;
import org.quicktheories.QuickTheory;
import org.quicktheories.core.Gen;

import java.util.function.Predicate;

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
     * The actual test configuration to use when evaluating this assertion.
     */
    private final TestConfiguration config;

    /** The actual filter being tested. */
    private final Filter actual;

    /**
     * The condition effectively being checked when testing some partial
     * assignment.
     */
    private Predicate<PartialAssignment> check;

    /**
     * The reference filter with which to compare the resuts.
     */
    private Filter other;

    /**
     * The generator used to produce the randomly generated partial assignments.
     */
    private Gen<PartialAssignment> generator;

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     * @param actual the Filter whose property is being evaluated.
     */
    public FilterAssertion(final Filter actual) {
        this(new TestConfiguration(), actual);
    }

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     *
     * @param config the test configuration to use when evaluating this
     *               property.
     * @param actual the Filter whose property is being evaluated.
     */
    public FilterAssertion(final TestConfiguration config,
                           final Filter actual) {
        this.config    = config;
        this.actual    = actual;
        this.generator = Generators.partialAssignments();
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
     * Configure the assertion to check that `actual` and `other` have
     * strictly incomparable propagating strengths.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public FilterAssertion isIncomparableTo(final Filter other) {
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

    /**
     * Returns a FilterAssertion (an assertion whose generator can be further
     * constrained with assumptions).
     *
     * @param generator the generator to use in order to generate random
     *                  partial assignements.
     * @return a filter assertion which whose generator can be configured.
     */
    public FilterAssertion forAll(final Gen<PartialAssignment> generator) {
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
    public FilterAssertion assuming(final Predicate<PartialAssignment> assumption) {
        this.generator = generator.assuming(assumption);

        return this;
    }


    /** {@inheritDoc} */
    public boolean test(final PartialAssignment domains) {
        return this.check.test(domains);
    }

    /** {@inheritDoc} */
    public void check() {
        QuickTheory.qt(config).forAll(generator).check(this);
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
     * Checks that `actual` and `other` have incomparable propagating strengths
     * for the given `domains` test case.
     *
     * @param domains the randomly generated test case which is to be fed to
     *                the compared filters.
     * @return true iff actual is incomparable to other
     */
    private boolean checkIncomparable(final PartialAssignment domains) {
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
