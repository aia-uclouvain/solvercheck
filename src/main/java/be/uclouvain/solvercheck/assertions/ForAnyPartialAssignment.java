package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.generators.Generators;
import org.quicktheories.QuickTheory;
import org.quicktheories.core.Configuration;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static org.quicktheories.generators.SourceDSL.integers;

/**
 * This class provides a mini framework to setup tests relative to the use of
 * partial assignments.
 *
 * <div>
 *     <h1>Note</h1>
 *     In a sense, one could consider that the work accomplished by this
 *     class should be realized by a `generator`. This would indeed feel more
 *     consistent with the rest of the QuickTheories framework. However,
 *     practice has shown that the partial assignments sample produced using
 *     this class is more varied and tackles a wider range of possible cases.
 * </div>
 */
public class ForAnyPartialAssignment implements Supplier<Strategy> {

    /**
     * The default number of attempts made to generate an input that matches
     * the assumptions.
     */
    private static final int DEFAULT_GEN_ATTEMPTS = 10000;
    /**
     * The default number of `anchor values` which designate the 'center' of
     * the values distributions in a partial assignment.
     */
    private static final int DEFAULT_ANCHOR_SAMPLES = 100;
    /**
     * The default number of partial assignment generated (and tested) for each
     * anchor value.
     */
    private static final int DEFAULT_EXAMPLES = 10;
    /**
     * The default minimal value that may appear in a generated partial
     * assignment.
     */
    private static final int DEFAULT_MIN_VALUE = MIN_VALUE;
    /**
     * The default maximal value that may appear in a generated partial
     * assignment.
     */
    private static final int DEFAULT_MAX_VALUE = MAX_VALUE;
    /**
     * The default maximum difference between any two values occurring in a
     * partial assignment.
     */
    private static final int DEFAULT_SPREAD = 10;
    /**
     * The default minimum number of variables constituting a partial
     * assignment.
     */
    private static final int DEFAULT_NB_VARS_MIN = 0;
    /**
     * The default maximum number of variables constituting a partial
     * assignment.
     */
    private static final int DEFAULT_NB_VARS_MAX = 5;
    /**
     * The default maximum number of values in a domain constitutive of a
     * partial assignment.
     */
    private static final int DEFAULT_MAX_DOM_SIZE = 10;

    /**
     * A hook used to build on top of the underlying QuickTheories
     * property-based testing library.
     */
    private Strategy strategy;

    /**
     * The number of `anchor values` which designate the 'center' of the
     * values distributions in a partial assignment.
     */
    private int anchorSamples;
    /**
     * The minimal value that may appear in a generated partial assignment.
     */
    private int minValue;
    /**
     * The maximal value that may appear in a generated partial assignment.
     */
    private int maxValue;
    /**
     * The maximum difference between any two values occurring in a partial
     * assignment.
     */
    private int spread;
    /**
     * The minimum number of variables constituting a partial assignment.
     */
    private int nbVarMin;
    /**
     * The maximum number of variables constituting a partial assignment.
     */
    private int nbVarMax;
    /**
     * The maximum number of values in a domain constitutive of a partial
     * assignment.
     */
    private int maxDomSize;
    /**
     * Assumptions enforced on the generated partial assignments. Any partial
     * assignemnt reaching `check` or `checkAssert` must satisfy these
     * assumptions.
     */
    private Predicate<PartialAssignment> assumptions;

    /**
     * Creates a new instance with all fields initialized to their default
     * values.
     *
     * @param config an initial configuration to start from
     */
    public ForAnyPartialAssignment(final Supplier<Strategy> config) {
        this.strategy      = config.get();

        this.anchorSamples = DEFAULT_ANCHOR_SAMPLES;
        this.minValue      = DEFAULT_MIN_VALUE;
        this.maxValue      = DEFAULT_MAX_VALUE;
        this.spread        = DEFAULT_SPREAD;
        this.nbVarMin      = DEFAULT_NB_VARS_MIN;
        this.nbVarMax      = DEFAULT_NB_VARS_MAX;
        this.maxDomSize    = DEFAULT_MAX_DOM_SIZE;

        this.assumptions   = pa -> true;
    }

