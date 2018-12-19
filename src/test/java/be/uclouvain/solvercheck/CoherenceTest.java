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
           forAll(partialAssignment().withValuesRanging(0, 3)).assertThat(
             a(boundZConsistent(allDiff())).isWeakerThan(arcConsistent(allDiff()))
           )
        );
    }

    @Test
    public void statelessBoundZisWeakerThanBoundD() {
        assertThat(
           forAll(partialAssignment().withValuesRanging(0, 3)).assertThat(
             a(boundZConsistent(allDiff())).isWeakerThan(boundDConsistent(allDiff()))
           )
        );
    }
    @Test
    public void statelessBoundDisWeakerThanArc() {
        assertThat(
           forAll(partialAssignment().withValuesRanging(0, 3)).assertThat(
             a(boundDConsistent(allDiff())).isWeakerThan(arcConsistent(allDiff()))
           )
        );
    }
    @Test
    public void statelessArcIStrongerThanRange() {
        assertThat(
           forAll(partialAssignment().withValuesRanging(0, 3)).assertThat(
             a(arcConsistent(allDiff())).isStrongerThan(rangeConsistent(allDiff()))
           )
        );
    }


    // --- Stateful checks ---
    @Test
    public void statefulBoundZisWeakerThanArc() {
        assertThat(
           forAll(partialAssignment().withValuesRanging(0, 3)).assertThat(
             a(stateful(boundZConsistent(allDiff())))
              .isWeakerThan(stateful(arcConsistent(allDiff())))
              .diving(5)
           )
        );
    }

    @Test
    public void statefulBoundZisWeakerThanBoundD() {
        assertThat(
           forAll(partialAssignment().withValuesRanging(0, 3)).assertThat(
             a(stateful(boundZConsistent(allDiff())))
              .isWeakerThan(stateful(boundDConsistent(allDiff())))
           )
        );
    }
    @Test
    public void statefulBoundDisWeakerThanArc() {
        assertThat(
           forAll(partialAssignment().withValuesRanging(0, 3)).assertThat(
             a(stateful(boundDConsistent(allDiff())))
              .isWeakerThan(stateful(arcConsistent(allDiff())))
           )
        );
    }
    @Test
    public void statefulArcIStrongerThanRange() {
        assertThat(
           forAll(partialAssignment().withValuesRanging(0, 3)).assertThat(
             a(stateful(arcConsistent(allDiff())))
              .isStrongerThan(stateful(rangeConsistent(allDiff())))
           )
        );
    }

}
