package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.core.Assignment;
import be.uclouvain.solvercheck.core.Operator;
import be.uclouvain.solvercheck.generators.Generators;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import static be.uclouvain.solvercheck.checkers.Checkers.*;
import static be.uclouvain.solvercheck.core.Operator.*;
import static be.uclouvain.solvercheck.generators.Generators.operators;
import static be.uclouvain.solvercheck.generators.Generators.tables;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;

public class TestCheckers implements WithQuickTheories {

    @Test
    public void testAllDiff(){
        qt().forAll(assignments())
            .check(a -> allDiff().test(a) == (a.asSet().size() == a.size()));
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
            .assuming(a -> !isValidIndex(a.get(-2), a.size()-2))
            .check   (a -> !element().test(a));
    }

    @Test
    public void testElementChecksValueOfIthElement(){
        qt().withGenerateAttempts(10000)
            .forAll(assignmentWithAtLeast(3))
            .assuming(a -> isValidIndex(a.get(-2), a.size()-2))
            .check   (a -> element().test(a) == (a.get(a.get(-2)).equals(a.get(a.size()-1))));
    }

    @Test
    public void testTable() {
        qt().forAll(tables().build(), assignments())
            .check((t, a) -> table(t).test(a) == t.contains(a));
    }

    private Gen<Assignment> assignments() {
        return Generators.assignments().build();
    }
    private Gen<Assignment> assignmentWithAtLeast(int nVars) {
        return Generators.assignments().withVariablesRanging(3, 6).build();
    }
}
