package be.uclouvain.solvercheck.assertions.util;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class provides a simple way to define and check a property that
 * should hold for any partial assignment. However, this class should not be
 * used when StatelessAssertion or StatefulAssertion can be used. Indeed, the latter
 * two provide much more advanced services to check the correctness of a
 * constraint.
 */
public final class ForAnyPartialAssignment
        extends AbstractFluentConfig<ForAnyPartialAssignment> {

    /** Creates a new instance. */
    public ForAnyPartialAssignment() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    protected ForAnyPartialAssignment getThis() {
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
    public void check(final Predicate<PartialAssignment> test) {
        doCheck(test);
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
    public void checkAssert(final Consumer<PartialAssignment> test) {
        doCheckAssert(test);
    }


    /**
     * Tests the validity of the given assertion by feeding it a large number
     * of test cases.
     * <div>
     *     <h1>Note</h1>
     *     This is the core of the utility of this class.
     * </div>
     *
     * @param assertion an assertion snippet testing the validity of some
     *                  property depending on partial assignment.
     * @return an assertion which is to be tested against a wide range of
     * partial assignments.
     */
    public Assertion itIsTrueThat(
            final Function<PartialAssignment, Assertion> assertion) {
        return () -> doCheckAssert(pa -> assertion.apply(pa).check());
    }
}
