package be.uclouvain.solvercheck;

import be.uclouvain.solvercheck.assertions.ForAnyPartialAssignment;
import be.uclouvain.solvercheck.consistencies.ArcConsitency;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.DomainFilter;
import be.uclouvain.solvercheck.stateful.StatefulFilterAdapter;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Function;

import static org.quicktheories.QuickTheory.qt;

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
                .forAll(partialAssignments().withValuesRanging(0, 3))
        );
    }
    @Test
    public void statelessBoundZisWeakerThanBoundD() {
        assertThat(
                a(boundZConsistent(allDiff()))
                        .isWeakerThan(boundDConsistent(allDiff()))
                        .forAll(partialAssignments().withValuesRanging(0, 3))
        );
    }
    @Test
    public void statefulBoundZisWeakerThanBoundD() {
        assertThat(
                a(stateful(boundZConsistent(allDiff())))
                        .isWeakerThan(stateful(boundDConsistent(allDiff())))
                        .forAll(partialAssignments().withValuesRanging(0, 3))
        );
    }
    @Test
    public void statefulBoundDisWeakerThanArc() {
        assertThat(
                a(stateful(boundDConsistent(allDiff())))
                        .isWeakerThan(stateful(arcConsistent(allDiff())))
                        .forAll(partialAssignments().withValuesRanging(0, 3))
        );
    }
    @Test
    public void statefulArcIStrongerThanRange() {
        assertThat(
                a(stateful(arcConsistent(allDiff())))
                        .isStrongerThan(stateful(rangeConsistent(allDiff())))
                        .forAll(partialAssignments().withValuesRanging(0, 3))
        );
    }

    @Test
    public void testHybridEquivalentToNormalConsistency() {
        assertThat(
                forAll(integers().between(0, 5))
                .itIsTrueThat(size -> {
                    Function<Checker, DomainFilter>[] filters =
                            new Function[size];

                    for (int i =  0; i < size; i++) {
                        filters[i] = ArcConsitency::domainFilter;
                    }

                    return an(arcConsistent(allDiff()))
                            .isEquivalentTo(hybrid(allDiff(), filters))
                            .assuming(pa -> pa.size() == size);
                })
        );
    }


}
