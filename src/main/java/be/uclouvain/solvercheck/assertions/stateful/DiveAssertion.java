package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.assertions.util.AbstractFluentConfig;
import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.checkers.Checkers;
import be.uclouvain.solvercheck.consistencies.ArcConsitency;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.StatefulFilter;
import be.uclouvain.solvercheck.stateful.StatefulFilterAdapter;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;
import org.quicktheories.core.Strategy;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This is the class that implements the assertions relative to the stateful
 * testing of the StatefulFilter algorithms. Its purpose is mostly to
 * configure and run `Dive`s (see Dive) which in turn validate the property
 * on some execution subtree.
 *
 * @see Dive
 */
@SuppressWarnings("checkstyle:hiddenfield")
public final class DiveAssertion
        extends AbstractFluentConfig<DiveAssertion>
        implements Predicate<PartialAssignment>, Assertion {
    /**
     * The number of 'dives' (branches that are explored until a leaf is
     * reached) explored by default when testing a stateful property.
     */
    private static final int DEFAULT_NB_DIVES = 1000;

    /** The actual StatefulFilter being tested. */
    private final StatefulFilter actual;

    /** The reference StatefulFilter with which to compare the resuts. */
    private StatefulFilter other;

    /**
     * The condition effectively being checked at each node of the dive
     * search tree.
     */
    private Supplier<Boolean> check;

    /**
     * The number of 'dives' (branches that are explored until a leaf is
     * reached) explored when performing the stateful check of some property.
     */
    private int nbDives;

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     * @param actual the Filter whose property is being evaluated.
     */
    public DiveAssertion(final StatefulFilter actual) {
        super();
        this.actual  = actual;
        this.other   = defaultOther();
        this.check   = () -> true;
        this.nbDives = DEFAULT_NB_DIVES;

        // performing dives for an empty root makes no sense.
        assuming(root -> !root.isEmpty());
    }

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     *
     * @param config the initial configuration of the test case (can be
     *               customized)
     * @param actual the Filter whose property is being evaluated.
     */
    public DiveAssertion(final Supplier<Strategy> config,
                         final StatefulFilter actual) {
        super(config);
        this.actual  = actual;
        this.other   = defaultOther();
        this.check   = () -> true;
        this.nbDives = DEFAULT_NB_DIVES;

        // performing dives for an empty root makes no sense.
        assuming(root -> !root.isEmpty());
    }

    /** {@inheritDoc} */
    @Override
    protected DiveAssertion getThis() {
        return this;
    }

    /**
     * Configures the dependent stateful tests to execute `n` dives when
     * trying to invalidate some property.
     *
     * @param n the number of branches to explore until a leaf is reached.
     * @return this
     */
    public DiveAssertion diving(final int n) {
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
    public DiveAssertion isEquivalentTo(final StatefulFilter other) {
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
    public DiveAssertion isWeakerThan(final StatefulFilter other) {
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
    public DiveAssertion isStrictlyWeakerThan(final StatefulFilter other) {
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
    public DiveAssertion isStrongerThan(final StatefulFilter other) {
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
    public DiveAssertion isStrictlyStrongerThan(final StatefulFilter other) {
        this.other = other;
        this.check = this::checkStrictlyStronger;

        return this;
    }

    /** {@inheritDoc} */
    @Override
    public void check() {
        doCheckAssert(pa -> dive(pa).run());
    }

    /**
     * Verifies the property for the one given test case.
     * @param pa the test case for whichg to verify the property
     */
    public void check(final PartialAssignment pa) {
        dive(pa).run();
    }

    /** {@inheritDoc} */
    @Override
    public boolean test(final PartialAssignment pa) {
        try {
            dive(pa).run();
            return true;
        } catch (AssertionError error) {
            return false;
        }
    }

    /**
     * Checks that `actual` and `other` have equivalent propagating strengths
     * for the given `domains` test case.
     *
     * @return true iff actual is equivalent to other
     */
    private boolean checkEquivalent() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.EQUIVALENT;
    }

    /**
     * Checks that `actual` is weaker or equivalent to `other` for the given
     * `domains` test case.
     *
     * @return true iff actual is weaker or equivalent to other
     */
    private boolean checkWeaker() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.WEAKER
            || comparison == PartialOrdering.EQUIVALENT;
    }
    /**
     * Checks that `actual` is weaker than `other` for the given `domains`
     * test case.
     *
     * @return true iff actual is weaker than other
     */
    private boolean checkStrictlyWeaker() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.WEAKER;
    }
    /**
     * Checks that `actual` is stronger or equivalent to `other` for the given
     * `domain` test case.
     *
     * @return true iff actual is stronger or equivalent to other
     */
    private boolean checkStronger() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.STRONGER
            || comparison == PartialOrdering.EQUIVALENT;
    }

    /**
     * Checks that `actual` is stronger than `other` for the given `domains`
     * test case.
     *
     * @return true iff actual is stronger than other
     */
    private boolean checkStrictlyStronger() {
        final PartialOrdering comparison =
                actual.currentState().compareWith(other.currentState());

        return comparison == PartialOrdering.STRONGER;
    }

    /**
     * Instanciate a new Dive rooted at the given `root`, using `strategy` to
     * generate functions picking values from predefined data distributions.
     *
     * @param root the value of the variables domains at the root of the
     *             search tree explored by the dive.
     * @return a new Dive rooted at the given `root`.
     */
    private Dive dive(final PartialAssignment root) {
        return new Dive(this, get(), root);
    }

    /**
     * @return The default stateful identifier. (One that prunes absolutely
     * nothing.
     */
    private static StatefulFilter defaultOther() {
        return new StatefulFilterAdapter(
                new ArcConsitency(
                        Checkers.alwaysTrue()));
    }

    /**
     * The number of branches to explore until a leaf is reached.
     *
     * @return the configured nbDives. That is to say the number of distinct
     * branched explored by a dive search.
     */
    /* package */ int getNbDives() {
        return nbDives;
    }

    /**
     * Initialises the state of both `actual` and `other` telling them the
     * initial value of the variables domains.
     *
     * @see StatefulFilter::setup
     *
     * @param root the initial value of the variables domains.
     */
    /* package */ void setup(final PartialAssignment root) {
        actual.setup(root);
        other .setup(root);
    }

    /**
     * Pushes the state of both `actual` and other.
     * {@see StatefulFilter::pushState}.
     */
    /* package */ void pushState() {
        actual.pushState();
        other .pushState();
    }

    /**
     * Pops the state of both `actual` and other.
     * {@see StatefulFilter::popState}.
     */
    /* package */ void popState() {
        actual.popState();
        other .popState();
    }

    /**
     * Tells both `actual` and other to branch on the following restriction:
     * [[ variable op value ]].
     *
     * {@see StatefulFilter::branchOn}.
     *
     * @param variable the variable on which a branching decision is made.
     * @param op the constraint imposed on the value that can be taken by
     *           `variable`.
     * @param value in conjunction with `op` determines the constraint
     *              imposed on the values that can be taken by `variable`.
     */
    /* package */ void branchOn(
            final int variable,
            final Operator op,
            final int value) {

        actual.branchOn(variable, op, value);
        other .branchOn(variable, op, value);
    }

    /**
     * @return iff the current state of `actual` or that of `other` is a leaf
     * of the search tree.
     */
    /* package */ boolean isCurrentStateLeaf() {
        return actual.currentState().isLeaf()
            || other .currentState().isLeaf();
    }

    /**
     * Evaluates the local assertion described by `this` and retruns its
     * truth value.
     *
     * @return true iff the assertion is verified in the current node.
     */
    /* package */ boolean checkTest() {
        return check.get();
    }

    /**
     * @return the current state of the `actual` filter.
     */
    /* package */ PartialAssignment actualState() {
        return actual.currentState();
    }

    /**
     * @return the current state of the `other` filter.
     */
    /* package */ PartialAssignment otherState() {
        return other.currentState();
    }
}
