package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.assertions.util.ForAnyPartialAssignment;
import be.uclouvain.solvercheck.generators.WithGenerators;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.Arrays;
import java.util.List;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;
import static org.junit.Assert.assertEquals;

public class TestAssignment
        implements WithQuickTheories, WithGenerators {

    @Test
    public void testSize() {
        qt().forAll(listOfInt()).check(a ->
                a.size() == Assignment.from(a).size()
        );
    }

    @Test
    public void getReturnsTheIthElementIffItsAValidIndex(){
        qt().withGenerateAttempts(10000)
            .forAll(listOfInt(), integers().between(0, 10))
            .assuming((a, i) -> isValidIndex(i, a.size()))
            .check   ((a, i) -> a.get(i).equals(Assignment.from(a).get(i)));
    }

    @Test
    public void getFailsWithExceptionWhenItsNotAValidIndex() {
        qt().withGenerateAttempts(10000)
            .forAll(assignments(), integers().between(0, 10))
            .assuming((a, i) -> !isValidIndex(i, a.size()))
            .check   ((a, i) -> failsThrowing(IndexOutOfBoundsException.class, () -> a.get(i) ));
    }

    @Test
    public void testEqualsAllValuesMatch() {
        qt().forAll(listOfInt(), listOfInt())
            .check ((a, b) -> a.equals(b) == Assignment.from(a).equals(Assignment.from(b)));
    }

    @Test
    public void testHashCode() {
        qt().forAll(assignments(), assignments())
                .check ((a, b) -> a.equals(b) == (a.hashCode() == b.hashCode()));
    }

    @Test
    public void testToString() {
        assertEquals(
            Assignment.from(List.of(1, 2, 3, 4)).toString(),
            "x0=1, x1=2, x2=3, x3=4"
        );
    }

    @Test
    public void testFromList() {
        qt().forAll(lists().of(integers().all()).ofSizeBetween(0, 1000))
            .check(lst ->
                lst.equals(Assignment.from(lst))
            );
    }

    @Test
    public void testFromArray() {
        qt().forAll(
           arrays().ofIntegers(integers().all()).withLengthBetween(0, 1000))
           .check(array -> {
               int[] values =
                  Arrays.stream(array).mapToInt(Integer::intValue).toArray();
               return Arrays.asList(array).equals(Assignment.from(values));
           });
    }

    @Test
    public void testFromPartialAssignmentEqualsAsAssignmentWhenCompete() {
        new ForAnyPartialAssignment()
            .assuming(PartialAssignment::isComplete)
            .check(partialAssignment ->
                partialAssignment.asAssignment()
                    .equals(Assignment.from(partialAssignment))
            );
    }

    @Test
    public void testFromPartialAssignmentFailsWhenNotCompete() {
        new ForAnyPartialAssignment()
            .assuming(partialAssignment -> !partialAssignment.isComplete())
            .check(partialAssignment ->
                failsThrowing(
                        IllegalStateException.class,
                        () -> Assignment.from(partialAssignment))
            );
    }

    @Test
    public void testCollector() {
        qt().forAll(lists().of(integers().all()).ofSizeBetween(0, 1000))
            .check(lst ->
                lst.equals(lst.stream().collect(Assignment.collector()))
            );
    }

    private Gen<List<Integer>> listOfInt() {
        return lists().of(integers().all()).ofSizeBetween(0, 100);
    }

}
