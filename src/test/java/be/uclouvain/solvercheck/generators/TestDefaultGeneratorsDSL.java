// FIXME
/*
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
      assertThat(forAll(operators()).itIsTrueThat(Objects::nonNull));
    }

    // ----- DOMAINS ----------------------------------------------------------
    @Test
    public void testListOfSizeBetween() {
      assertThat(forAll(lists().ofSizeBetween(0, 0)).itIsTrueThat(List::isEmpty));
    }

    @Test
    public void defaultDomain() {
        assertThat(forAll(domains()).itIsTrueThat(Objects::nonNull));
    }

    @Test
    public void domainsOfSizeUpTo() {
        assertThat  (     forAll(integers().between(0, 10))
       .assertThat  (i -> forAll(domains().ofSizeUpTo(i).allowingErrors())
       .itIsTrueThat(d -> d.size() <= i)));
    }
    @Test
    public void domainsWithValuesBetween() {
        assertThat(
           forAll(
              integers("x").between(0, 10),
              integers("y").between(0, 10))
       .assuming  ((x, y) -> x <= y)
       .assertThat((x, y) ->
           forAll(domains().withValuesBetween(x, y))
       .itIsTrueThat(d ->
          d.stream().allMatch(v -> x <= v && v <= y))
        ));
    }
    @Test
    public void domainsWithValuesBetweenBoundsInWrongOrder() {
        assertThat(
          forAll(integers("x").positive(), integers("y").positive())
          .assuming    ((x, y) -> x > y)
          .assertThat  ((x, y) -> randomness ->
             failsThrowing(
                IllegalArgumentException.class,
                () -> domains().withValuesBetween(x, y).build().generate(randomness))
          )
        );
    }


    // ----- PARTIAL ASSIGNMENTS ----------------------------------------------
    @Test
    public void defaultPartialAssignment() {
        assertThat(
           forAll(partialAssignments()).itIsTrueThat(Objects::nonNull)
        );
    }

    @Test
    public void partialAssignmentWithNVariables() {
        assertThat(
           forAll(integers().between(0, 10))
           .assertThat(i ->
              forAll(partialAssignments().withVariables(i))
              .itIsTrueThat(pa -> pa.size() == i)
           ));
    }

    @Test
    public void partialAssignmentWithUpToNVariables() {
        assertThat  (      forAll(integers().between(0, 10))
       .assertThat  (i  -> forAll(partialAssignments().withUpToVariables(i))
       .itIsTrueThat(pa -> pa.size() <= i)));
    }

    @Test
    public void partialAssignmentVariablesBetween() {
        assertThat(forAll(
           integers().between(0, 10),
           integers().between(0, 10))
       .assuming  ((from, to) -> from <= to)
       .assertThat((from, to) -> forAll(partialAssignments().withVariablesBetween(from, to))
       .itIsTrueThat(pa -> from <= pa.size() && pa.size() <= to)
       ));
    }

    @Test
    public void partialAssignmentWithDomainsOfSizeUpTo() {
        assertThat  (      forAll(integers().between(0, 10))
       .assertThat  (i  -> forAll(partialAssignments().withDomainsOfSizeUpTo(i))
       .itIsTrueThat(pa -> pa.stream().allMatch(d-> d.size() <= i))));
    }

    @Test
    public void partialAssignmentWithValuesRanging() {
        assertThat  (forAll(integers().positive(), integers().positive())
       .assuming    ((from, to) -> from <= to)
       .assertThat  ((from, to) -> forAll(partialAssignments().withValuesRanging(from, to))
       .assuming    (pa         -> !pa.isError())
       .itIsTrueThat(pa         ->
             pa.stream().allMatch(d-> d.minimum() >= from)
          && pa.stream().allMatch(d -> d.maximum() <= to)
       )));
    }

    // ----- ASSIGNMENTS ------------------------------------------------------
    @Test
    public void defaultAssignment() {
        assertThat(forAll(assignments()).itIsTrueThat(Objects::nonNull));
    }
    @Test
    public void assignmentWithNVariables() {
        assertThat  (      forAll(integers().between(0, 10))
       .assertThat  (i  -> forAll(assignments().withVariables(i))
       .itIsTrueThat(pa -> pa.size() == i)));
    }
    @Test
    public void assignmentWithUpToVariables() {
        assertThat  (      forAll(integers().between(0, 10))
       .assertThat  (i  -> forAll(assignments().withUpToVariables(i))
       .itIsTrueThat(pa -> pa.size() <= i)));
    }
    @Test
    public void assignmentVariablesBetween() {
        assertThat(forAll(integers().between(0, 10), integers().between(0, 10))
       .assuming((from, to) -> from <= to)
       .assertThat((from, to) ->
          forAll(assignments().withVariablesBetween(from, to))
       .itIsTrueThat(ass -> from <= ass.size() && ass.size() <= to)));
    }
    @Test
    public void assignmentWithValuesRanging() {
        assertThat(forAll(integers().positive(), integers().positive())
       .assuming((from, to) -> from <= to)
       .assertThat((from, to) ->
          forAll(assignments().withValuesRanging(from, to))
       .itIsTrueThat(ass -> ass.stream().allMatch(v -> from <= v && v <= to))));
    }

    // ----- TABLES -----------------------------------------------------------
    @Test
    public void defaultTable() {
        assertThat(forAll(tables())
       .itIsTrueThat(t -> {
            if (t.isEmpty()) {
                return true;
            } else {
                int size = t.stream().findAny().get().size();
                return t.stream().allMatch(line -> line.size() == size);
            }
       }));
    }

    @Test
    public void tablesWithNVariables() {
        assertThat  (         forAll(integers().between(0, 10))
       .assertThat  (i     -> forAll(tables().withVariables(i))
       .itIsTrueThat(table -> table.stream().allMatch(line -> line.size() == i))));
    }

    @Test
    public void tablesWithValuesRanging() {
        assertThat(       forAll(integers().between(0, 10))
       .assertThat(vars ->forAll(integers().positive(), integers().positive())
       .assuming((from, to) -> from <= to)
       .assertThat((from, to) ->
             forAll(tables().withVariables(vars).withValuesRanging(from, to))
       .itIsTrueThat(table ->
             table.stream().allMatch(
                line -> line.size() == vars
             && line.stream().allMatch(v -> from <= v && v <= to))))));
    }

    @Test
    public void tablesWithNLines() {
        assertThat(forAll(
           integers().between(0, 10),
           integers().between(0, 10))
       .assertThat((vars, lines) ->
             forAll(tables().withVariables(vars).withLines(lines))
       .itIsTrueThat(table -> table.size() == lines)));
    }

    @Test
    public void tablesWithUpToLines() {
        assertThat(forAll(
           integers().between(0, 10),
           integers().between(0, 10))
       .assertThat((vars, lines) ->
             forAll(tables().withVariables(vars).withUpToLines(lines))
       .itIsTrueThat(table -> table.size() <= lines)));
    }

    @Test
    public void tablesWithLinesRanging() {
        assertThat(forAll(integers("vars").between(0, 10))
       .assertThat(vars -> forAll(
          integers("from").between(0, 20),
          integers("to"  ).between(0, 30))
       .assuming  ((from, to) -> from <= to)
       .assertThat((from, to) ->
             forAll(tables().withVariables(vars).withLinesRanging(from, to))
       .itIsTrueThat(table -> from <= table.size() && table.size() <= to))));
    }
}
*/