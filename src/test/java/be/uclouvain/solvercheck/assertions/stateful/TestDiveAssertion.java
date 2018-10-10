package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.core.task.StatefulFilter;
import be.uclouvain.solvercheck.stateful.StatefulFilterAdapter;
import org.junit.Test;

import java.util.Stack;

public class TestDiveAssertion implements WithSolverCheck {

    @Test(expected = AssertionError.class)
    public void itMustDetectProblemsEvenIfTheOnlyDifferenceOccursAfterSetup() {
        StatefulFilter alpha = stateful(arcConsistent(allDiff()));
        StatefulFilter beta  = incorrectSetup(stateful(arcConsistent(allDiff())));

        assertThat( given().examples(10).an(alpha).isEquivalentTo(beta) );
    }

    // --- Equiv ---
    @Test
    public void checkEquivalentOkWhenReallyEquivalent() {
        StatefulFilter alpha = stateful(arcConsistent(allDiff()));
        StatefulFilter beta  = stateful(arcConsistent(allDiff()));

        assertThat( given().examples(10).an(alpha).isEquivalentTo(beta) );
    }

    @Test(expected = AssertionError.class)
    public void checkEquivalentFailsWhenObviouslyNotEquivalent() {
        StatefulFilter alpha = stateful(arcConsistent(allDiff()));
        StatefulFilter beta  = stateful(boundDConsistent(allDiff()));

        assertThat(
            an(alpha).isEquivalentTo(beta)
               .withShrinkCycles(0)
               .withExamples(10)
        );
    }

    @Test(expected = AssertionError.class)
    public void checkEquivalentFailsWhenInsidiouslyNotEquivalent() {
        StatefulFilter alpha = stateful(arcConsistent(allDiff()));
        StatefulFilter beta  = buggy(
                arcConsistent(allDiff()),
                boundDConsistent(allDiff()),
                25);

        assertThat(
            an(alpha).isEquivalentTo(beta)
               .withShrinkCycles(0)
               .withExamples(30)
        );
    }

    // --- Weaker ---
    @Test
    public void checkWeakerCanBeEquivalent() {
        StatefulFilter alpha = stateful(arcConsistent(allDiff()));
        StatefulFilter beta  = stateful(arcConsistent(allDiff()));

        assertThat( given().examples(10).an(alpha).isWeakerThan(beta) );
    }

    @Test
    public void checkWeakerCanBeStrictlyWeaker() {
        StatefulFilter alpha = stateful(arcConsistent(alwaysTrue()));
        StatefulFilter beta  = stateful(arcConsistent(alwaysFalse()));

        assertThat( given().examples(10).an(alpha).isWeakerThan(beta) );
    }


    @Test(expected = AssertionError.class)
    public void checkWeakerCannotBeStronger() {
        StatefulFilter alpha = stateful(arcConsistent(allDiff()));
        StatefulFilter beta  = stateful(boundDConsistent(allDiff()));

        assertThat( given().examples(10).an(alpha).isWeakerThan(beta) );
    }

    @Test(expected = AssertionError.class)
    public void checkWeakerFailsWhenInsidiouslyNotWeaker() {
        StatefulFilter alpha = buggy(
                boundDConsistent(allDiff()),
                arcConsistent(allDiff()),
                25);
        StatefulFilter beta  = stateful(boundDConsistent(allDiff()));

        assertThat(
                an(alpha).isWeakerThan(beta)
                   .withShrinkCycles(0)
                   .withExamples(30)
        );
    }

    // --- Stronger ---
    @Test
    public void checkStrongerCanBeEquivalent() {
        StatefulFilter alpha = stateful(arcConsistent(allDiff()));
        StatefulFilter beta  = stateful(arcConsistent(allDiff()));

        assertThat( given().examples(10).an(alpha).isStrongerThan(beta) );
    }

    @Test
    public void checkStrongerCanBeStrictlyStronger() {
        StatefulFilter alpha = stateful(arcConsistent(alwaysFalse()));
        StatefulFilter beta  = stateful(arcConsistent(alwaysTrue()));

        assertThat( given().examples(10).an(alpha).isStrongerThan(beta) );
    }


    @Test(expected = AssertionError.class)
    public void checkStrongerCannotBeWeaker() {
        StatefulFilter alpha = stateful(boundDConsistent(allDiff()));
        StatefulFilter beta  = stateful(arcConsistent(allDiff()));

        assertThat( given().examples(10).an(alpha).isStrongerThan(beta) );
    }

    @Test(expected = AssertionError.class)
    public void checkStrongerFailsWhenInsidiouslyNotStronger() {
        StatefulFilter alpha = buggy(
                arcConsistent(allDiff()),
                boundDConsistent(allDiff()),
                25);

        StatefulFilter beta  = stateful(arcConsistent(allDiff()));

        assertThat(
                an(alpha).isStrongerThan(beta)
                   .withShrinkCycles(0)
                   .withExamples(30)
        );
    }

    // --- Strictly Weaker ---
    @Test(expected = AssertionError.class)
    public void checkStrictlyWeakerCannotBeEquivalent() {
        StatefulFilter alpha = stateful(arcConsistent(allDiff()));
        StatefulFilter beta  = stateful(arcConsistent(allDiff()));

        assertThat(
           an(alpha).isStrictlyWeakerThan(beta)
              .withExamples(30)
              .withShrinkCycles(0)
        );
    }

