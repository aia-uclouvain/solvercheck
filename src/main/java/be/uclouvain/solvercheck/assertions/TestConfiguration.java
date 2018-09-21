package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.assertions.stateful.DiveAssertion;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.core.task.StatefulFilter;
import org.quicktheories.core.ExceptionReporter;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Guidance;
import org.quicktheories.core.NoGuidance;
import org.quicktheories.core.PseudoRandom;
import org.quicktheories.core.Strategy;

import java.util.function.Supplier;

/**
 * This class is a plain data object storing the information required to
 * appropriately configure a running assertion. It is designed to have a
 * fluent interface and hence acts as a builder for QuickTheories strategy
 * object.
 */
public final class TestConfiguration implements Supplier<Strategy> {
    /**
     * The default value for the number of test cases that are randomly
     * generated.
     */
    private static final int DEFAULT_NB_EXAMPLES = 1000;
    /**
     * The default value for the number of cycles spent trying to generate an
     * input that satisfies the assumptions placed on the tested property.
     */
    private static final int DEFAULT_NB_ATTEMPTS = 100000;
    /**
     * The default value for the number of cycles spent shrinking the size of
     * a test case that was shown to falsify the tested property.
     */
    private static final int DEFAULT_NB_SHRINK_CYLES = 1000000;
    /**
     * The number of 'dives' (branches that are explored until a leaf is
     * reached) explored by default when testing a stateful property.
     */
    private static final int DEFAULT_NB_DIVES = 1000;


    /** The random seed used to initialize the randomness source. */
    private long seed = System.nanoTime();

    /** The number of randomly generated test cases. */
    private int nbExamples = DEFAULT_NB_EXAMPLES;

    /**
     * The number of cycles spent trying to generate an input that satisfies
     * the assumptions placed on the tested property.
     */
    private int nbAttempts = DEFAULT_NB_ATTEMPTS;

    /**
     * Maximum number of shrink cycles spent trying to minimize a test
     * falsifying a checked property.
     */
    private int nbShrinkCycles = DEFAULT_NB_SHRINK_CYLES;
    /**
     * The number of 'dives' (branches that are explored until a leaf is
     * reached) explored when performing the stateful check of some property.
     */
    private int nbDives = DEFAULT_NB_DIVES;

    /**
     * Configures the dependent tests to use the given random seed to
     * generate test cases.
     *
     * @param randomSeed the value of the random seed to use.
     * @return this
     */
    public TestConfiguration theRandomSeed(final long randomSeed) {
        if (randomSeed == 0) {
            throw new IllegalArgumentException("Random seed may not be 0");
        }

        this.seed = randomSeed;
        return this;
    }

    /**
     * Configures the dependent tests to generate `n` different test cases to
     * validate an asserted property.
     *
     * @param n the number of test cases to produce
     * @return this
     */
    public TestConfiguration examples(final int n) {
        this.nbExamples = n;
        return this;
    }

    /**
     * Configures the dependent tests to generate `n` different attempts to
     * find a suitable input satisfying the assumptions imposed by the user
     * onto the generated test cases.
     *
     * @param n the number of attempts to generate suitable input parameters
     * @return this
     */
    public TestConfiguration attempts(final int n) {
        this.nbAttempts = n;
        return this;
    }

    /**
     * Configures the dependent tests to execute `n` shrink cycles in
     * order to minimize the input of the generated test case when one is
     * found to falsify the tested property.
     *
     * @param n the number of attempts to generate suitable input parameters
     * @return this
     */
    public TestConfiguration shrinkCycles(final int n) {
        this.nbShrinkCycles = n;
        return this;
    }

    /**
     * Configures the dependent stateful tests to execute `n` dives when
     * trying to invalidate some property.
     *
     * @param n the number of branches to explore until a leaf is reached.
     * @return this
     */
    public TestConfiguration dives(final int n) {
        this.nbDives = n;
        return this;
    }
    /**
     * Return the current value of the number of dives that was configured.
     *
     * @return the configured number of dives.
     */
    public int getNbDives() {
        return this.nbDives;
    }

    /**
     * Returns a FilterAssertion (builder) that uses the current configuration
     * to assess the correctness of the property.
     *
     * @param actual the actual filter (propagator) whose property is to be
     *               verified.
     * @return FilterAssertion (builder) that uses the current configuration.
     */
    public FilterAssertion propagator(final Filter actual) {
        return new FilterAssertion(this, actual);
    }