    /**
     * Creates a new instance with all fields initialized to their default
     * values.
     */
    public ForAnyPartialAssignment() {
        this(ForAnyPartialAssignment::defaultStrategy);
    }

    /**
     * Configures the seed of the PRNG used to pseudo-randomly generate partial
     * assignments, anchors and values.
     *
     * @param seed the seed to use to initialize the PRNG
     * @return this
     */
    public final ForAnyPartialAssignment withFixedSeed(final long seed) {
        strategy = strategy.withFixedSeed(seed);
        return this;
    }

    /**
     * Configures the underlying quicktheories layer to try to generate a
     * partial assignment satisfying the assumptions at least `attempts` time
     * before failing on value exhaustion.
     *
     * @param attempts the number of attempts to try before value exhaustion.
     * @return this
     */
    public final ForAnyPartialAssignment withGenerateAttempts(final int attempts) {
        strategy = strategy.withGenerateAttempts(attempts);
        return this;
    }

    /**
     * Configures the number of tests which are generated for each anchor value.
     *
     * @param n the number of example partial assignments produced for each
     *          anchor value.
     * @return this
     */
    public final ForAnyPartialAssignment withExamples(final int n) {
        strategy = strategy.withExamples(n);
        return this;
    }

    /**
     * Configures the desired number of anchors which are picked to seed a
     * round of partial assignment tests.
     *
     * @param n the number of anchor values to generate.
     * @return this
     */
    public final ForAnyPartialAssignment withAnchorSamples(final int n) {
        anchorSamples = n;
        return this;
    }

    /**
     * Configures the number of shrink cycles used by the underlying
     * quicktheories layer in order to determine the smallest possible
     * violation instances.
     *
     * @param cycles the number of shrink cycles to use.
     * @return this
     */
    public final ForAnyPartialAssignment withShrinkCycles(final int cycles) {
        strategy = strategy.withShrinkCycles(cycles);
        return this;
    }

    /**
     * Configures the range of values which can appear in the partial
     * assignments.
     *
     * <div>
     *     <h1>Note</h1>
     *     The range of values must be expressed with x being smaller or
     *     equal to y. Any other combination will be rejected.
     * </div>
     *
     * @param x the lowest value that can possibly appear in a partial
     *          assignment.
     * @param y the highest value that can possibly appear in a partial
     *          assignment.
     * @return this
     */
    public final ForAnyPartialAssignment withValuesBetween(final int x, final int y) {
        if (y < x) {
            throw new IllegalArgumentException("x must be >= y");
        }
        minValue = x;
        maxValue = y;

        return this;
    }

    /**
     * Configures the maximum spread between any two values appearing in the
     * partial assignment.
     *
     * @param n the maximum allowed spread (must be positive)
     * @return this
     */
    public final ForAnyPartialAssignment spreading(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0");
        }
        spread = n;

