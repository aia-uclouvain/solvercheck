package be.uclouvain.solvercheck.assertions.util;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.core.data.PartialAssignment;

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
     * Lets you express the parametric assertion in terms of its actual
     * parameters.
     *
     * @param assertion the parametric assertion expressed in terms of
     *                  its abstract parameter.
     * @return the (parametric) assertion which can be checked.
     */
    public Assertion itIsTrueThat(final Predicate<PartialAssignment> assertion) {
        return assertThat(pa -> rnd -> {
               if (!assertion.test(pa)) {
                   throw new AssertionError();
               }
           }
        );
    }

    /**
     * Lets you express the parametric assertion in terms of its actual
     * parameters.
     *
     * @param assertion the parametric assertion expressed in terms of
     *                  its abstract parameter.
     * @return the (parametric) assertion which can be checked.
     */
    public Assertion itIsFalseThat(final Predicate<PartialAssignment> assertion) {
        return itIsTrueThat(assertion.negate());
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
    public Assertion assertThat(
            final Function<PartialAssignment, Assertion> assertion) {
        return rnd -> doCheckAssert(rnd, pa -> assertion.apply(pa).check(rnd));
    }
}
