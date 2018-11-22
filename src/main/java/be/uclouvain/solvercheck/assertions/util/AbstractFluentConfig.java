package be.uclouvain.solvercheck.assertions.util;

import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.generators.GeneratorsDSL;
import be.uclouvain.solvercheck.fuzzing.Randomness;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static be.uclouvain.solvercheck.assertions.util.Defaults.DEFAULT_ANCHOR_SAMPLES;
import static be.uclouvain.solvercheck.assertions.util.Defaults.DEFAULT_EXAMPLES;
import static be.uclouvain.solvercheck.assertions.util.Defaults.DEFAULT_MIN_VALUE;
import static be.uclouvain.solvercheck.assertions.util.Defaults.DEFAULT_MAX_VALUE;
import static be.uclouvain.solvercheck.assertions.util.Defaults.DEFAULT_SPREAD;
import static be.uclouvain.solvercheck.assertions.util.Defaults.DEFAULT_NB_VARS_MIN;
import static be.uclouvain.solvercheck.assertions.util.Defaults.DEFAULT_NB_VARS_MAX;
import static be.uclouvain.solvercheck.assertions.util.Defaults.DEFAULT_MAX_DOM_SIZE;

/**
 * This class provides the basic services to create classes of assertions that
 * apply to all partial assignments, providing a fluent interface for the
 * property checking configuration.
 *
 * @param <T> the concrete type of the subclass.
 */
