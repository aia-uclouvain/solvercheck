package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.Assignment;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static be.uclouvain.solvercheck.core.data.Operator.EQ;
import static be.uclouvain.solvercheck.core.data.Operator.GE;
import static be.uclouvain.solvercheck.core.data.Operator.GT;
import static be.uclouvain.solvercheck.core.data.Operator.LE;
import static be.uclouvain.solvercheck.core.data.Operator.LT;
import static be.uclouvain.solvercheck.core.data.Operator.NE;
import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;
import static org.junit.Assert.assertEquals;


public class TestCheckers implements WithSolverCheck {

    @Test
    public void testAllDiff(){
        assertThat(
          forAll(assignment()).itIsTrueThat(a ->
             allDiff().test(a) == (a.stream().distinct().count() == a.size())
          )
        );
    }

    @Test
    public void testAlwaysFalse(){
        assertThat(
           forAll(assignment()).itIsFalseThat(a ->
              alwaysFalse().test(a)
           )
        );
    }

    @Test
    public void testAlwaysTrue(){
        assertThat(
           forAll(assignment()).itIsTrueThat(a ->
              alwaysTrue().test(a)
           )
        );
    }

    @Test
    public void testElementIsFalseWhenGivenAnInfeasibleIndex(){
        assertThat(
           forAll(assignment().withVariablesBetween(3, 5))
           .assuming(a -> !isValidIndex(a.get(a.size()-2), a.size()-2))
           .itIsFalseThat(a -> element().test(a))
        );
    }

    @Test
    public void testElementChecksValueOfIthElement(){
        assertThat(
           forAll(assignment().withVariablesBetween(3, 5))
            .assuming(a -> isValidIndex(a.get(a.size()-2), a.size()-2))
            .itIsTrueThat(a ->
               element().test(a) == a.get(a.get(a.size()-2)).equals(a.get(a.size()-1))
            )
        );
    }


    @Test
    public void testGccIsTrueIffAllValuesOccurWithGivenCardinality() {
        assertThat(
            forAll(setOf("VALUES", integer().between(-10,10)).possiblyEmpty()).assertThat(values ->
            forAll(
               listOf("CARDDINALITIES", integer().between(0, 10)).ofSize(values.size()),
               assignment("ASSIGNMENT").withValuesRanging(-10, 10))
           .itIsTrueThat((cards, asn) -> {
               List<Integer> vals = new ArrayList<>(values);
               boolean isGcc = gcc(cards, vals).test(asn);

               boolean verif = true;
               for(int i = 0; i < vals.size(); i++) {
                   final int idx = i;

                   int expectedCount = cards.get(i);
                   long counted = asn.stream()
                      .filter(x -> x.equals(vals.get(idx)))
                      .count();

                   verif &= (counted == expectedCount);
               }

               return verif == isGcc;
           }))
        );
    }

    @Test
    public void testOtherValuesPlayNoRoleInGcc() {
        Assignment ass = Assignment.from(List.of(-1, -2, -3));

        Assert.assertTrue(gcc(List.of(0,0,0), List.of(1, 2, 3)).test(ass));
    }

    @Test
    public void testGccVarIsTrueIffAllValuesOccurWithGivenCardinality() {
      assertThat(
         forAll(listOf("VALUES", integer().between(-10, 10)).ofSizeBetween(0, 10))
        .assuming(vals -> vals.size() == new HashSet<>(vals).size())
        .assertThat(values -> rnd -> {
            int nbVarsMin = values.size();

            assertThat(
                forAll(assignment()
                   .withVariablesBetween(nbVarsMin,3*(1+nbVarsMin))
                   .withValuesRanging(0, 10)
                ).assertThat(ass -> randomness -> {
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

                    assertEquals(verif, isGcc);
                })
            );
        }));
    }

    @Test
    public void gccShouldFailWheneverTheValuesCannotDirectlyBeMappedOntoASet() {
        assertThat(
           forAll(integer("SIZE").between(2, 100))
          .assertThat(S ->
           forAll(
              listOf("VALUES", integer().between(0, 10)).ofSize(S),
              listOf("CARDS ", integer().between(0, 10)).ofSize(S)
           )
          .assuming((vals, cards) -> vals.size() != new HashSet<>(vals).size())
          .itIsTrueThat((vals, cards) ->
            failsThrowing(
               IllegalArgumentException.class,
               () -> gcc(cards, vals)
            )
          ))
        );
    }

    @Test
    public void gccVarShouldFailWheneverTheValuesCannotDirectlyBeMappedOntoASet() {
        assertThat(
           forAll(listOf("VALUES", integer().between(0, 100)).ofSizeBetween(1, 100))
          .assuming(vals -> vals.size() != new HashSet<>(vals).size())
          .itIsTrueThat(vals ->
             failsThrowing(
                IllegalArgumentException.class,
                () -> gccVar(vals))
          )
        );
    }

    @Test
    public void gccShouldFailWhenValuesAndCardinalitieDontHaveTheSameSize() {
        assertThat(
           forAll(
             listOf("VAL ", integer().between(0, 10)).ofSizeBetween(0, 100),
             listOf("CARD", integer().between(0, 10)).ofSizeBetween(0, 100)
           )
          .assuming((vals, cards) -> vals.size() != cards.size())
          .itIsTrueThat((vals, cards) ->
              failsThrowing(
                 IllegalArgumentException.class,
                 () -> gcc(cards, vals))
          )
        );
    }

