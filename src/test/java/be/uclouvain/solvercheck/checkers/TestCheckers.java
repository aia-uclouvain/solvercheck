package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.generators.Generators;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quicktheories.QuickTheory;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static be.uclouvain.solvercheck.core.data.Operator.EQ;
import static be.uclouvain.solvercheck.core.data.Operator.GE;
import static be.uclouvain.solvercheck.core.data.Operator.GT;
import static be.uclouvain.solvercheck.core.data.Operator.LE;
import static be.uclouvain.solvercheck.core.data.Operator.LT;
import static be.uclouvain.solvercheck.core.data.Operator.NE;
import static be.uclouvain.solvercheck.generators.Generators.tables;
import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;

public class TestCheckers implements WithQuickTheories, WithCheckers {

    private QuickTheory qt;

    @Before
    public void setUp() {
        qt = qt().withGenerateAttempts(10000);
    }

    @Test
    public void testAllDiff(){
        qt.forAll(assignments())
            .check(a -> allDiff().test(a) == (a.stream().distinct().count() == a.size()));
    }

    @Test
    public void testAlwaysFalse(){
        qt.forAll(assignments())
            .check(a -> !alwaysFalse().test(a));
    }
    @Test
    public void testAlwaysTrue(){
        qt.forAll(assignments())
            .check(a -> alwaysTrue().test(a));
    }

    @Test
    public void testElementIsFalseWhenGivenAnInfeasibleIndex(){
        qt.withGenerateAttempts(10000)
            .forAll(assignmentWithAtLeast(3))
            .assuming(a -> !isValidIndex(a.get(a.size()-2), a.size()-2))
            .check   (a -> !element().test(a));
    }

    @Test
    public void testElementChecksValueOfIthElement(){
        qt.withGenerateAttempts(10000)
            .forAll(assignmentWithAtLeast(3))
            .assuming(a -> isValidIndex(a.get(a.size()-2), a.size()-2))
            .check   (a -> element().test(a) == (a.get(a.get(a.size()-2)).equals(a.get(a.size()-1))));
    }

    @Test
    public void testGccIsTrueIffAllValuesOccurWithGivenCardinality() {
        qt.withExamples(100)
            .forAll(integers().between(0, 10).describedAs(s -> "SIZE("+s+")"))
            .checkAssert(S ->
                // FIXME: Questionnable ? Can it make sense to have
                //        multiple occurrences of the same value ?
                qt.withExamples(100)
                    .forAll(Generators.setsOfUpTo(S,integers().between(-10,10)).describedAs(s -> "VALUES("+s+")"))
                    .checkAssert(values ->
                        qt.withExamples(100)
                            .forAll(lists().of(integers().between(0, 10)).ofSize(values.size()).describedAs(s -> "CARDS("+s+")"))
                            .checkAssert(cards ->
                                qt.withExamples(100)
                                    .forAll(Generators.assignments().withValuesRanging(-10, 10).describedAs(a -> "ASSIGNMENT("+a+")"))
                                    .check(ass -> {

                     List<Integer> vals = new ArrayList<>(values);
                     boolean isGcc = gcc(cards, vals).test(ass);

                     boolean verif = true;
                     for(int i = 0; i < vals.size(); i++) {
                         final int idx = i;

                         int expectedCount = cards.get(i);
                         long counted = ass.stream()
                                        .filter(x -> x.equals(vals.get(idx)))
                                        .count();

                         verif &= (counted == expectedCount);
                     }

                     return isGcc == verif;

                            })
                    )
            ));
    }

    @Test
    public void testOtherValuesPlayNoRoleInGcc() {
        Assignment ass = Assignment.from(List.of(-1, -2, -3));

        Assert.assertTrue(gcc(List.of(0,0,0), List.of(1, 2, 3)).test(ass));
    }

    @Test
    public void testGccVarIsTrueIffAllValuesOccurWithGivenCardinality() {
      qt.forAll(lists().of(integers().between(-10, 10)).ofSizeBetween(0, 10))
          // FIXME: Questionnable ? Can it make sense to have
          //        multiple occurrences of the same value ?
          .assuming(vals -> vals.size() == new HashSet<>(vals).size())
          .checkAssert(values -> {
              int nbVarsMin = values.size();
              qt.forAll(
                      Generators.assignments()
                                .withVariablesBetween(nbVarsMin,3*(1+nbVarsMin))
                                .withValuesRanging(0, 10)
                    )
                  .check(ass -> {
                      boolean isGcc = gccVar(values).test(ass);

                      List<Integer> vars =
                              ass.subList(0, ass.size()-values.size());

                      List<Integer> cards =
                              ass.subList(ass.size()-values.size(), ass.size());

                      int sumOfCards=
                              cards.stream().mapToInt(Integer::intValue).sum();

                      boolean verif = vars.size() >= sumOfCards;

                      for(int i = 0; i < values.size(); i++) {
                          final int idx = i;

                          int expectedCount = cards.get(i);
                          long counted = vars.stream()
                                  .filter(x -> x.equals(values.get(idx)))
                                  .count();

                          verif &= (counted == expectedCount);
                      }

                      return isGcc == verif;
                  });
          });
    }

