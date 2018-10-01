package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.assertions.stateful.DiveAssertion;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.core.task.StatefulFilter;
import org.quicktheories.core.Gen;

/**
 * This class provides some utility methods to instanciate CP-level test cases.
 */
public final class AssertionDSL {

    /** An utility class has no public constructor. */
    private AssertionDSL() { }

    /**
     * This method returns an object holding the information to appropriately
     * configure a running test.
     *
     * @return an object holding the information to appropriately configure a
     * running test.
     */
    public static TestConfiguration given() {
        return new TestConfiguration();
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
     * FilterAssertion for the given filter.
     *
     * @param actual the actual filter (propagator) about which a property is
     *              being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public static FilterAssertion an(final Filter actual) {
        return propagator(actual);
    }

    /**
     * This method is an alias for (see propagator). It yields a
     * FilterAssertion for the given filter.
     *
     * @param actual the actual filter (propagator) about which a property is
     *              being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public static FilterAssertion a(final Filter actual) {
        return propagator(actual);
    }

    /**
     * Returns a FilterAssertion (builder) that uses the current configuration
     * to assess the correctness of the property.
     *
     * @param actual the actual filter (propagator) whose property is to be
     *               verified.
     * @return FilterAssertion (builder) that uses the current configuration.
     */
    public static FilterAssertion propagator(final Filter actual) {
        return new FilterAssertion(actual);
    }

    /**
     * This method is an alias for (see statefulPropagator). It yields a
     * DiveAssertion for the given SatefulFilter.
     *
     * @param actual the actual filter (stateful propagator) about which a
     *               property is being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public static DiveAssertion a(final StatefulFilter actual) {
        return statefulPropagator(actual);
    }
    /**
     * This method is an alias for (see statefulPropagator). It yields a
     * DiveAssertion for the given SatefulFilter.
     *
     * @param actual the actual filter (stateful propagator) about which a
     *               property is being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public static DiveAssertion an(final StatefulFilter actual) {
        return statefulPropagator(actual);
    }

    /**
     * Returns a DiveAssertion (builder) that uses the current configuration
     * to assess the correctness of the property.
     *
     * @param actual the actual StatefulFilter (propagator) whose property is
     *               to be verified.
     * @return DiveAssertion (builder) that uses the current configuration.
     */
    public static DiveAssertion statefulPropagator(final StatefulFilter actual) {
        return new DiveAssertion(actual);
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
     *       forAll(booleans())
     *          .itIsTrueThat(b -&gt; ... ) )))
     *    </pre>
     *
     *
     * @param a the generator creating the generated 1-parameter
     * @param <A> the type of the parameter produced by the generator
     * @return a hook to conveniently express a 1-parametric assertion.
     */
    public static <A> ForAllAssertion.Forall1<A> forAll(final Gen<A> a) {
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
     *       forAll(booleans(), integers.between(0, 10))
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
            final Gen<A> a,
            final Gen<B> b) {

        return ForAllAssertion.forAll(a, b);
    }

    /**
     * Creates a 3-parametric assertion using the current configuration.
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
     *       forAll(xs(), ys(), zs())
     *            .itIsTrueThat((x, y, z) -&gt; ... ) )))
     *    </pre>
     *
     *
     * @param a the generator creating the generated 1st argument.
     * @param b the generator creating the generated 2nd argument.
     * @param c the generator creating the generated 3rd argument.
     * @param <A> the type of the parameter produced by the 1st generator
     * @param <B> the type of the parameter produced by the 2nd generator
     * @param <C> the type of the parameter produced by the 3rd generator
     * @return a hook to conveniently express a 3-parametric assertion.
     */
    public static <A, B, C> ForAllAssertion.Forall3<A, B, C> forAll(
            final Gen<A> a,
            final Gen<B> b,
            final Gen<C> c) {

        return ForAllAssertion.forAll(a, b, c);
    }
    /**
     * Creates a 4-parametric assertion using the current configuration.
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
     *       forAll(ws(), xs(), ys(), zs())
     *            .itIsTrueThat((w, x, y, z) -&gt; ... ) )))
     *    </pre>
     *
     *
     * @param a the generator creating the generated 1st argument.
     * @param b the generator creating the generated 2nd argument.
     * @param c the generator creating the generated 3rd argument.
     * @param d the generator creating the generated 4th argument.
     * @param <A> the type of the parameter produced by the 1st generator
     * @param <B> the type of the parameter produced by the 2nd generator
     * @param <C> the type of the parameter produced by the 3rd generator
     * @param <D> the type of the parameter produced by the 4th generator
     * @return a hook to conveniently express a 4-parametric assertion.
     */
    public static <A, B, C, D> ForAllAssertion.Forall4<A, B, C, D> forAll(
            final Gen<A> a,
            final Gen<B> b,
            final Gen<C> c,
            final Gen<D> d) {

        return ForAllAssertion.forAll(a, b, c, d);
    }


}
