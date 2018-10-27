package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.assertions.stateful.StatefulAssertion;
import be.uclouvain.solvercheck.assertions.stateless.StatelessAssertion;
import be.uclouvain.solvercheck.assertions.util.AssertionRunner;
import be.uclouvain.solvercheck.assertions.util.ForAllAssertion;
import be.uclouvain.solvercheck.assertions.util.ForAnyPartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.core.task.StatefulFilter;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * This interface collects all the useful methods that let you plug
 * SolverCheck's assertion DSL into your code.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithAssertions {

    /**
     * Checks the validity of the given assertion.
     *
     * @param assertion the assertion to verify.
     */
    default void assertThat(final Assertion assertion) {
        AssertionDSL.assertThat(assertion);
    }

    /**
     * Builds a runner for some assertion that will timeout after the
     * specified time period.
     *
     * @param timeout the duration (in `unit`) after which the assertion
     *                should be killed.
     * @param unit the time unit to use to interpret the value of `duration`.
     * @return a runner able to evaluate some assertion.
     */
    default AssertionRunner given(final long timeout, final TimeUnit unit) {
        return AssertionDSL.given(timeout, unit);
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
    default ForAnyPartialAssignment forAnyPartialAssignment() {
        return AssertionDSL.forAnyPartialAssignment();
    }

    /**
     * Provides you with a convenient way to express some assertion/property
     * about a given propagator.
     *
     * .. Note::
     *    The assertion returned by this method uses the default configuration
     *    as if it were produced by the sequence <pre>given().propagator(x)
     *    </pre>.
     *
     * @param actual the actual filter (propagator) about which a property is
     *              being expressed.
     * @return a builder to express the assertion about some Filter
     */
    default StatelessAssertion propagator(final Filter actual) {
        return AssertionDSL.propagator(actual);
    }

    /**
     * This method is an alias for (see propagator). It yields a
     * StatelessAssertion for the given filter.
     *
     * @param actual the actual filter (propagator) about which a property is
     *              being expressed.
     * @return a builder to express the assertion about some Filter
     */
    default StatelessAssertion an(final Filter actual) {
        return AssertionDSL.an(actual);
    }

    /**
     * This method is an alias for (see propagator). It yields a
     * StatelessAssertion for the given filter.
     *
     * @param actual the actual filter (propagator) about which a property is
     *              being expressed.
     * @return a builder to express the assertion about some Filter
     */
    default StatelessAssertion a(final Filter actual) {
        return AssertionDSL.a(actual);
    }

    /**
     * Returns a StatefulAssertion (builder) that uses the current configuration
     * to assess the correctness of the property.
     *
     * @param actual the actual StatefulFilter (propagator) whose property is
     *               to be verified.
     * @return StatefulAssertion (builder) that uses the current configuration.
     */
    default StatefulAssertion statefulPropagator(final StatefulFilter actual) {
        return AssertionDSL.statefulPropagator(actual);
    }
    /**
     * This method is an alias for (see statefulPropagator). It yields a
     * StatefulAssertion for the given SatefulFilter.
     *
     * @param actual the actual filter (stateful propagator) about which a
     *               property is being expressed.
     * @return a builder to express the assertion about some Filter
     */
    default StatefulAssertion a(final StatefulFilter actual) {
        return AssertionDSL.a(actual);
    }
    /**
     * This method is an alias for (see statefulPropagator). It yields a
     * StatefulAssertion for the given SatefulFilter.
     *
     * @param actual the actual filter (stateful propagator) about which a
     *               property is being expressed.
     * @return a builder to express the assertion about some Filter
     */
    default StatefulAssertion an(final StatefulFilter actual) {
        return AssertionDSL.an(actual);
    }

    /**
     * Creates a 1-parametric assertion.
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
     *       forAll(tables())
     *         .itIsTrueThat(table -&gt;
     *              propagator(arcConsistent(table(t))
     *             .isStrongerThan(boundZConsistent(table(t)))
     *         )
     *       )
     *    )
     *    </pre>
     *
     *
     * @param a the generator creating the generated 1-parameter
     * @param <A> the type of the parameter produced by the generator
     * @return a hook to conveniently express a 1-parametric assertion.
     */
    default <A> ForAllAssertion.Forall1<A> forAll(final Stream<A> a) {
        return AssertionDSL.forAll(a);
    }

    /**
     * Creates a 2-parametric assertion.
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
     *       forAll(ints(0, 10), ints(20, 30))
     *         .itIsTrueThat((b, i) -&gt; ... ) )
     *       )
     *    )
     *    </pre>
     *
     *
     * @param a the generator creating the generated 1st argument.
     * @param b the generator creating the generated 2nd argument.
     * @param <A> the type of the parameter produced by the 1st generator
     * @param <B> the type of the parameter produced by the 2nd generator
     * @return a hook to conveniently express a 2-parametric assertion.
     */
    default <A, B> ForAllAssertion.Forall2<A, B> forAll(
            final Stream<A> a,
            final Stream<B> b) {

        return AssertionDSL.forAll(a, b);
    }
}