    @Test
    public void gccShouldFailWheneverTheValuesCannotDirectlyBeMappedOntoASet() {
        qt.forAll(integers().between(2, 100).describedAs(s -> "SIZE("+s+")"))
            .checkAssert(S ->
              qt.withGenerateAttempts(10000)
                  .forAll(
                      lists()
                          .of(integers().between(0, 10))
                          .ofSize(S)
                          .describedAs(v ->"VALS("+v+")"),
                      lists()
                          .of(integers().between(0, 10))
                          .ofSize(S)
                          .describedAs(c ->"CARDS("+c+")"))
                  .assuming((vals, cards) -> vals.size() != new HashSet<>(vals).size())
                  .check((vals, cards) ->
                    failsThrowing(
                        IllegalArgumentException.class,
                        () -> gcc(cards, vals)))
            );
    }

    @Test
    public void gccVarShouldFailWheneverTheValuesCannotDirectlyBeMappedOntoASet() {
        qt.forAll(
            lists()
                .of(integers().between(0, 100))
                .ofSizeBetween(2, 100)
                .describedAs(v ->"VALS("+v+")"))
            .assuming(vals -> vals.size() != new HashSet<>(vals).size())
            .check(vals ->
                failsThrowing(
                    IllegalArgumentException.class,
                    () -> gccVar(vals)));
    }

    @Test
    public void gccShouldFailWhenValuesAndCardinalitieDontHaveTheSameSize() {
        qt.withGenerateAttempts(10000)
            .forAll(
                lists()
                    .of(integers().between(0, 10))
                    .ofSizeBetween(0, 100)
                    .describedAs(v ->"VALS("+v+")"),
                lists()
                    .of(integers().between(0, 10))
                    .ofSizeBetween(0, 100)
                    .describedAs(c ->"CARDS("+c+")"))
            .assuming((vals, cards) -> vals.size() != cards.size())
            .check((vals, cards) ->
                    failsThrowing(
                            IllegalArgumentException.class,
                            () -> gcc(cards, vals)));
    }

    @Test
    public void gccVardShouldFailWhenCardinalitiesCantCoverValues() {
        qt.withGenerateAttempts(10000)
                .forAll(
                        lists()
                                .of(integers().between(0, 10))
                                .ofSizeBetween(0, 100)
                                .describedAs(v ->"VALS("+v+")"),
                        Generators.assignments()
                                .withUpToVariables(100)
                                .describedAs(a -> "ASSIGNMENT("+a+")"))
                .assuming((vals, ass) -> vals.size() > ass.size())
                .check((vals, ass) ->
                        failsThrowing(
                                IllegalArgumentException.class,
                                () -> gccVar(vals).test(ass)));
    }

    @Test
    public void testSumEQ(){
        qt.forAll(assignments(), integers().all())
            .check((a, c) -> sum(EQ, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() == c) );
    }
    @Test
    public void testSumNE(){
        qt.forAll(assignments(), integers().all())
                .check((a, c) -> sum(NE, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() != c) );
    }
    @Test
    public void testSumLT(){
        qt.forAll(assignments(), integers().all())
                .check((a, c) -> sum(LT, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() < c) );
    }
    @Test
    public void testSumLE(){
        qt.forAll(assignments(), integers().all())
                .check((a, c) -> sum(LE, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() <= c) );
    }
    @Test
    public void testSumGT(){
        qt.forAll(assignments(), integers().all())
                .check((a, c) -> sum(GT, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() > c) );
    }
    @Test
    public void testSumGE(){
        qt.forAll(assignments(), integers().all())
                .check((a, c) -> sum(GE, c).test(a) == (a.stream().mapToInt(Integer::intValue).sum() >= c) );
    }

    @Test
    public void testTable() {
        qt.forAll(tables().build(), assignments())
            .check((t, a) -> table(t).test(a) == t.contains(a));
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
