package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.WithSolverCheck;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;

public class TestGeneratorsDSL implements WithSolverCheck {

    // ----- LISTS ------------------------------------------------------------
    @Test
    public void testLists() {
        assertThat  (     forAll(integer().between(0, 10))
       .assertThat  (i -> forAll(GeneratorsDSL.listOf(integer()).ofSize(i))
       .itIsTrueThat(x -> x.size() == i )));
    }

    @Test
    public void listsOfFromTo() {
        assertThat(forAll(
           integer("from").between(0, 10),
           integer("to"  ).between(0, 10))
        .assuming  ((from, to) -> from <= to)
        .assertThat((from, to) ->
           forAll(GeneratorsDSL.listOf(integer()).ofSizeBetween(from, to))
        .itIsTrueThat(x ->
           from <= x.size() && x.size() <= to)));
    }
    @Test
    public void listsOfFromToWhenFromIsBiggerThanTo() {
        assertThat(forAll(
           integer("from").between(0, 10),
           integer("to"  ).between(0, 10))
        .assuming  ((from, to) -> from > to)
        .assertThat((from, to) -> randomness ->
           failsThrowing(
              IllegalArgumentException.class,
              () -> GeneratorsDSL.listOf(integer()).ofSizeBetween(from, to).build().generate(randomness))
        ));
    }

    @Test
    public void listsOfUpTo() {
        assertThat  (     forAll(integer().between(0, 10))
       .assertThat  (i -> forAll(GeneratorsDSL.listOf(integer()).ofSizeUpTo(i))
       .itIsTrueThat(x -> x.size() <= i)));
    }

    // ----- SETS -------------------------------------------------------------
    @Test
    public void setsOfSizeUpTo() {
        assertThat  (     forAll(integer().between(0, 10))
       .assertThat  (i -> forAll(GeneratorsDSL.setOf(integer()).ofSizeUpTo(i).possiblyEmpty())
       .itIsTrueThat(x -> x.size() <= i)));
    }

    // ----- OPERATORS --------------------------------------------------------
    @Test
    public void tetsOperators() {
        assertThat(forAll(GeneratorsDSL.operator()).itIsTrueThat(Objects::nonNull));
    }

    // ----- DOMAINS ----------------------------------------------------------
    @Test
    public void testListOfSizeBetween() {
        assertThat(forAll(listOf(integer()).ofSizeBetween(0, 0)).itIsTrueThat(List::isEmpty));
    }

    @Test
    public void defaultDomain() {
        assertThat(forAll(GeneratorsDSL.domain()).itIsTrueThat(Objects::nonNull));
    }

    @Test
    public void domainsOfSizeUpTo() {
        assertThat  (     forAll(integer().between(0, 10))
       .assertThat  (i -> forAll(GeneratorsDSL.domain().ofSizeUpTo(i).allowingErrors())
       .itIsTrueThat(x -> x.size() <= i)));
    }
    @Test
    public void domainsWithValuesBetween() {
        assertThat(forAll(
           integer("x").between(0, Integer.MAX_VALUE),
           integer("y").between(0, Integer.MAX_VALUE))
        .assuming  ((x, y) -> x <= y)
        .assertThat((x, y) ->
           forAll(GeneratorsDSL.domain().withValuesBetween(x, y))
        .itIsTrueThat(d -> d.stream().allMatch(v -> x <= v && v <= y))));
    }
    @Test
    public void domainsWithValuesBetweenBoundsInWrongOrder() {
        assertThat(forAll(
           integer("x").between(0, Integer.MAX_VALUE),
           integer("y").between(0, Integer.MAX_VALUE))
       .assuming  ((x, y) -> x > y)
       .assertThat((x, y) -> randomness ->
          failsThrowing(
             IllegalArgumentException.class,
             () -> GeneratorsDSL.domain().withValuesBetween(x, y).build().generate(randomness)
          )
       ));
    }