        return this;
    }

    /**
     * Configures the maximum size of the domains composing the partial
     * assignments.
     *
     * @param n the maximum allowed domain size
     * @return this
     */
    public final ForAnyPartialAssignment withDomainsOfSizeUpTo(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0");
        }
        this.maxDomSize = n;

        return this;
    }

    /**
     * Configures the size of the generated partial assignments. All
     * resulting PAs will have that exact size.
     *
     * @param x the exact target size (must be positive).
     * @return this
     */
    public final ForAnyPartialAssignment ofSize(final int x) {
        return ofSizeBetween(x, x);
    }

    /**
     * Configures the minimum and maximum size of the generated partial
     * assignments. All resulting PAs will comprise at least x variables and
     * at most y.
     *
     * @param x minimum number of variables in the generated partial
     *          assignments.
     * @param y maximum number of variables in the generated partial
     *          assignments.
     * @return this
     */
    public final ForAnyPartialAssignment ofSizeBetween(final int x, final int y) {
        if (0 < x || y < x) {
            throw new IllegalArgumentException("0 <= x <= y");
        }
        this.nbVarMin = x;
        this.nbVarMax = y;

        return this;
    }

    /**
     * Enforces the given predicate as an assumption on the generated partial
     * assignments.
     *
     * @param assumption the assumption which must be satisfied
     * @return this
     */
    public final ForAnyPartialAssignment assuming(
            final Predicate<PartialAssignment> assumption) {
        this.assumptions = this.assumptions.and(assumption);
        return this;
    }

    /**
     * Tests the validity of the given predicate by feeding it a large number
     * of test cases.
     * <div>
     *     <h1>Note</h1>
     *     This is the core of the utility of this class.
     * </div>
     *
     * @param test a predicate whose validity is being tested.
     */
    public final void check(final Predicate<PartialAssignment> test) {
        final QuickTheory qt = QuickTheory.qt(this);

        qt.withExamples(anchorSamples)
          .forAll(integers().between(anchorMin(), anchorMax()))
          .checkAssert(anchor ->
             qt.forAll(partialAssignments(anchor))
               .assuming(assumptions)
               .check(test)
          );
    }

    /**
     * Tests the validity of the given assertion by feeding it a large number
     * of test cases.
     * <div>
     *     <h1>Note</h1>
     *     This is the core of the utility of this class.
     * </div>
     *
     * @param test an assertion snippet testing the validity of some property
     *            depending on partial assignment.
     */
    public final void checkAssert(final Consumer<PartialAssignment> test) {
        final QuickTheory qt = QuickTheory.qt(this);

        qt.withExamples(anchorSamples)
          .forAll(integers().between(anchorMin(), anchorMax()))
          .checkAssert(anchor ->
             qt.forAll(partialAssignments(anchor))
               .assuming(assumptions)
               .checkAssert(test)
          );
    }

    /** {@inheritDoc} */
    @Override
    public final Strategy get() {
        return strategy;
    }

    /**
     * Returns a generator of partial assignments whose values surround the
     * given anchor value.
     *
     * @param anchor the anchor value
     * @return a generator of partial assignment.
     */
    private Gen<PartialAssignment> partialAssignments(final int anchor) {
        return Generators.partialAssignments()
                .withVariablesBetween(nbVarMin, nbVarMax)
                .withDomainsOfSizeUpTo(maxDomSize)
                .withValuesRanging(lowerBound(anchor), upperBound(anchor));
    }

    /**
     * @return a generator producing the anchors used during a check or
     * checkAssert phase.
     */
    private Gen<Integer> anchors() {
        return integers().between(anchorMin(), anchorMax());
    }

    /**
     * Returns the lowest acceptable value given an anchor value, the minimum
     * and maximum values and the allowed spread.
     *
     * Note, it returns minValue if the whole spectrum of values between
     * minValue and maxValue is smaller than the size of the spread.
     *
     * @param anchor an anchor value
     * @return lowest acceptable value
     */
    private int lowerBound(final int anchor) {
        if (rangeFitsInSpreadMax()) {
            return minValue;
        } else {
            return anchor - ((spread + 1) / 2);
        }
    }

    /**
     * Returns the highest acceptable value given an anchor value, the minimum
     * and maximum values and the allowed spread.
     *
     * Note, it returns maxValue if the whole spectrum of values between
     * minValue and maxValue is smaller than the size of the spread.
     *
     * @param anchor an anchor value
     * @return highest acceptable value
     */
    private int upperBound(final int anchor) {
        if (rangeFitsInSpreadMax()) {
            return maxValue;
        } else {
            return anchor + (spread / 2);
        }
    }

    /**
     * Returns the lowest acceptable anchor value.
     *
     * @return lowest acceptable anchor value
     */
    private int anchorMin() {
        if (rangeFitsInSpreadMax()) {
            return minValue;
        } else {
            return minValue + ((spread + 1) / 2);
        }
    }

    /**
     * Returns the highest acceptable anchor value.
     *
     * @return highest acceptable anchor value
     */
    private int anchorMax() {
        if (rangeFitsInSpreadMax()) {
            return maxValue;
        } else {
            return maxValue - (spread / 2);
        }
    }

    /**
     * @return true iff all the values between minValue and maxValue fit
     * within the specified spread.
     */
    private boolean rangeFitsInSpreadMax() {
        return (long) maxValue - (long) minValue <= (long) spread;
    }

    /**
     * Returns the default strategy to run all tests.
     *
     * @return the default strategy
     */
    private static Strategy defaultStrategy() {
        return Configuration
                .systemStrategy()
                .withGenerateAttempts(DEFAULT_GEN_ATTEMPTS)
                .withExamples(DEFAULT_EXAMPLES);
    }
}
