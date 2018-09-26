package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.core.data.impl.AssignmentFactory;
import be.uclouvain.solvercheck.generators.WithCpGenerators;
import org.junit.Test;
import org.quicktheories.QuickTheory;
import org.quicktheories.WithQuickTheories;

import java.util.function.Predicate;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

public class TestAssignmentFactory
        implements WithQuickTheories, WithCpGenerators {

    @Test
    public void testFromList() {
        qt().forAll(lists().of(integers().all()).ofSizeBetween(0, 1000))
                .check(lst ->
                        lst.equals(AssignmentFactory.from(lst))
                );
    }

    @Test
    public void testFromPartialAssignmentEqualsAsAssignmentWhenCompete() {
        forAnyPartialAssignment(
                partialAssignment -> partialAssignment.isComplete(),
                partialAssignment ->
                    partialAssignment.asAssignment()
                            .equals(AssignmentFactory.from(partialAssignment))
        );
    }

    @Test
    public void testFromPartialAssignmentFailsWhenNotCompete() {
        forAnyPartialAssignment(
                partialAssignment -> !partialAssignment.isComplete(),
                partialAssignment ->
                    failsThrowing(
                        IllegalStateException.class,
                        () -> AssignmentFactory.from(partialAssignment))
        );
    }

    @Test
    public void testCollector() {
        qt().forAll(lists().of(integers().all()).ofSizeBetween(0, 1000))
            .check(lst ->
                lst.equals(lst.stream().collect(AssignmentFactory.collector()))
            );
    }

    private void forAnyPartialAssignment(
            final Predicate<PartialAssignment> assumptions,
            final Predicate<PartialAssignment> actual) {

        final QuickTheory qt = qt()
                .withGenerateAttempts(10000)
                .withFixedSeed(1234567890);

        qt.withExamples(100)
          .forAll(integers().between(MIN_VALUE+5, MAX_VALUE-4))
          .checkAssert(anchor ->
             qt.withExamples(10)
               .forAll(
                  partialAssignments()
                    .withUpToVariables(5)
                    .withValuesRanging(anchor-5, anchor+4))
               .assuming(assumptions)
               .check(actual)
          );
    }
}
