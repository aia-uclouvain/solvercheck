package be.uclouvain.solvercheck.core;

import be.uclouvain.solvercheck.generators.Generators;
import com.google.common.collect.Iterables;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.Arrays;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.core.StrengthComparison.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;
import static be.uclouvain.solvercheck.utils.Utils.isValidRelaxIndex;

public class TestAssignment implements WithQuickTheories {
    @Test
    public void testSize() {
        qt().forAll(assignments()).check(a ->
                a.size() == a.asList().size()
        );
    }

    @Test
    public void getReturnsTheIthElementIffItsAValidIndex(){
        qt().withGenerateAttempts(10000)
            .forAll(assignments(), integers().between(0, 10))
            .assuming((a, i) -> isValidIndex(i, a.size()))
            .check   ((a, i) -> a.get(i).equals(a.asArray()[i]));
    }
    @Test
    public void getReturnsTheIthElementFromThEndIffItsAValidNegativeIndex(){
        qt().withGenerateAttempts(10000)
                .forAll(assignments(), integers().between(-10, 0))
                .assuming((a, i) -> i < 0 && isValidRelaxIndex(i, a.size()))
                .check   ((a, i) -> a.get(i).equals(a.asArray()[a.size()+i]));
    }

    @Test
    public void getFailsWithExceptionWhenItsNotAValidIndex() {
        qt().withGenerateAttempts(10000)
            .forAll(assignments(), integers().between(-10, 10))
            .assuming((a, i) -> !isValidRelaxIndex(i, a.size()))
            .check   ((a, i) -> failsThrowing(IndexOutOfBoundsException.class, () -> a.get(i) ));
    }

    @Test
    public void testAsList() {
        qt().forAll(assignments())
            .check(a -> a.asList().equals(a.stream().collect(Collectors.toList())));
    }
    @Test
    public void testAsSet() {
        qt().forAll(assignments())
                .check(a -> a.asSet().equals(a.stream().collect(Collectors.toSet())));
    }
    @Test
    public void testAsArray() {
        qt().forAll(assignments())
                .check(a -> Arrays.equals(a.asArray(), a.asList().toArray()));
    }
    @Test
    public void testEqualsAllValuesMatch() {
        qt().forAll(assignments(), assignments())
                .check ((a, b) -> a.equals(b) == (a.asList().equals(b.asList())));
    }

    @Test
    public void testHashCode() {
        qt().forAll(assignments(), assignments())
                .check ((a, b) -> a.equals(b) == (a.hashCode() == b.hashCode()));
    }

    private Gen<Assignment> assignments() {
        return Generators.assignments().build();
    }
}
