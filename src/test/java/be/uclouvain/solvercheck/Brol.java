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
            an(arcConsistent(allDiff())).isStrongerThan(boundZConsistent(allDiff()))
            .forAll(partialAssignments()
                    .withUpToVariables(4)
                    .withValuesRanging(-10, 10))

        );
    }

}