    @Test
    public void gccVardShouldFailWhenCardinalitiesCantCoverValues() {
        assertThat(
           forAll(
              listOf("VALS", integer().between(0, 10)).ofSizeBetween(0, 100),
              assignment("ASN").withUpToVariables(100)
           )
          .assuming((vals, ass) -> vals.size() > ass.size())
          .itIsTrueThat((vals, ass) ->
             failsThrowing(
                IllegalArgumentException.class,
                () -> gccVar(vals).test(ass))
          )
        );
    }

    @Test
    public void testSumEQ(){
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
          .assertThat((a, c) -> randomness ->
             assertEquals(
               a.stream().mapToLong(Integer::longValue).sum() == c,
                sum(EQ, c).test(a)
             )
          )
        );
    }
    @Test
    public void testSumNE(){
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assertThat((a, c) -> randomness ->
                 assertEquals(
                    a.stream().mapToLong(Integer::longValue).sum() != c,
                    sum(NE, c).test(a)
                 )
              )
        );
    }
    @Test
    public void testSumLT(){
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assertThat((a, c) -> randomness ->
                 assertEquals(
                    a.stream().mapToLong(Integer::longValue).sum() < c,
                    sum(LT, c).test(a)
                 )
              )
        );
    }
    @Test
    public void testSumLE(){
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assertThat((a, c) -> randomness ->
                 assertEquals(
                    a.stream().mapToLong(Integer::longValue).sum() <= c,
                    sum(LE, c).test(a)
                 )
              )
        );
    }
    @Test
    public void testSumGT(){
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assertThat((a, c) -> randomness ->
                 assertEquals(
                    a.stream().mapToLong(Integer::longValue).sum() > c,
                    sum(GT, c).test(a)
                 )
              )
        );
    }
    @Test
    public void testSumGE(){
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assertThat((a, c) -> randomness ->
                 assertEquals(
                    a.stream().mapToLong(Integer::longValue).sum() >= c,
                    sum(GE, c).test(a)
                 )
              )
        );
    }

    @Test
    public void testSumLeIsNotSubjectToOverflows() {
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assuming((assignment, rhs) -> underflowOrOverflow(assignment))
              .assertThat((assignment, rhs) -> randomness -> {
                  long sumL =
                     assignment.stream().mapToLong(Integer::longValue).sum();

                  assertEquals(
                     (sumL <= (long) rhs),
                     sum(LE, rhs).test(assignment));
              })
        );
    }
    @Test
    public void testSumLtIsNotSubjectToOverflows() {
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assuming((assignment, rhs) -> underflowOrOverflow(assignment))
              .assertThat((assignment, rhs) -> randomness -> {
                  long sumL =
                     assignment.stream().mapToLong(Integer::longValue).sum();

                  assertEquals(
                     (sumL < (long) rhs),
                     sum(LT, rhs).test(assignment));
              })
        );
    }
    @Test
    public void testSumEqIsNotSubjectToOverflows() {
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assuming((assignment, rhs) -> underflowOrOverflow(assignment))
              .assertThat((assignment, rhs) -> randomness -> {
                  long sumL =
                     assignment.stream().mapToLong(Integer::longValue).sum();

                  assertEquals(
                     (sumL == (long) rhs),
                     sum(EQ, rhs).test(assignment));
              })
        );
    }
    @Test
    public void testSumNeIsNotSubjectToOverflows() {
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assuming((assignment, rhs) -> underflowOrOverflow(assignment))
              .assertThat((assignment, rhs) -> randomness -> {
                  long sumL =
                     assignment.stream().mapToLong(Integer::longValue).sum();

                  assertEquals(
                     (sumL != (long) rhs),
                     sum(NE, rhs).test(assignment));
              })
        );
    }
    @Test
    public void testSumGeIsNotSubjectToOverflows() {
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assuming((assignment, rhs) -> underflowOrOverflow(assignment))
              .assertThat((assignment, rhs) -> randomness -> {
                  long sumL =
                     assignment.stream().mapToLong(Integer::longValue).sum();

                  assertEquals(
                     (sumL >= (long) rhs),
                     sum(GE, rhs).test(assignment));
              })
        );
    }
    @Test
    public void testSumGtIsNotSubjectToOverflows() {
        assertThat(
           forAll(assignment("ASN"), integer("RHS"))
              .assuming((assignment, rhs) -> underflowOrOverflow(assignment))
              .assertThat((assignment, rhs) -> randomness -> {
                  long sumL =
                     assignment.stream().mapToLong(Integer::longValue).sum();

                  assertEquals(
                     (sumL > (long) rhs),
                     sum(GT, rhs).test(assignment));
              })
        );
    }

    //
    // This assumption ensures that either an overflow or an underflow
    // underflow happens while summing up the values.
    //
    // @param pa the assignment on which the assuption bears.
    // @return true iff no overflow can happen during the summation.
    //
    private boolean underflowOrOverflow(final Assignment pa) {
        long sumL = pa.stream().mapToLong(Integer::longValue).sum();
        int  sumI = pa.stream().mapToInt(Integer::intValue).sum();

        return sumL != sumI;
    }

}