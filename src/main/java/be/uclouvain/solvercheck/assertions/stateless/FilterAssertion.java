package be.uclouvain.solvercheck.assertions.stateless;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.assertions.util.AbstractFluentConfig;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import org.quicktheories.core.Strategy;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static be.uclouvain.solvercheck.assertions.stateless.StatelessProperties.contracting;
import static be.uclouvain.solvercheck.assertions.stateless.StatelessProperties.equivalentTo;
import static be.uclouvain.solvercheck.assertions.stateless.StatelessProperties.idempotent;
import static be.uclouvain.solvercheck.assertions.stateless.StatelessProperties.strictlyStrongerThan;
import static be.uclouvain.solvercheck.assertions.stateless.StatelessProperties.strictlyWeakerThan;
import static be.uclouvain.solvercheck.assertions.stateless.StatelessProperties.strongerThan;
import static be.uclouvain.solvercheck.assertions.stateless.StatelessProperties.weakerThan;
import static be.uclouvain.solvercheck.assertions.stateless.StatelessProperties.weaklyMonotonic;

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
        this.check  = x -> true;
    }

    /**
     * Configures the assertion `check` with the arbitrarily specified property.
     *
     * @param property the property that must hold for this assertion to be
     *                 valid
     * @return this
     */
    public FilterAssertion is(final Predicate<PartialAssignment> property) {
        this.check = property;
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
        return is(equivalentTo(actual, other));
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is weaker or equivalent to that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public FilterAssertion isWeakerThan(final Filter other) {
        return is(weakerThan(actual, other));
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is strictly weaker than that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public FilterAssertion isStrictlyWeakerThan(final Filter other) {
        return is(strictlyWeakerThan(actual, other));
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is stronger or equivalent to that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public FilterAssertion isStrongerThan(final Filter other) {
        return is(strongerThan(actual, other));
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is strictly stronger than that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public FilterAssertion isStrictlyStrongerThan(final Filter other) {
        return is(strictlyStrongerThan(actual, other));
    }

    /**
     * Configures the assertion to check that the `actual` propagator is
     * contracting. That is to say:
     * $\forall d \in PartialAssignments : actual(d) \subseteq d$
     *
     * @return this
     */
    public FilterAssertion isContracting() {
        return is(contracting(actual));
    }

    /**
     * Configures the assertion to check that the `actual` propagator is
     * contracting. That is to say:
     * $\forall d \in PartialAssignments : actual(actual(d)) = actual(d)$
     *
     * @return this
     */
    public FilterAssertion isIdempotent() {
        return is(idempotent(actual));
    }

    /**
     * Configures the assertion to check that the `actual` propagator is
     * contracting. That is to say:
     * $\forall d \in PartialAssignment, \forall a \in d: actual({a}) \subseteq actual(d)$
     *
     * @return this
     */
    public FilterAssertion isWeaklyMonotonic() {
        return is(weaklyMonotonic(actual));
    }

    /** {@inheritDoc} */
    public boolean test(final PartialAssignment domains) {
        return this.check.test(domains);
    }

    /** {@inheritDoc} */
    public void check() {
        doCheck(this);
    }

    /** {@inheritDoc} */
    @Override
    protected FilterAssertion getThis() {
        return this;
    }
}
