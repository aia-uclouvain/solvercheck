package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.data.impl.AssignmentFactory;
import be.uclouvain.solvercheck.core.data.impl.DomainFactory;
import be.uclouvain.solvercheck.core.data.impl.PartialAssignmentFactory;
import be.uclouvain.solvercheck.generators.Generators;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.checkers.Checkers.*;
import static be.uclouvain.solvercheck.core.data.Operator.*;
import static be.uclouvain.solvercheck.generators.Generators.tables;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;

public class TestCheckers implements WithQuickTheories {

    @Test
    public void testAllDiff(){
        qt().forAll(assignments())
            .check(a -> allDiff().test(a) == (a.stream().distinct().count() == a.size()));
    }

    @Test
    public void testSumEQ(){
        qt().forAll(assignments(), integers().all())
            .check((a, c) -> sum(EQ, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() == c) );
    }
    @Test
    public void testSumNE(){
        qt().forAll(assignments(), integers().all())
                .check((a, c) -> sum(NE, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() != c) );
    }
    @Test
    public void testSumLT(){
        qt().forAll(assignments(), integers().all())
                .check((a, c) -> sum(LT, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() < c) );
    }
    @Test
    public void testSumLE(){
        qt().forAll(assignments(), integers().all())
                .check((a, c) -> sum(LE, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() <= c) );
    }
    @Test
    public void testSumGT(){
        qt().forAll(assignments(), integers().all())
                .check((a, c) -> sum(GT, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() > c) );
    }
    @Test
    public void testSumGE(){
        qt().forAll(assignments(), integers().all())
                .check((a, c) -> sum(GE, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() >= c) );
    }

    @Test
    public void testElementIsFalseWhenGivenAnInfeasibleIndex(){
        qt().withGenerateAttempts(10000)
            .forAll(assignmentWithAtLeast(3))
            .assuming(a -> !isValidIndex(a.get(a.size()-2), a.size()-2))
            .check   (a -> !element().test(a));
    }

    @Test
    public void testElementChecksValueOfIthElement(){
        qt().withGenerateAttempts(10000)
            .forAll(assignmentWithAtLeast(3))
            .assuming(a -> isValidIndex(a.get(a.size()-2), a.size()-2))
            .check   (a -> element().test(a) == (a.get(a.get(a.size()-2)).equals(a.get(a.size()-1))));
    }

    @Test
    public void testTable() {
        qt().forAll(tables().build(), assignments())
            .check((t, a) -> table(t).test(a) == t.contains(a));
    }

    @Test
    public void dbgGccVar() {
        PartialAssignment initial = PartialAssignmentFactory.from(
            List.of(
                // variables
                DomainFactory.from(1, 2),
                DomainFactory.from(1, 2),
                DomainFactory.from(1, 2),
                // Cardinalities
                DomainFactory.from(0, 3),
                DomainFactory.from(0, 3)
            )
        );

        PartialAssignment actual = PartialAssignmentFactory.unionOf(
                CartesianProduct.of(initial).stream()
                        .map(AssignmentFactory::from)
                        .filter(gccVar(List.of(1, 2)))
                        .collect(Collectors.toList())
        );

        PartialAssignment expected = PartialAssignmentFactory.unionOf(
                List.of(
                    List.of(1, 1, 1, 3, 0),
                    List.of(2, 2, 2, 0, 3)
                )
        );

        Assert.assertEquals(actual, expected);
    }

    private Gen<Assignment> assignments() {
        return Generators.assignments().build();
    }
    private Gen<Assignment> assignmentWithAtLeast(int nVars) {
        return Generators.assignments().withVariablesRanging(3, 6).build();
    }
}