    @Test
    public void checkStrictlyWeakerCanOnlyBeStrictlyWeaker() {
        StatefulFilter alpha = stateful(arcConsistent(alwaysTrue()));
        StatefulFilter beta  = stateful(arcConsistent(alwaysFalse()));

        assertThat(
           given()
              .examples(10)
              .an(alpha).isStrictlyWeakerThan(beta)
              .assuming(pa -> !pa.isError())
        );
    }


    @Test(expected = AssertionError.class)
    public void checkStrictlyWeakerCannotBeStronger() {
        StatefulFilter alpha = stateful(arcConsistent(allDiff()));
        StatefulFilter beta  = stateful(boundDConsistent(allDiff()));

        assertThat( given().examples(10).an(alpha).isStrictlyWeakerThan(beta) );
    }

    @Test(expected = AssertionError.class)
    public void checkStrictlyWeakerFailsWhenInsidiouslyNotWeaker() {
        StatefulFilter alpha = stateful(arcConsistent(alwaysTrue()));
        StatefulFilter beta  = buggy(
                arcConsistent(alwaysTrue()),
                arcConsistent(alwaysFalse()),
                25);

        assertThat(
                an(alpha).isStrictlyWeakerThan(beta)
                   .withShrinkCycles(0)
                   .withExamples(30)
        );
    }

    // --- Strictly Stronger ---
    @Test(expected = AssertionError.class)
    public void checkStrictlyStrongerCannotBeEquivalent() {
        StatefulFilter alpha = stateful(arcConsistent(allDiff()));
        StatefulFilter beta  = stateful(arcConsistent(allDiff()));

        assertThat(
           an(alpha).isStrictlyStrongerThan(beta)
            .withShrinkCycles(0)
            .withExamples(30)
        );
    }

    @Test
    public void checkStrictlyStrongerCanOnlyBeStrictlyStronger() {
        StatefulFilter alpha = stateful(arcConsistent(alwaysFalse()));
        StatefulFilter beta  = stateful(arcConsistent(alwaysTrue()));

        assertThat(
           given()
              .examples(10)
              .an(alpha).isStrictlyStrongerThan(beta)
              .assuming(pa -> !pa.isError())
        );
    }


    @Test(expected = AssertionError.class)
    public void checkStrictlyStrongerCannotBeWeaker() {
        StatefulFilter alpha = stateful(boundDConsistent(allDiff()));
        StatefulFilter beta  = stateful(arcConsistent(allDiff()));

        assertThat( given().examples(10).an(alpha).isStrictlyStrongerThan(beta) );
    }

    // --- Utils ---
    private static StatefulFilter buggy(
            final Filter f1,
            final Filter f2,
            final int threshold) {
        return new BuggyStatefulFilter(f1, f2, threshold);
    }

    /**
     * Returns stateful filter that messes up the initial filtering of the
     * constraint *and then* behaves as if everything was correct.
     *
     * @param decorated a decorated stateful filter
     * @return a stateful filter whose result is only incorrect after setup()
     * and then behaves correctly.
     */
    private StatefulFilter incorrectSetup(final StatefulFilter decorated) {
        return new StatefulFilter() {
            /** a marker to remember whether we are right after the setup */
            private boolean initial = true;
            /**
             * The root of the problem. This is the value being returned
             * after setup
             */
            private PartialAssignment root;

            /** {@inheritDoc} */
            @Override
            public void setup(PartialAssignment initialDomains) {
                initial = true;
                root = initialDomains;
                decorated.setup(initialDomains);
            }

            /** {@inheritDoc} */
            @Override
            public void pushState() {
                decorated.pushState();
            }

            /** {@inheritDoc} */
            @Override
            public void popState() {
                decorated.popState();
            }

            /** {@inheritDoc} */
            @Override
            public PartialAssignment currentState() {
                if (initial) {
                    return root;
                }
                return decorated.currentState();
            }

            /** {@inheritDoc} */
            @Override
            public void branchOn(int variable, Operator op, int value) {
                initial = false;
                decorated.branchOn(variable, op, value);
            }
        };
    }

    private static class BuggyStatefulFilter implements StatefulFilter {
        private final Filter filter1;
        private final Filter filter2;
        private final int threshold;
        private final Stack<PartialAssignment> snapshots;

        private int count;
        private PartialAssignment current;

        BuggyStatefulFilter(
                final Filter filter1,
                final Filter filter2,
                final int threshold) {
            this.filter1 = filter1;
            this.filter2 = filter2;
            this.threshold = threshold;
            this.snapshots = new Stack<>();
            this.count = 0;
        }

        @Override
        public void setup(PartialAssignment initialDomains) {
            snapshots.clear();
            current = filter(initialDomains);
            count = 0;
        }

        @Override
        public void pushState() {
            snapshots.push(current);
        }

        @Override
        public void popState() {
            if (!snapshots.isEmpty()) {
                current = snapshots.pop();
                count++;
            }
        }

        @Override
        public PartialAssignment currentState() {
            return current;
        }

        @Override
        public void branchOn(int variable, Operator op, int value) {
            current = filter(PartialAssignment.restrict(current, variable, op, value));
        }

        private PartialAssignment filter(final PartialAssignment partial) {
            Filter f = filter1;

            if (count >= threshold) {
                f = filter2;
            }

            return f.filter(partial);
        }
    }
}