    // ----- PARTIAL ASSIGNMENTS ----------------------------------------------
    @Test
    public void defaultPartialAssingment() {
        assertThat(forAll(GeneratorsDSL.monolithicPartialAssignment()).itIsTrueThat(Objects::nonNull));
    }

    @Test
    public void partialAssignmentWithNVariables() {
        assertThat  (      forAll(integer().between(0, 10))
       .assertThat  (i  -> forAll(GeneratorsDSL.monolithicPartialAssignment().withVariables(i))
       .itIsTrueThat(pa -> pa.size() == i)));
    }

    @Test
    public void partialAssignmentWithUpToNVariables() {
        assertThat  (     forAll(integer().between(0, 10))
       .assertThat  (i -> forAll(GeneratorsDSL.monolithicPartialAssignment().withUpToVariables(i))
       .itIsTrueThat(pa -> pa.size() <= i)));
    }

    @Test
    public void partialAssignmentVariablesBetween() {
        assertThat(forAll(
           integer("from").between(0, 10),
           integer("to"  ).between(0, 10))
      .assuming  ((from, to) -> from <= to)
      .assertThat((from, to) ->
         forAll(GeneratorsDSL.monolithicPartialAssignment().withVariablesBetween(from, to))
      .itIsTrueThat(pa -> from <= pa.size() && pa.size() <= to)));
    }

    @Test
    public void partialAssignmentWithDomainsOfSizeUpTo() {
        assertThat(forAll(integer().between(0, 10))
       .assertThat(i ->
          forAll(GeneratorsDSL.monolithicPartialAssignment().withDomainsOfSizeUpTo(i))
       .itIsTrueThat(pa -> pa.stream().allMatch(d-> d.size() <= i))));
    }

    @Test
    public void partialAssignmentWithValuesRanging() {
        assertThat(forAll(
           integer("from").between(0, Integer.MAX_VALUE),
           integer("to"  ).between(0, Integer.MAX_VALUE))
       .assuming  ((from, to) -> from <= to)
       .assertThat((from, to) ->
          forAll(GeneratorsDSL.monolithicPartialAssignment().withValuesRanging(from, to))
       .assuming    (pa -> !pa.isEmpty() && !pa.isError())
       .itIsTrueThat(pa ->
             pa.stream().allMatch(d -> d.minimum() >= from)
          && pa.stream().allMatch(d -> d.maximum() <= to)
        )));
    }

    // ----- ASSIGNMENTS ------------------------------------------------------
    @Test
    public void defaultAssignment() {
        assertThat(forAll(GeneratorsDSL.assignment()).itIsTrueThat(Objects::nonNull));
    }
    @Test
    public void assignmentWithNVariables() {
        assertThat  (      forAll(integer().between(0, 10))
       .assertThat  (i  -> forAll(GeneratorsDSL.assignment().withVariables(i))
       .itIsTrueThat(pa -> pa.size() == i)));
    }
    @Test
    public void assignmentWithUpToVariables() {
        assertThat  (      forAll(integer().between(0, 10))
       .assertThat  (i  -> forAll(GeneratorsDSL.assignment().withUpToVariables(i))
       .itIsTrueThat(pa -> pa.size() <= i)));
    }
    @Test
    public void assignmentVariablesBetween() {
        assertThat(forAll(
           integer("from").between(0, 10),
           integer("to"  ).between(0, 10))
       .assuming  ((from, to) -> from <= to)
       .assertThat((from, to) ->
          forAll(GeneratorsDSL.assignment().withVariablesBetween(from, to))
       .itIsTrueThat(ass -> from <= ass.size() && ass.size() <= to)));
    }
    @Test
    public void assignmentWithValuesRanging() {
        assertThat(forAll(
           integer("from").between(0, Integer.MAX_VALUE),
           integer("to"  ).between(0, Integer.MAX_VALUE))
       .assuming  ((from, to) -> from <= to)
       .assertThat((from, to) ->
            forAll(GeneratorsDSL.assignment().withValuesRanging(from, to))
       .itIsTrueThat(ass -> ass.stream().allMatch(v -> from <= v && v <= to))));
    }
}