public abstract class AbstractFluentConfig<T extends AbstractFluentConfig<T>>
        implements WithFluentConfig<T> {
    /**
     * The number of `anchor values` which designate the 'center' of the
     * values distributions in a partial assignment.
     */
    private int anchorSamples;
    /**
     * The number of number of partial assignment generated (and tested) for
     * each anchor value.
     */
    private int examples;
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
     * A function parsing a partial assignment to make an intelligible
     * explanation (description) out of it. This is particularly appropriate
     * for the constraints having different groups of arguments like i.e. the
     * element constraint.
     */
    private Function<PartialAssignment, String> description;

    /**
     * Creates a new instance with all fields initialized to their default
     * values.
     */
    public AbstractFluentConfig() {
        this.anchorSamples = DEFAULT_ANCHOR_SAMPLES;
        this.examples      = DEFAULT_EXAMPLES;
        this.minValue      = DEFAULT_MIN_VALUE;
        this.maxValue      = DEFAULT_MAX_VALUE;
        this.spread        = DEFAULT_SPREAD;
        this.nbVarMin      = DEFAULT_NB_VARS_MIN;
        this.nbVarMax      = DEFAULT_NB_VARS_MAX;
        this.maxDomSize    = DEFAULT_MAX_DOM_SIZE;

        this.assumptions   = pa -> true;
        this.description   = PartialAssignment::toString;
    }

    /**
     * This method must return <b>this</b> with the appropriate type.
     * @return this
     */
    protected abstract T getThis();

    /** {@inheritDoc} */
    @Override
    public final T forAnyPartialAssignment() {
        return getThis();
    }

    /** {@inheritDoc} */
    @Override @SuppressWarnings("checkstyle:hiddenfield")
    public final T describedAs(final Function<PartialAssignment, String> description) {
        this.description = description;
        return getThis();
    }

    /** {@inheritDoc} */
    @Override
    public final T withExamples(final int n) {
        this.examples = n;
        return getThis();
    }

    /** {@inheritDoc} */
    @Override
    public final T withAnchorSamples(final int n) {
        anchorSamples = n;
        return getThis();
    }

    /** {@inheritDoc} */
    @Override
    public final T withValuesBetween(final int x, final int y) {
        if (y < x) {
            throw new IllegalArgumentException("x must be >= y");
        }
        minValue = x;
        maxValue = y;

        return getThis();
    }

    /** {@inheritDoc} */
    @Override
    public final T spreading(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0");
        }
        spread = n;

        return getThis();
    }

    /** {@inheritDoc} */
    @Override
    public final T withDomainsOfSizeUpTo(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0");
        }
        this.maxDomSize = n;

        return getThis();
    }

    /** {@inheritDoc} */
    @Override
    public final T ofSize(final int x) {
        return ofSizeBetween(x, x);
    }

    /** {@inheritDoc} */
    @Override
    public final T ofSizeBetween(final int x, final int y) {
        if (0 > x || y < x) {
            throw new IllegalArgumentException("0 <= x <= y");
        }
        this.nbVarMin = x;
        this.nbVarMax = y;

        return getThis();
    }

    /** {@inheritDoc} */
    @Override
    public final T assuming(
            final Predicate<PartialAssignment> assumption) {
        this.assumptions = this.assumptions.and(assumption);
        return getThis();
    }

    /**
     * Tests the validity of the given predicate by feeding it a large number
     * of test cases.
     * <div>
     *     <h1>Note</h1>
     *     This is the core of the utility of this class.
     * </div>
     *
     * @param rnd the source of randomness used for the fuzzing.
     * @param test a predicate whose validity is being tested.
     */
    protected final void doCheck(final Randomness rnd,
                                 final Predicate<PartialAssignment> test) {
        Optional<PartialAssignment> failure = anchors()
           .build()
           .generate(rnd)
           .limit(anchorSamples)
           .map(a  -> partialAssignments(a).build().generate(rnd).limit(examples))
           .flatMap(pas -> pas.filter(assumptions).filter(pa -> !test.test(pa)))
           //.parallel()
           .findAny();

        if (failure.isPresent()) {
            throw new AssertionError(
               explanation(rnd, failure.get(), "Property violated")
            );
        }
    }

    /**
     * Tests the validity of the given assertion by feeding it a large number
     * of test cases.
     * <div>
     *     <h1>Note</h1>
     *     This is the core of the utility of this class.
     * </div>
     *
     * @param rnd the source of randomness used for the fuzzing.
     * @param test an assertion snippet testing the validity of some property
     *            depending on partial assignment.
     */
    protected final void doCheckAssert(final Randomness rnd,
                                       final Consumer<PartialAssignment> test) {
        anchors()
           .build()
           .generate(rnd)
           .limit(anchorSamples)
           .map(a  -> partialAssignments(a).build().generate(rnd).limit(examples))
           .flatMap(pas -> pas.filter(assumptions))
           //.parallel()
           .forEach(pa -> {
               try {
                 test.accept(pa);
               } catch (AssertionError cause) {
                 throw new AssertionError(
                      explanation(rnd, pa, cause.getMessage()), cause);
               } catch (Throwable cause) {
                 throw new AssertionError(
                      explanation(rnd, pa,
                      "\nCAUSE     : An exception was caught"
                         + "\n###########################"),
                      cause);
               }
           });
    }

    /**
     * Creates an intelligible error report which can be used to reproduce an
     * investigate an error witness.
     *
     * @param rnd the source of randomness used for the fuzzing.
     * @param pa the partial assignment
     * @param cause an explanatory message about why the violation occurred.
     * @return An error message giving the details of the witnessed
     * property violation.
     */
    protected final String explanation(final Randomness rnd,
                                       final PartialAssignment pa,
                                       final String cause) {

        final StringBuilder builder = new StringBuilder("\n");
        builder.append("########################### \n");
        builder.append("SEED    : ").append(Long.toHexString(rnd.getSeed())).append("\n");
        builder.append("CAUSE   : ").append(cause).append("\n");
        builder.append("WITNESS : ").append(description.apply(pa)).append("\n");
        builder.append("########################### \n");

        return builder.toString();
    }

    /**
     * Returns a generator of partial assignments whose values surround the
     * given anchor value.
     *
     * @param anchor the anchor value
     * @return a generator of partial assignment.
     */
    private GeneratorsDSL.GenPartialAssignmentBuilder partialAssignments(final int anchor) {
        return GeneratorsDSL.partialAssignments()
                .withVariablesBetween(nbVarMin, nbVarMax)
                .withDomainsOfSizeUpTo(maxDomSize)
                .withValuesRanging(lowerBound(anchor), upperBound(anchor));
    }

    /**
     * @return a generator producing the anchors used during a check or
     * checkAssert phase.
     */
    private GeneratorsDSL.GenIntBuilder anchors() {
        return GeneratorsDSL.ints().between(anchorMin(), anchorMax());
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
}