    /**
     * This method is an alias for {@see propagator}. It yields a
     * FilterAssertion for the given filter.
     *
     * @param actual the actual filter (propagator) about which a property is
     *              being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public FilterAssertion an(final Filter actual) {
        return propagator(actual);
    }

    /**
     * This method is an alias for {@see propagator}. It yields a
     * FilterAssertion for the given filter.
     *
     * @param actual the actual filter (propagator) about which a property is
     *              being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public FilterAssertion a(final Filter actual) {
        return propagator(actual);
    }

    /**
     * Returns a DiveAssertion (builder) that uses the current configuration
     * to assess the correctness of the property.
     *
     * @param actual the actual StatefulFilter (propagator) whose property is
     *               to be verified.
     * @return DiveAssertion (builder) that uses the current configuration.
     */
    public DiveAssertion statefulPropagator(final StatefulFilter actual) {
        return new DiveAssertion(this, actual);
    }
    /**
     * This method is an alias for {@see statefulPropagator}. It yields a
     * DiveAssertion for the given SatefulFilter.
     *
     * @param actual the actual filter (stateful propagator) about which a
     *               property is being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public DiveAssertion a(final StatefulFilter actual) {
        return statefulPropagator(actual);
    }
    /**
     * This method is an alias for {@see statefulPropagator}. It yields a
     * DiveAssertion for the given SatefulFilter.
     *
     * @param actual the actual filter (stateful propagator) about which a
     *               property is being expressed.
     * @return a builder to express the assertion about some Filter
     */
    public DiveAssertion an(final StatefulFilter actual) {
        return statefulPropagator(actual);
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
     *    ```
     *    assertThat(
     *       given()
     *        .theRandomSeed(1234)
     *        .forAll(booleans())
     *            .itIsTrueThat(b -> ... ) )))
     *    ```
     *
     *
     * @param a the generator creating the generated 1-parameter
     * @param <A> the type of the parameter produced by the generator
     * @return a hook to conveniently express a 1-parametric assertion.
     */
    public <A> ForAllAssertion.Forall1<A> forAll(final Gen<A> a) {
        return ForAllAssertion.forAll(this, a);
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
     *   ```
     *    assertThat(
     *       given()
     *        .theRandomSeed(1234)
     *        .forAll(booleans(), integers.between(0, 10))
     *            .itIsTrueThat((b, i) -> ... ) )))
     *    ```
     *
     *
     * @param a the generator creating the generated 1st argument.
     * @param b the generator creating the generated 2nd argument.
     * @param <A> the type of the parameter produced by the 1st generator
     * @param <B> the type of the parameter produced by the 2nd generator
     * @return a hook to conveniently express a 2-parametric assertion.
     */
    public <A, B> ForAllAssertion.Forall2<A, B> forAll(
            final Gen<A> a,
            final Gen<B> b) {

        return ForAllAssertion.forAll(this, a, b);
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
     *   ```
     *    assertThat(
     *       given()
     *        .theRandomSeed(1234)
     *        .forAll(xs(), ys(), zs())
     *            .itIsTrueThat((x, y, z) -> ... ) )))
     *    ```
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
    public <A, B, C> ForAllAssertion.Forall3<A, B, C> forAll(
            final Gen<A> a,
            final Gen<B> b,
            final Gen<C> c) {

        return ForAllAssertion.forAll(this, a, b, c);
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
     *   ```
     *    assertThat(
     *       given()
     *        .theRandomSeed(1234)
     *        .forAll(ws(), xs(), ys(), zs())
     *            .itIsTrueThat((w, x, y, z) -> ... ) )))
     *    ```
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
    public <A, B, C, D> ForAllAssertion.Forall4<A, B, C, D> forAll(
            final Gen<A> a,
            final Gen<B> b,
            final Gen<C> c,
            final Gen<D> d) {

        return ForAllAssertion.forAll(this, a, b, c, d);
    }

    /** {@inheritDoc} */
    @Override
    public Strategy get() {
        return new Strategy(
                Strategy.defaultPRNG(seed),
                nbExamples,
                nbShrinkCycles,
                nbAttempts,
                new ExceptionReporter(),
                this::noGuidance
        );
    }

    /**
     * Strategy to create a guidance that provides no hints to the given prng.
     *
     * @param prng the pseudo random generator used as randomness source for
     *             the generated test cases.
     * @return A function that wraps the given prng to and gives it no guidance.
     */
    private Guidance noGuidance(final PseudoRandom prng) {
        return new NoGuidance();
    }
}
