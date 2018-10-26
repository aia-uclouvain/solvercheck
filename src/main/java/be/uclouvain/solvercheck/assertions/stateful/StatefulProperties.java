package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.StatefulFilter;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;

/**
 * This class acts as a stateless property factory. It lets you create basic
 * predicates (bearing on partial assignment) applicable for some given
 * `StatefulFilter`.
 */
public final class StatefulProperties {

    /** An utility class has no public constructor. */
    private StatefulProperties() { }

    /**
     * A 'property' is nothing but the condition being checked. Note: given
     * the stateful context, this condition should hold its own state and
     * evaluate according to it.
     */
    public interface Property {
        /**
         * Initialises the state of the property, given initial value of the
         * variables domains.
         *
         * @param root the initial value of the variables domains.
         */
        void setup(PartialAssignment root);
        /**
         * Saves the state of the property.
         */
        void pushState();
        /**
         * Restores the state of the property.
         */
        void popState();
        /**
         * Tells both the property that pseudo solver decided to to branch on
         * the following restriction: [[ variable op value ]].
         *
         * @param variable the variable on which a branching decision is made.
         * @param op the constraint imposed on the value that can be taken by
         *           `variable`.
         * @param value in conjunction with `op` determines the constraint
         *              imposed on the values that can be taken by `variable`.
         */
        void branchOn(int variable, Operator op, int value);
        /**
         * @return iff the current state of `actual` or that of `other` is a leaf
         * of the search tree.
         */
        boolean isCurrentStateLeaf();
        /**
         * Evaluates the local assertion described by `this` and retruns its
         * truth value.
         *
         * @return true iff the assertion is verified in the current node.
         */
        boolean checkTest();

        /**
         * @return the actual state of the property. (What does the initial
         * domains look like after having been modified etc.. to reach this
         * point).
         */
        PartialAssignment actualState();

        /**
         * @return the expected state of the property. (What should the initial
         * domains look like after having been modified etc.. to reach this
         * point).
         */
        PartialAssignment expectedState();
    }

    /**
     * An utility class to wrap stateful properties.
     */
    private static abstract class StatefulFilterComparisonProperty implements Property {
        /** The stateful filter under test. */
        protected final StatefulFilter actual;

        /** The reference stateful filter. */
        protected final StatefulFilter other;

        /**
         * Creates a new stateful comparison property that compares the
         * propagation strength of `actual` and `other` in the context of a
         * stateful check.
         *
         * @param actual the stateful filter under test
         * @param other  the reference stateful filter
         */
        public StatefulFilterComparisonProperty(final StatefulFilter actual,
                                                final StatefulFilter other) {
            this.actual = actual;
            this.other  = other;
        }

        /** {@inheritDoc} */
        @Override
        public final void setup(final PartialAssignment root) {
            actual.setup(root);
            other .setup(root);
        }

        /** {@inheritDoc} */
        @Override
        public final void pushState() {
            actual.pushState();
            other .pushState();
        }

        /** {@inheritDoc} */
        @Override
        public final void popState() {
            actual.popState();
            other .popState();
        }

        /** {@inheritDoc} */
        @Override
        public final void branchOn(
           final int variable,
           final Operator op,
           final int value) {

            actual.branchOn(variable, op, value);
            other .branchOn(variable, op, value);
        }

        /** {@inheritDoc} */
        @Override
        public final boolean isCurrentStateLeaf() {
            return actual.currentState().isLeaf()
                || other .currentState().isLeaf();
        }

        /** {@inheritDoc} */
        @Override
        public final PartialAssignment actualState() {
            return actual.currentState();
        }

        /** {@inheritDoc} */
        @Override
        public final PartialAssignment expectedState() {
            return other.currentState();
        }
    }

    /**
     * Checks that `actual` and `other` have equivalent propagating strengths
     * for the given `domains` test case.
     *
     * @return true iff actual is equivalent to other
     */
    public static Property equivalentTo(final StatefulFilter actual,
                                        final StatefulFilter other) {

        return new StatefulFilterComparisonProperty(actual, other) {
            /** {@inheritDoc} */
            @Override
            public boolean checkTest() {
                final PartialOrdering comparison =
                   actual.currentState().compareWith(other.currentState());

                return comparison == PartialOrdering.EQUIVALENT;
            }
        };
    }

    /**
     * Checks that `actual` is weaker or equivalent to `other` for the given
     * `domains` test case.
     *
     * @return true iff actual is weaker or equivalent to other
     */
    public static Property weakerThan(final StatefulFilter actual,
                                      final StatefulFilter other) {
        return new StatefulFilterComparisonProperty(actual, other) {
            /** {@inheritDoc} */
            @Override
            public boolean checkTest() {
                final PartialOrdering comparison =
                   actual.currentState().compareWith(other.currentState());

                return comparison == PartialOrdering.WEAKER
                   || comparison == PartialOrdering.EQUIVALENT;
            }
        };
    }
    /**
     * Checks that `actual` is weaker than `other` for the given `domains`
     * test case.
     *
     * @return true iff actual is weaker than other
     */
    public static Property strictlyWeakerThan(final StatefulFilter actual,
                                              final StatefulFilter other) {
        return new StatefulFilterComparisonProperty(actual, other) {
            /** {@inheritDoc} */
            @Override
            public boolean checkTest() {
                final PartialOrdering comparison =
                   actual.currentState().compareWith(other.currentState());

                return comparison == PartialOrdering.WEAKER;
            }
        };
    }
    /**
     * Checks that `actual` is stronger or equivalent to `other` for the given
     * `domain` test case.
     *
     * @return true iff actual is stronger or equivalent to other
     */
    public static Property strongerThan(final StatefulFilter actual,
                                        final StatefulFilter other) {
        return new StatefulFilterComparisonProperty(actual, other) {
            /** {@inheritDoc} */
            @Override
            public boolean checkTest() {
                final PartialOrdering comparison =
                   actual.currentState().compareWith(other.currentState());

                return comparison == PartialOrdering.STRONGER
                   || comparison == PartialOrdering.EQUIVALENT;
            }
        };
    }

    /**
     * Checks that `actual` is stronger than `other` for the given `domains`
     * test case.
     *
     * @return true iff actual is stronger than other
     */
    public static Property strictlyStrongerThan(final StatefulFilter actual,
                                                final StatefulFilter other) {
        return new StatefulFilterComparisonProperty(actual, other) {
            /** {@inheritDoc} */
            @Override
            public boolean checkTest() {
                final PartialOrdering comparison =
                   actual.currentState().compareWith(other.currentState());

                return comparison == PartialOrdering.STRONGER;
            }
        };
    }
}
