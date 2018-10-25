package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.assertions.util.ForAnyPartialAssignment;
import be.uclouvain.solvercheck.core.data.impl.AssignmentFactory;
import be.uclouvain.solvercheck.generators.WithCpGenerators;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;

import java.util.Arrays;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;

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
    public void testFromArray() {
        qt().forAll(
           arrays().ofIntegers(integers().all()).withLengthBetween(0, 1000))
           .check(array -> {
               int[] values =
                  Arrays.stream(array).mapToInt(Integer::intValue).toArray();
               return Arrays.asList(array).equals(AssignmentFactory.from(values));
           });
    }

    @Test
    public void testFromPartialAssignmentEqualsAsAssignmentWhenCompete() {
        new ForAnyPartialAssignment()
                .withDomainsOfSizeUpTo(1)
                .assuming(PartialAssignment::isComplete)
                .check(partialAssignment ->
                        partialAssignment.asAssignment()
                            .equals(AssignmentFactory.from(partialAssignment))
                );
    }

    @Test
    public void testFromPartialAssignmentFailsWhenNotCompete() {
        new ForAnyPartialAssignment()
                .assuming(partialAssignment -> !partialAssignment.isComplete())
                .check(partialAssignment ->
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
}
