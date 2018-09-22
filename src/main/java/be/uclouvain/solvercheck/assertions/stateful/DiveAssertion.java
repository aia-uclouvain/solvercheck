package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.assertions.TestConfiguration;
import be.uclouvain.solvercheck.checkers.Checkers;
import be.uclouvain.solvercheck.consistencies.ArcConsitency;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.StatefulFilter;
import be.uclouvain.solvercheck.generators.Generators;
import be.uclouvain.solvercheck.stateful.StatefulFilterAdapter;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.quicktheories.QuickTheory.qt;

/**
 * This is the class that implements the assertions relative to the stateful
 * testing of the StatefulFilter algorithms. Its purpose is mostly to
 * configure and run `Dive`s {@see Dive} which in turn validate the property
 * on some execution subtree.
 */
@SuppressWarnings("checkstyle:hiddenfield")
public final class DiveAssertion implements Assertion {
    /**
     * The actual test configuration to use when evaluating this assertion.
     */
    private final TestConfiguration config;

    /** The actual StatefulFilter being tested. */
    private final StatefulFilter actual;

    /**
     * The condition effectively being checked at each node of the dive
     * search tree.
     */
    private Supplier<Boolean> check;

    /** The reference StatefulFilter with which to compare the resuts. */
    private StatefulFilter other;

    /**
     * The generator used to produce the randomly generated partial assignments.
     */
    private Gen<PartialAssignment> generator;

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     * @param actual the Filter whose property is being evaluated.
     */
    public DiveAssertion(final StatefulFilter actual) {
        this(new TestConfiguration(), actual);
    }

    /**
     * Creates a new instance evaluating some property of the `actual` filter.
     *
     * @param config the test configuration to use when evaluating this
     *               property.
     * @param actual the Filter whose property is being evaluated.
     */
    public DiveAssertion(final TestConfiguration config,
                         final StatefulFilter actual) {
        this.config    = config;
        this.actual    = actual;
        this.check     = () -> true;
        this.other     = defaultOther();
        this.generator = Generators.partialAssignments();
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

    /**
     * Returns a FilterAssertion (an assertion whose generator can be further
     * constrained with assumptions).
     *
     * @param generator the generator to use in order to generate random
     *                  partial assignements.
     * @return a filter assertion which whose generator can be configured.
     */
    public DiveAssertion forAll(final Gen<PartialAssignment> generator) {
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
    public DiveAssertion assuming(final Predicate<PartialAssignment> assumption) {
        this.generator = generator.assuming(assumption);

        return this;
    }

    /** {@inheritDoc} */
    public void check() {
        final Strategy strategy = config.get();

        qt(() -> strategy)
            .forAll(generator)
            .assuming(root -> !root.isEmpty())
            .checkAssert(root -> dive(strategy, root).run());
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
     * @param strategy the configuration of the underlying QuickTheories
     *                 layer. It is used to configure the data distributions
     *                 from which values are chosen.
     * @param root the value of the variables domains at the root of the
     *             search tree explored by the dive.
     * @return a new Dive rooted at the given `root`.
     */
    private Dive dive(final Strategy strategy, final PartialAssignment root) {
        return new Dive(this, strategy, root);
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
        return config.getNbDives();
    }

    /**
     * Initialises the state of both `actual` and `other` telling them the
     * initial value of the variables domains.
     *
     * {@see StatefulFilter::setup}.
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
