package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.core.data.impl.AssignmentFactory;
import be.uclouvain.solvercheck.generators.Generators;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.List;

import static be.uclouvain.solvercheck.utils.Utils.*;

public class TestAssignment implements WithQuickTheories {
    @Test
    public void testSize() {
        qt().forAll(listOfInt()).check(a ->
                a.size() == AssignmentFactory.from(a).size()
        );
    }

    @Test
    public void getReturnsTheIthElementIffItsAValidIndex(){
        qt().withGenerateAttempts(10000)
            .forAll(listOfInt(), integers().between(0, 10))
            .assuming((a, i) -> isValidIndex(i, a.size()))
            .check   ((a, i) -> a.get(i).equals(AssignmentFactory.from(a).get(i)));
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
            .check ((a, b) -> a.equals(b) == AssignmentFactory.from(a).equals(AssignmentFactory.from(b)));
    }

    @Test
    public void testHashCode() {
        qt().forAll(assignments(), assignments())
                .check ((a, b) -> a.equals(b) == (a.hashCode() == b.hashCode()));
    }

    private Gen<Assignment> assignments() {
        return Generators.assignments().build();
    }
    private Gen<List<Integer>> listOfInt() {
        return lists().of(integers().all()).ofSizeBetween(0, 100);
    }
}
