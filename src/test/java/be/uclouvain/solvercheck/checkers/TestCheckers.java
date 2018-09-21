package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.consistencies.ArcConsitency;
import be.uclouvain.solvercheck.consistencies.BoundDConsistency;
import be.uclouvain.solvercheck.consistencies.BoundZConsistency;
import be.uclouvain.solvercheck.consistencies.RangeConsistency;
import be.uclouvain.solvercheck.consistencies.HybridConsistency;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.generators.Generators;
import be.uclouvain.solvercheck.utils.collections.Range;
import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.List;

import static be.uclouvain.solvercheck.checkers.Checkers.allDiff;
import static be.uclouvain.solvercheck.checkers.Checkers.sum;
import static be.uclouvain.solvercheck.checkers.Checkers.element;
import static be.uclouvain.solvercheck.checkers.Checkers.gccVar;
import static be.uclouvain.solvercheck.checkers.Checkers.table;

import static be.uclouvain.solvercheck.core.data.Operator.GT;
import static be.uclouvain.solvercheck.core.data.Operator.GE;
import static be.uclouvain.solvercheck.core.data.Operator.EQ;
import static be.uclouvain.solvercheck.core.data.Operator.NE;
import static be.uclouvain.solvercheck.core.data.Operator.LE;
import static be.uclouvain.solvercheck.core.data.Operator.LT;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.WEAKER;

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
        PartialAssignment initial = PartialAssignment.from(
            List.of(
                // variables
                Domain.from(1, 2, 3),
                Domain.from(1, 2, 3),
                Domain.from(1, 2, 3),
                // Cardinalities
                Domain.from(0, 3),
                Domain.from(0, 3)
            )
        );

        Filter gccAc  = new ArcConsitency(    gccVar(List.of(1, 3)));
        PartialAssignment actualAC = gccAc .filter(initial);
        PartialAssignment expected = PartialAssignment.unionOf(
                5,
                List.of(
                    List.of(1, 1, 1, 3, 0),
                    List.of(3, 3, 3, 0, 3),
                    List.of(2, 2, 2, 0, 0)
                )
        );

        Assert.assertEquals(expected, actualAC);
    }

    @Test
    public void foireux(){
        Domain.from(Range.between(4, 10));
    }

    @Test
    public void dbgSum() {
        PartialAssignment initial = PartialAssignment.from(
                List.of(
                        // variables
                        Domain.from(List.of(1, 2, 4)),
                        Domain.from(List.of(1, 4))
                ));

        Filter sumAc  = new ArcConsitency(sum(EQ, 5));
        Filter sumBcD = new BoundDConsistency(sum(EQ, 5));
        Filter sumBcZ = new BoundZConsistency(sum(EQ, 5));
        Filter sumRng = new RangeConsistency(sum(EQ, 5));

        Filter sumHyb = new HybridConsistency(
                sum(EQ, 5),
                ArcConsitency::domainFilter,
                ArcConsitency::domainFilter);

        PartialAssignment actualAc  = sumAc.filter(initial);
        PartialAssignment actualBcD = sumBcD.filter(initial);
        PartialAssignment actualBcZ = sumBcZ.filter(initial);
        PartialAssignment actualRng = sumRng.filter(initial);
        PartialAssignment actualHyb = sumHyb.filter(initial);


        Assert.assertEquals(WEAKER, actualBcD.compareWith(actualAc));
        Assert.assertEquals(WEAKER, actualBcZ.compareWith(actualAc));
        Assert.assertEquals(WEAKER, actualRng.compareWith(actualAc));

        Assert.assertEquals(EQUIVALENT, actualHyb.compareWith(actualAc));
    }

    private Gen<Assignment> assignments() {
        return Generators.assignments().build();
    }
    private Gen<Assignment> assignmentWithAtLeast(int nVars) {
        return Generators.assignments()
                .withVariablesBetween(nVars, nVars+3)
                .withValuesRanging(-10, 10)
                .build();
    }
}
