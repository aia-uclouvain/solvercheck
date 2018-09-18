package be.uclouvain.solvercheck;

import org.junit.Test;

public class Brol implements WithSolverCheck {

    @Test
    public void testDescribed() {
        /*
        assertThat(
            forAll(tables()).itIsTrueThat(t ->
                an(arcConsistent(allDiff())).isStrongerThan(boundZConsistent(table(t)))
        ));
        */

        assertThat(
            given()
            .theRandomSeed(42)
            .examples(5)
            .attempts(10)
            .an(arcConsistent(allDiff())).isStrongerThan(boundZConsistent(allDiff()))
            .assuming(partialAssignment -> !partialAssignment.isError())
        );
    }

}
