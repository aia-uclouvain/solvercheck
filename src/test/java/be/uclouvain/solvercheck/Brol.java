package be.uclouvain.solvercheck;

import be.uclouvain.solvercheck.assertions.stateful.DiveAssertion;
import be.uclouvain.solvercheck.core.task.impl.StatefulFilterAdapter;
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
/*
        assertThat(
            an(arcConsistent(allDiff())).isWeakerThan(boundZConsistent(allDiff()))
            .forAll(partialAssignments()
                    .withUpToVariables(4)
                    .withValuesRanging(-10, 10))

        );
*/
    }


    @Test
    public void brol() {
        assertThat(
            new DiveAssertion(new StatefulFilterAdapter(boundZConsistent(allDiff())))
                .isWeakerThan(new StatefulFilterAdapter(arcConsistent(allDiff())))
                .forAll(partialAssignments().withValuesRanging(0, 3))
                .assuming(pa -> !pa.isEmpty())
                //.assuming(pa -> !pa.isError())
        );
    }


}
