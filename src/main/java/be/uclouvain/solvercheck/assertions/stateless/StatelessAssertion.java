package be.uclouvain.solvercheck.assertions.stateless;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.fuzzing.Randomness;

import java.util.function.Function;
import java.util.function.Predicate;

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
public final class StatelessAssertion implements Function<PartialAssignment, Assertion> {

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
    public StatelessAssertion(final Filter actual) {
        super();
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
    public StatelessAssertion is(final Predicate<PartialAssignment> property) {
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
    public StatelessAssertion isEquivalentTo(final Filter other) {
        return is(equivalentTo(actual, other));
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is weaker or equivalent to that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public StatelessAssertion isWeakerThan(final Filter other) {
        return is(weakerThan(actual, other));
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is strictly weaker than that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public StatelessAssertion isStrictlyWeakerThan(final Filter other) {
        return is(strictlyWeakerThan(actual, other));
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is stronger or equivalent to that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public StatelessAssertion isStrongerThan(final Filter other) {
        return is(strongerThan(actual, other));
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is strictly stronger than that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public StatelessAssertion isStrictlyStrongerThan(final Filter other) {
        return is(strictlyStrongerThan(actual, other));
    }

    /**
     * Configures the assertion to check that the `actual` propagator is
     * contracting. That is to say:
     * $\forall d \in PartialAssignments : actual(d) \subseteq d$
     *
     * @return this
     */
    public StatelessAssertion isContracting() {
        return is(contracting(actual));
    }

    /**
     * Configures the assertion to check that the `actual` propagator is
     * contracting. That is to say:
     * $\forall d \in PartialAssignments : actual(actual(d)) = actual(d)$
     *
     * @return this
     */
    public StatelessAssertion isIdempotent() {
        return is(idempotent(actual));
    }

    /**
     * Configures the assertion to check that the `actual` propagator is
     * contracting. That is to say:
     * $\forall d \in PartialAssignment, \forall a \in d: actual({a}) \subseteq actual(d)$
     *
     * @return this
     */
    public StatelessAssertion isWeaklyMonotonic() {
        return is(weaklyMonotonic(actual));
    }

    /** {@inheritDoc} */
    @Override
    public Assertion apply(final PartialAssignment pa) {
        return rnd -> {
            if (!this.check.test(pa)) {
                throw new AssertionError(explanation(rnd, pa));
            }
        };
    }

    /**
     * Creates an intelligible error report which can be used to reproduce an
     * investigate an error witness.
     *
     * @param rnd the source of randomness used for the fuzzing.
     * @param pa  the partial assignment
     * @return An error message giving the details of the witnessed
     * property violation.
     */
    private String explanation(final Randomness rnd,
                               final PartialAssignment pa) {

        final StringBuilder builder = new StringBuilder("\n");
        builder.append("########################### \n");
        builder.append("SEED    : ").append(Long.toHexString(rnd.getSeed())).append("\n");
        builder.append("WITNESS : ").append(pa).append("\n");
        builder.append("########################### \n");

        return builder.toString();
    }
}
