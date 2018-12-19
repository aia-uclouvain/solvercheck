package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.WithSolverCheck;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;

public class TestDefaultGeneratorsDSL implements WithSolverCheck {

    // ----- OPERATORS --------------------------------------------------------
    @Test
    public void testOperators() {
      assertThat(forAll(operator()).itIsTrueThat(Objects::nonNull));
    }

    // ----- DOMAINS ----------------------------------------------------------
    @Test
    public void testListOfSizeBetween() {
      assertThat(forAll(listOf(integer()).ofSizeBetween(0, 0)).itIsTrueThat(List::isEmpty));
    }

    @Test
    public void defaultDomain() {
        assertThat(forAll(domain()).itIsTrueThat(Objects::nonNull));
    }

    @Test
    public void domainsOfSizeUpTo() {
        assertThat  (     forAll(integer().between(0, 10))
       .assertThat  (i -> forAll(domain().ofSizeUpTo(i).allowingErrors())
       .itIsTrueThat(d -> d.size() <= i)));
    }
    @Test
    public void domainsWithValuesBetween() {
        assertThat(
           forAll(
              integer("x").between(0, 10),
              integer("y").between(0, 10))
       .assuming  ((x, y) -> x <= y)
       .assertThat((x, y) ->
           forAll(domain().withValuesBetween(x, y))
       .itIsTrueThat(d ->
          d.stream().allMatch(v -> x <= v && v <= y))
        ));
    }
    @Test
    public void domainsWithValuesBetweenBoundsInWrongOrder() {
        assertThat(
          forAll(integer("x").between(0, Integer.MAX_VALUE), integer("y").between(0, Integer.MAX_VALUE))
          .assuming    ((x, y) -> x > y)
          .assertThat  ((x, y) -> randomness ->
             failsThrowing(
                IllegalArgumentException.class,
                () -> domain().withValuesBetween(x, y).build().generate(randomness))
          )
        );
    }


    // ----- PARTIAL ASSIGNMENTS ----------------------------------------------
    @Test
    public void defaultPartialAssignment() {
        assertThat(
           forAll(partialAssignment()).itIsTrueThat(Objects::nonNull)
        );
    }

    @Test
    public void partialAssignmentWithNVariables() {
        assertThat(
           forAll(integer().between(0, 10))
           .assertThat(i ->
              forAll(partialAssignment().withVariables(i))
              .itIsTrueThat(pa -> pa.size() == i)
           ));
    }

    @Test
    public void partialAssignmentWithUpToNVariables() {
        assertThat  (      forAll(integer().between(0, 10))
       .assertThat  (i  -> forAll(partialAssignment().withUpToVariables(i))
       .itIsTrueThat(pa -> pa.size() <= i)));
    }

    @Test
    public void partialAssignmentVariablesBetween() {
        assertThat(forAll(
           integer().between(0, 10),
           integer().between(0, 10))
       .assuming  ((from, to) -> from <= to)
       .assertThat((from, to) -> forAll(partialAssignment().withVariablesBetween(from, to))
       .itIsTrueThat(pa -> from <= pa.size() && pa.size() <= to)
       ));
    }

    @Test
    public void partialAssignmentWithDomainsOfSizeUpTo() {
        assertThat  (      forAll(integer().between(0, 10))
       .assertThat  (i  -> forAll(partialAssignment().withDomainsOfSizeUpTo(i))
       .itIsTrueThat(pa -> pa.stream().allMatch(d-> d.size() <= i))));
    }

    @Test
    public void partialAssignmentWithValuesRanging() {
        assertThat  (forAll(integer().between(0, Integer.MAX_VALUE), integer().between(0, Integer.MAX_VALUE))
       .assuming    ((from, to) -> from <= to)
       .assertThat  ((from, to) -> forAll(partialAssignment().withValuesRanging(from, to))
       .assuming    (pa         -> !pa.isError())
       .itIsTrueThat(pa         ->
             pa.stream().allMatch(d-> d.minimum() >= from)
          && pa.stream().allMatch(d -> d.maximum() <= to)
       )));
    }

    // ----- ASSIGNMENTS ------------------------------------------------------
    @Test
    public void defaultAssignment() {
        assertThat(forAll(assignment()).itIsTrueThat(Objects::nonNull));
    }
    @Test
    public void assignmentWithNVariables() {
        assertThat  (      forAll(integer().between(0, 10))
       .assertThat  (i  -> forAll(assignment().withVariables(i))
       .itIsTrueThat(pa -> pa.size() == i)));
    }
    @Test
    public void assignmentWithUpToVariables() {
        assertThat  (      forAll(integer().between(0, 10))
       .assertThat  (i  -> forAll(assignment().withUpToVariables(i))
       .itIsTrueThat(pa -> pa.size() <= i)));
    }
    @Test
    public void assignmentVariablesBetween() {
        assertThat(forAll(integer().between(0, 10), integer().between(0, 10))
       .assuming((from, to) -> from <= to)
       .assertThat((from, to) ->
          forAll(assignment().withVariablesBetween(from, to))
       .itIsTrueThat(ass -> from <= ass.size() && ass.size() <= to)));
    }
    @Test
    public void assignmentWithValuesRanging() {
        assertThat(forAll(integer().between(0, Integer.MAX_VALUE), integer().between(0, Integer.MAX_VALUE))
       .assuming((from, to) -> from <= to)
       .assertThat((from, to) ->
          forAll(assignment().withValuesRanging(from, to))
       .itIsTrueThat(ass -> ass.stream().allMatch(v -> from <= v && v <= to))));
    }
}