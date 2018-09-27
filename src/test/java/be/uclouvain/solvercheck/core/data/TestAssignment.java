package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.generators.WithCpGenerators;
import org.junit.Test;
import org.quicktheories.QuickTheory;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.List;
import java.util.function.Predicate;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static org.junit.Assert.assertEquals;

public class TestAssignment
        implements WithQuickTheories, WithCpGenerators {

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
    public void testFromPartialAssignmentEqualsAsAssignmentWhenCompete() {
        forAnyPartialAssignment(
            partialAssignment -> partialAssignment.isComplete(),
            partialAssignment ->
                    partialAssignment.asAssignment()
                            .equals(Assignment.from(partialAssignment))
        );
    }

    @Test
    public void testFromPartialAssignmentFailsWhenNotCompete() {
        forAnyPartialAssignment(
                partialAssignment -> !partialAssignment.isComplete(),
                partialAssignment ->
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
