package be.uclouvain.solvercheck;

import be.uclouvain.solvercheck.assertions.ForAnyPartialAssignment;
import org.junit.Test;

public class Brol implements WithSolverCheck {


    @Test
    public void testTables() {
        assertThat(
            forAll(tables()).itIsTrueThat(t ->
                    an(arcConsistent(allDiff())).isStrongerThan(boundZConsistent(table(t)))
            )
        );
    }

    @Test
    public void arcConsistentIsStrongerThanBoundZ() {
        new ForAnyPartialAssignment()
            .ofSizeBetween(0, 4)
            .withValuesBetween(-10, 10)
            .check(
                an(arcConsistent(allDiff())).isStrongerThan(boundZConsistent(allDiff()))
            );
    }


    @Test
    public void statefulBoundZisWeakerThanArc() {
        assertThat(
            a(stateful(boundZConsistent(allDiff())))
                    .isWeakerThan(stateful(arcConsistent(allDiff())))
                    .forAnyPartialAssignment().withValuesBetween(0, 3)
        );
    }
    @Test
    public void statelessBoundZisWeakerThanBoundD() {
        assertThat(
                a(boundZConsistent(allDiff()))
                        .isWeakerThan(boundDConsistent(allDiff()))
                        .forAnyPartialAssignment().withValuesBetween(0, 3)
        );
    }
    @Test
    public void statefulBoundZisWeakerThanBoundD() {
        assertThat(
                a(stateful(boundZConsistent(allDiff())))
                        .isWeakerThan(stateful(boundDConsistent(allDiff())))
                        .forAnyPartialAssignment().withValuesBetween(0, 3)
        );
    }
    @Test
    public void statefulBoundDisWeakerThanArc() {
        assertThat(
                a(stateful(boundDConsistent(allDiff())))
                        .isWeakerThan(stateful(arcConsistent(allDiff())))
                        .forAnyPartialAssignment().withValuesBetween(0, 3)
        );
    }
    @Test
    public void statefulArcIStrongerThanRange() {
        assertThat(
                a(stateful(arcConsistent(allDiff())))
                        .isStrongerThan(stateful(rangeConsistent(allDiff())))
                        .forAnyPartialAssignment().withValuesBetween(0, 3)
        );
    }
}
