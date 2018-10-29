package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.assertions.stateful.StatefulAssertion;
import be.uclouvain.solvercheck.assertions.stateless.StatelessAssertion;
import be.uclouvain.solvercheck.assertions.util.AssertionRunner;
import be.uclouvain.solvercheck.assertions.util.ForAllAssertion;
import be.uclouvain.solvercheck.assertions.util.ForAnyPartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.core.task.StatefulFilter;
import be.uclouvain.solvercheck.generators.GenBuilder;

import java.util.concurrent.TimeUnit;

/**
 * This class provides some utility methods to instanciate CP-level test cases.
 */
public final class AssertionDSL {

    /** An utility class has no public constructor. */
    private AssertionDSL() { }

    /**
     * Builds a runner for some assertion that will timeout after the
     * specified time period.
     *
     * @param timeout the duration (in `unit`) after which the assertion
     *                should be killed.
     * @param unit the time unit to use to interpret the value of `duration`.
     * @return a runner able to evaluate some assertion.
     */
    public static AssertionRunner given(final long timeout, final TimeUnit unit) {
        return new AssertionRunner(timeout, unit);
    }

    /**
     * Builds a runner for some assertion that will not timeout.
     *
     * @return a runner able to evaluate some assertion.
     */
    public static AssertionRunner given() {
        return new AssertionRunner();
    }

    /**
     * Checks the validity of the given assertion.
     *
     * @param assertion the assertion to verify.
     */
    public static void assertThat(final Assertion assertion) {
        new AssertionRunner().assertThat(assertion);
    }

    /**
     * This class provides a simple way to define and check a property that
     * should hold for any partial assignment. However, it should not be used
     * in place of the more specific methods (an, a) which are much more
     * powerful.
     *
     * @return a simple way to define and check a property that should hold
     * for any partial assignment.
     */
    public static ForAnyPartialAssignment forAnyPartialAssignment() {
        return new ForAnyPartialAssignment();
    }

    /**
     * This method is an alias for (see propagator). It yields a
     * StatelessAssertion for the given filter.
     *
     * @param actual the actual filter (propagator) about which a property is
     *              being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public static StatelessAssertion an(final Filter actual) {
        return propagator(actual);
    }

    /**
     * This method is an alias for (see propagator). It yields a
     * StatelessAssertion for the given filter.
     *
     * @param actual the actual filter (propagator) about which a property is
     *              being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public static StatelessAssertion a(final Filter actual) {
        return propagator(actual);
    }

    /**
     * Returns a StatelessAssertion (builder) that uses the current configuration
     * to assess the correctness of the property.
     *
     * @param actual the actual filter (propagator) whose property is to be
     *               verified.
     * @return StatelessAssertion (builder) that uses the current configuration.
     */
    public static StatelessAssertion propagator(final Filter actual) {
        return new StatelessAssertion(actual);
    }

    /**
     * This method is an alias for (see statefulPropagator). It yields a
     * StatefulAssertion for the given SatefulFilter.
     *
     * @param actual the actual filter (stateful propagator) about which a
     *               property is being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public static StatefulAssertion a(final StatefulFilter actual) {
        return statefulPropagator(actual);
    }
    /**
     * This method is an alias for (see statefulPropagator). It yields a
     * StatefulAssertion for the given SatefulFilter.
     *
     * @param actual the actual filter (stateful propagator) about which a
     *               property is being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public static StatefulAssertion an(final StatefulFilter actual) {
        return statefulPropagator(actual);
    }

    /**
     * Returns a StatefulAssertion (builder) that uses the current configuration
     * to assess the correctness of the property.
     *
     * @param actual the actual StatefulFilter (propagator) whose property is
     *               to be verified.
     * @return StatefulAssertion (builder) that uses the current configuration.
     */
    public static StatefulAssertion statefulPropagator(final StatefulFilter actual) {
        return new StatefulAssertion(actual);
    }

    /**
     * Creates a 1-parametric assertion using the current configuration.
     *
     * This is particularly useful to check that not only constraint behave
     * correctly no matter what partial assignment they are facing, but also
     * to show that they behave correctly no matter the way they are configured.
     *
     * .. Example::
     *   The following example illustrates how one can use a parametric
     *   assertion to validate the behavior of the table constraint on
     *   **virtually all** partial assignments and **virtually all** possble
     *   table extensions.
     *
     *    <pre>
     *    assertThat(
     *       forAll(ints(0, 10))
     *          .itIsTrueThat(b -&gt; ... ) )))
     *    </pre>
     *
     *
     * @param a the generator creating the generated 1-parameter
     * @param <A> the type of the parameter produced by the generator
     * @return a hook to conveniently express a 1-parametric assertion.
     */
    public static <A> ForAllAssertion.Forall1<A> forAll(final GenBuilder<A> a) {
        return ForAllAssertion.forAll(a);
    }

    /**
     * Creates a 2-parametric assertion using the current configuration.
     *
     * This is particularly useful to check that not only constraint behave
     * correctly no matter what partial assignment they are facing, but also
     * to show that they behave correctly no matter the way they are configured.
     *
     * .. Example::
     *   The following example illustrates how one can use a parametric
     *   assertion to validate the behavior of the table constraint on
     *   **virtually all** partial assignments and **virtually all** possble
     *   table extensions.
     *
     *   <pre>
     *    assertThat(
     *       forAll(ints(0, 10), ints(0, 20))
     *            .itIsTrueThat((b, i) -&gt; ... ) )))
     *    </pre>
     *
     *
     * @param a the generator creating the generated 1st argument.
     * @param b the generator creating the generated 2nd argument.
     * @param <A> the type of the parameter produced by the 1st generator
     * @param <B> the type of the parameter produced by the 2nd generator
     * @return a hook to conveniently express a 2-parametric assertion.
     */
    public static <A, B> ForAllAssertion.Forall2<A, B> forAll(
            final GenBuilder<A> a,
            final GenBuilder<B> b) {

        return ForAllAssertion.forAll(a, b);
    }

}
