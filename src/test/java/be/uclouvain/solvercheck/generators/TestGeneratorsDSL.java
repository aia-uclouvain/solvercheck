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
        assertThat  (     forAll(integers().between(0, 10))
       .assertThat  (i -> forAll(GeneratorsDSL.lists().ofSize(i))
       .itIsTrueThat(x -> x.size() == i )));
    }

    @Test
    public void listsOfFromTo() {
        assertThat(forAll(
           integers("from").between(0, 10),
           integers("to"  ).between(0, 10))
        .assuming  ((from, to) -> from <= to)
        .assertThat((from, to) ->
           forAll(GeneratorsDSL.lists().ofSizeBetween(from, to))
        .itIsTrueThat(x ->
           from <= x.size() && x.size() <= to)));
    }
    @Test
    public void listsOfFromToWhenFromIsBiggerThanTo() {
        assertThat(forAll(
           integers("from").between(0, 10),
           integers("to"  ).between(0, 10))
        .assuming  ((from, to) -> from > to)
        .assertThat((from, to) -> randomness ->
           failsThrowing(
              IllegalArgumentException.class,
              () -> GeneratorsDSL.lists().ofSizeBetween(from, to).build().generate(randomness))
        ));
    }

    @Test
    public void listsOfUpTo() {
        assertThat  (     forAll(integers().between(0, 10))
       .assertThat  (i -> forAll(GeneratorsDSL.lists().ofSizeUpTo(i))
       .itIsTrueThat(x -> x.size() <= i)));
    }

    // ----- SETS -------------------------------------------------------------
    @Test
    public void setsOfSizeUpTo() {
        assertThat  (     forAll(integers().between(0, 10))
       .assertThat  (i -> forAll(GeneratorsDSL.sets().ofSizeUpTo(i).possiblyEmpty())
       .itIsTrueThat(x -> x.size() <= i)));
    }

    // ----- OPERATORS --------------------------------------------------------
    @Test
    public void tetsOperators() {
        assertThat(forAll(GeneratorsDSL.operators()).itIsTrueThat(Objects::nonNull));
    }

    // ----- DOMAINS ----------------------------------------------------------
    @Test
    public void testListOfSizeBetween() {
        assertThat(forAll(lists().ofSizeBetween(0, 0)).itIsTrueThat(List::isEmpty));
    }

    @Test
    public void defaultDomain() {
        assertThat(forAll(GeneratorsDSL.domains()).itIsTrueThat(Objects::nonNull));
    }

    @Test
    public void domainsOfSizeUpTo() {
        assertThat  (     forAll(integers().between(0, 10))
       .assertThat  (i -> forAll(GeneratorsDSL.domains().ofSizeUpTo(i).allowingErrors())
       .itIsTrueThat(x -> x.size() <= i)));
    }
    @Test
    public void domainsWithValuesBetween() {
        assertThat(forAll(
           integers("x").positive(),
           integers("y").positive())
        .assuming  ((x, y) -> x <= y)
        .assertThat((x, y) ->
           forAll(GeneratorsDSL.domains().withValuesBetween(x, y))
        .itIsTrueThat(d -> d.stream().allMatch(v -> x <= v && v <= y))));
    }
    @Test
    public void domainsWithValuesBetweenBoundsInWrongOrder() {
        assertThat(forAll(
           integers("x").positive(),
           integers("y").positive())
       .assuming  ((x, y) -> x > y)
       .assertThat((x, y) -> randomness ->
          failsThrowing(
             IllegalArgumentException.class,
             () -> GeneratorsDSL.domains().withValuesBetween(x, y).build().generate(randomness)
          )
       ));
    }

    // ----- PARTIAL ASSIGNMENTS ----------------------------------------------
    @Test
    public void defaultPartialAssingment() {
        assertThat(forAll(GeneratorsDSL.simplePartialAssignments()).itIsTrueThat(Objects::nonNull));
    }

    @Test
    public void partialAssignmentWithNVariables() {
        assertThat  (      forAll(integers().between(0, 10))
       .assertThat  (i  -> forAll(GeneratorsDSL.simplePartialAssignments().withVariables(i))
       .itIsTrueThat(pa -> pa.size() == i)));
    }

    @Test
    public void partialAssignmentWithUpToNVariables() {
        assertThat  (     forAll(integers().between(0, 10))
       .assertThat  (i -> forAll(GeneratorsDSL.simplePartialAssignments().withUpToVariables(i))
       .itIsTrueThat(pa -> pa.size() <= i)));
    }

    @Test
    public void partialAssignmentVariablesBetween() {
        assertThat(forAll(
           integers("from").between(0, 10),
           integers("to"  ).between(0, 10))
      .assuming  ((from, to) -> from <= to)
      .assertThat((from, to) ->
         forAll(GeneratorsDSL.simplePartialAssignments().withVariablesBetween(from, to))
      .itIsTrueThat(pa -> from <= pa.size() && pa.size() <= to)));
    }

    @Test
    public void partialAssignmentWithDomainsOfSizeUpTo() {
        assertThat(forAll(integers().between(0, 10))
       .assertThat(i ->
          forAll(GeneratorsDSL.simplePartialAssignments().withDomainsOfSizeUpTo(i))
       .itIsTrueThat(pa -> pa.stream().allMatch(d-> d.size() <= i))));
    }

    @Test
    public void partialAssignmentWithValuesRanging() {
        assertThat(forAll(
           integers("from").positive(),
           integers("to"  ).positive())
       .assuming  ((from, to) -> from <= to)
       .assertThat((from, to) ->
          forAll(GeneratorsDSL.simplePartialAssignments().withValuesRanging(from, to))
       .assuming    (pa -> !pa.isEmpty() && !pa.isError())
       .itIsTrueThat(pa ->
             pa.stream().allMatch(d -> d.minimum() >= from)
          && pa.stream().allMatch(d -> d.maximum() <= to)
        )));
    }

    // ----- ASSIGNMENTS ------------------------------------------------------
    @Test
    public void defaultAssignment() {
        assertThat(forAll(GeneratorsDSL.assignments()).itIsTrueThat(Objects::nonNull));
    }
    @Test
    public void assignmentWithNVariables() {
        assertThat  (      forAll(integers().between(0, 10))
       .assertThat  (i  -> forAll(GeneratorsDSL.assignments().withVariables(i))
       .itIsTrueThat(pa -> pa.size() == i)));
    }
    @Test
    public void assignmentWithUpToVariables() {
        assertThat  (      forAll(integers().between(0, 10))
       .assertThat  (i  -> forAll(GeneratorsDSL.assignments().withUpToVariables(i))
       .itIsTrueThat(pa -> pa.size() <= i)));
    }
    @Test
    public void assignmentVariablesBetween() {
        assertThat(forAll(
           integers("from").between(0, 10),
           integers("to"  ).between(0, 10))
       .assuming  ((from, to) -> from <= to)
       .assertThat((from, to) ->
          forAll(GeneratorsDSL.assignments().withVariablesBetween(from, to))
       .itIsTrueThat(ass -> from <= ass.size() && ass.size() <= to)));
    }
    @Test
    public void assignmentWithValuesRanging() {
        assertThat(forAll(
           integers("from").positive(),
           integers("to"  ).positive())
       .assuming  ((from, to) -> from <= to)
       .assertThat((from, to) ->
            forAll(GeneratorsDSL.assignments().withValuesRanging(from, to))
       .itIsTrueThat(ass -> ass.stream().allMatch(v -> from <= v && v <= to))));
    }

    // ----- TABLES -----------------------------------------------------------
    @Test
    public void defaultTable() {
        assertThat(forAll(GeneratorsDSL.tables())
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
       .assertThat  (i     -> forAll(GeneratorsDSL.tables().withVariables(i))
       .itIsTrueThat(table -> table.stream().allMatch(line -> line.size() == i))));
    }

    @Test
    public void tablesWithValuesRanging() {
        assertThat(        forAll(integers("vars").between(0, 10))
       .assertThat(vars -> forAll(
          integers("from").positive(),
          integers("to"  ).positive())
       .assuming((from, to) -> from <= to)
       .assertThat((from, to) -> forAll(
          GeneratorsDSL.tables().withVariables(vars).withValuesRanging(from, to))
       .itIsTrueThat(table ->
                     table.stream().allMatch(line ->
                             line.size() == vars
                          && line.stream().allMatch(v -> from <= v && v <= to)
                     )
             )
          )));
    }

    @Test
    public void tablesWithNLines() {
        assertThat(forAll(
           integers("vars" ).between(0, 10),
           integers("lines").between(0, 10))
       .assertThat((vars, lines) ->
           forAll(GeneratorsDSL.tables().withVariables(vars).withLines(lines))
       .itIsTrueThat(table -> table.size() == lines)));
    }

    @Test
    public void tablesWithUpToLines() {
        assertThat(forAll(
           integers("vars" ).between(0, 10),
           integers("lines").between(0, 10))
       .assertThat((vars, lines) ->
           forAll(GeneratorsDSL.tables().withVariables(vars).withUpToLines(lines))
       .itIsTrueThat(table -> table.size() <= lines)));
    }

    @Test
    public void tablesWithLinesRanging() {
        assertThat(forAll(integers("vars" ).between(0, 10))
       .assertThat(vars -> forAll(
          integers("from").between(0, 10),
          integers("to"  ).between(0, 10))
       .assuming((from, to) -> from <= to)
       .assertThat((from, to) ->
            forAll(GeneratorsDSL.tables().withVariables(vars).withLinesRanging(from, to))
       .itIsTrueThat(table -> from <= table.size() && table.size() <= to))));
    }
}
