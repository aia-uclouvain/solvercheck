package be.uclouvain.solvercheck;

import be.uclouvain.solvercheck.assertions.WithAssertions;
import be.uclouvain.solvercheck.checkers.Checkers;
import be.uclouvain.solvercheck.consistencies.ArcConsitency;
import be.uclouvain.solvercheck.consistencies.BoundDConsistency;
import be.uclouvain.solvercheck.consistencies.BoundZConsistency;
import org.junit.Test;

public class Brol implements WithSolverCheck {

    @Test
    public void testDescribed() {

        assertThat(
                forAll(tables().describedAs(t -> String.format("Table(%s)", t)))
                .itIsTrueThat(t ->

                    propagator(new ArcConsitency(Checkers.table(t)))
                        .isEquivalentTo(
                            new BoundZConsistency(Checkers.table(t))
                        )
                        .assuming(partialAssignment -> !partialAssignment.isError())
                )

                /*
            propagator(new ArcConsitency(Checkers.allDiff()))
                .isEquivalentTo(new BoundDConsistency(Checkers.allDiff()))
                .assuming(domains -> !domains.isError())
                */
        );
    }

}
