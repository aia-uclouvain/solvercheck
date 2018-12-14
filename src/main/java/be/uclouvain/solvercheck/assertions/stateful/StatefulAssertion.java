package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.StatefulFilter;
import be.uclouvain.solvercheck.fuzzing.Randomness;

import java.util.function.Function;

import static be.uclouvain.solvercheck.assertions.stateful.StatefulProperties.equivalentTo;
import static be.uclouvain.solvercheck.assertions.stateful.StatefulProperties.strictlyStrongerThan;
import static be.uclouvain.solvercheck.assertions.stateful.StatefulProperties.strictlyWeakerThan;
import static be.uclouvain.solvercheck.assertions.stateful.StatefulProperties.strongerThan;
import static be.uclouvain.solvercheck.assertions.stateful.StatefulProperties.weakerThan;

/**
 * This is the class that implements the assertions relative to the stateful
 * testing of the StatefulFilter algorithms. Its purpose is mostly to
 * configure and run `Dive`s (see Dive) which in turn validate the property
 * on some execution subtree.
 *
 * @see Dive
 */
@SuppressWarnings("checkstyle:hiddenfield")
public final class StatefulAssertion implements Function<PartialAssignment, Assertion> {
    /**
     * The number of 'dives' (branches that are explored until a leaf is
     * reached) explored by default when testing a stateful property.
     */
    private static final int DEFAULT_NB_DIVES = 1000;

    /** The actual StatefulFilter being tested. */
    private final StatefulFilter actual;

    /**
     * The condition effectively being checked at each node of the dive
     * search tree.
     */
    private StatefulProperties.Property property;

    /**
     * The number of 'dives' (branches that are explored until a leaf is
     * reached) explored when performing the stateful check of some property.
     */
    private int nbDives;

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     * @param actual the Filter whose property is being evaluated.
     */
    public StatefulAssertion(final StatefulFilter actual) {
        super();
        this.actual  = actual;
        this.nbDives = DEFAULT_NB_DIVES;
    }

    /**
     * Configures the assertion `check` with the arbitrarily specified property.
     *
     * @param property the property that must hold for this assertion to be
     *                 valid
     * @return this
     */
    public StatefulAssertion is(final StatefulProperties.Property property) {
        this.property = property;
        return this;
    }

    /**
     * Configures the dependent stateful tests to execute `n` dives when
     * trying to invalidate some property.
     *
     * @param n the number of branches to explore until a leaf is reached.
     * @return this
     */
    public StatefulAssertion diving(final int n) {
        this.nbDives = n;
        return this;
    }

    /**
     * Configure the assertion to check that both `actual` and `other` have
     * always the same propagating strengths.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public StatefulAssertion isEquivalentTo(final StatefulFilter other) {
        return is(equivalentTo(actual, other));
    }

    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is weaker or equivalent to that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public StatefulAssertion isWeakerThan(final StatefulFilter other) {
        return is(weakerThan(actual, other));
    }
    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is strictly weaker than that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public StatefulAssertion isStrictlyWeakerThan(final StatefulFilter other) {
        return is(strictlyWeakerThan(actual, other));
    }
    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is stronger or equivalent to that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public StatefulAssertion isStrongerThan(final StatefulFilter other) {
        return is(strongerThan(actual, other));
    }
    /**
     * Configure the assertion to check that `actual` has propagation
     * strength which is strictly stronger than that of `other`.
     *
     * @param other the reference filter with which to compare the results.
     * @return this
     */
    public StatefulAssertion isStrictlyStrongerThan(final StatefulFilter other) {
        return is(strictlyStrongerThan(actual, other));
    }

    /** {@inheritDoc} */
    @Override
    public Assertion apply(final PartialAssignment pa) {
        return rnd -> {
            dive(rnd, pa).run();
        };
    }

    /**
     * Instanciate a new Dive rooted at the given `root`, using `strategy` to
     * generate functions picking values from predefined data distributions.
     *
     * @param rnd  the source of randomness used for the fuzzing.
     * @param root the value of the variables domains at the root of the
     *             search tree explored by the dive.
     * @return a new Dive rooted at the given `root`.
     */
    private Dive dive(final Randomness rnd, final PartialAssignment root) {
        return new Dive(rnd, property, nbDives, root);
    }
}
