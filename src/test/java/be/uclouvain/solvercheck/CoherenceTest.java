package be.uclouvain.solvercheck;

import org.junit.Test;

/**
 * The goal of these tests is to evaluate the coherence of the overall system.
 * It combines many features from many different packages and combines them
 * together to check whether the consistency hierarchy is respected.
 *
 * Doing so, it demonstrates the usability + readability of the library.
 * An interesting thing to note is that passing from a stateless check to a
 * stateful one requires almost no changes in the test (only the removal of
 * the `stateful` keyword).
 */
public class CoherenceTest implements WithSolverCheck {

    // --- Stateless checks ---
    @Test
    public void statelessBoundZisWeakerThanArc() {
        assertThat(
            a(boundZConsistent(allDiff()))
              .isWeakerThan(arcConsistent(allDiff()))
              .forAnyPartialAssignment()
              .withValuesBetween(0, 3)
        );
    }

    @Test
    public void statelessBoundZisWeakerThanBoundD() {
        assertThat(
            a(boundZConsistent(allDiff()))
              .isWeakerThan(boundDConsistent(allDiff()))
              .forAnyPartialAssignment()
              .withValuesBetween(0, 3)
        );
    }
    @Test
    public void statelessBoundDisWeakerThanArc() {
        assertThat(
            a(boundDConsistent(allDiff()))
              .isWeakerThan(arcConsistent(allDiff()))
              .forAnyPartialAssignment()
              .withValuesBetween(0, 3)
        );
    }
    @Test
    public void statelessArcIStrongerThanRange() {
        assertThat(
            a(arcConsistent(allDiff()))
              .isStrongerThan(rangeConsistent(allDiff()))
              .forAnyPartialAssignment()
              .withValuesBetween(0, 3)
        );
    }


    // --- Stateful checks ---
    @Test
    public void statefulBoundZisWeakerThanArc() {
        assertThat(
            a(stateful(boundZConsistent(allDiff())))
            .isWeakerThan(stateful(arcConsistent(allDiff())))
            .forAnyPartialAssignment()
            .diving(5)
            .withValuesBetween(0, 3)
        );
    }

    @Test
    public void statefulBoundZisWeakerThanBoundD() {
        assertThat(
            a(stateful(boundZConsistent(allDiff())))
            .isWeakerThan(stateful(boundDConsistent(allDiff())))
            .forAnyPartialAssignment()
            .withValuesBetween(0, 3)
        );
    }
    @Test
    public void statefulBoundDisWeakerThanArc() {
        assertThat(
            a(stateful(boundDConsistent(allDiff())))
            .isWeakerThan(stateful(arcConsistent(allDiff())))
            .forAnyPartialAssignment()
            .withValuesBetween(0, 3)
        );
    }
    @Test
    public void statefulArcIStrongerThanRange() {
        assertThat(
            a(stateful(arcConsistent(allDiff())))
            .isStrongerThan(stateful(rangeConsistent(allDiff())))
            .forAnyPartialAssignment()
            .withValuesBetween(0, 3)
        );
    }

}
