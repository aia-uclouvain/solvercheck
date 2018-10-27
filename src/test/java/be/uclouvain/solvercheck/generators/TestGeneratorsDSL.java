package be.uclouvain.solvercheck.generators;

import org.junit.Before;
import org.junit.Test;
import org.quicktheories.QuickTheory;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

public class TestGeneratorsDSL implements WithQuickTheories {
    private static final int INTMAX   = 100;
    private static final int EXAMPLES = 100;

    private QuickTheory q;

    @Before
    public void setUp() {
        q = qt().withExamples(EXAMPLES)
                .withGenerateAttempts(10000);
    }

    // ----- LISTS ------------------------------------------------------------
    @Test
    public void listsOf() {
        q.forAll(positiveInt()).checkAssert(i ->
                q.forAll(GeneratorsDSL.listsOf(i, positiveInt()))
                    .check(x -> x.size() == i
                             && x.stream().allMatch(v -> 0 <= v && v <= INTMAX))
        );
    }

    @Test
    public void listsOfFromTo() {
        q.forAll(positiveInt(), positiveInt())
         .assuming((from, to) -> from <= to)
         .checkAssert((from, to) ->
                q.forAll(GeneratorsDSL.listsOf(from, to, positiveInt()))
                 .check(x -> from <= x.size()
                          && x.size() <= to
                          && x.stream().allMatch(v -> 0 <= v && v <= INTMAX))
        );
    }
    @Test
    public void listsOfFromToWhenFromIsBiggerThanTo() {
        q.forAll(positiveInt(), positiveInt())
                .assuming((from, to) -> from > to)
                .checkAssert((from, to) ->
                    assertThat(
                        catchThrowable(() ->
                            GeneratorsDSL.listsOf(from, to, positiveInt())))
                        .isInstanceOf(IllegalArgumentException.class)
                );
    }

    @Test
    public void listsOfUpTo() {
        q.forAll(positiveInt()).checkAssert(i ->
            q.forAll(GeneratorsDSL.listsOf(i, positiveInt()))
             .check(x -> x.size() <= i
                      && x.stream().allMatch(v -> 0 <= v && v <= INTMAX))
        );
    }

    // ----- SETS -------------------------------------------------------------
    @Test
    public void setsOfSizeUpTo() {
        q.forAll(positiveInt()).checkAssert(i ->
            q.forAll(GeneratorsDSL.setsOfUpTo(i, positiveInt()))
             .check(x -> x.size() <= i
                      && x.stream().allMatch(v -> 0 <= v && v <= INTMAX))
        );
    }

    @Test
    public void conversionToSet() {
        q.forAll(positiveInt()).checkAssert(i ->
          q.forAll(lists().of(positiveInt()).ofSize(i))
           .checkAssert(list ->
               GeneratorsDSL.<Integer>toSet().apply(list).containsAll(list)
           )
        );
    }

    // ----- OPERATORS --------------------------------------------------------
    @Test
    public void operators() {
        q.forAll(GeneratorsDSL.operators()).check(Objects::nonNull);
    }

    // ----- DOMAINS ----------------------------------------------------------
    @Test
    public void testListOfSizeBetween() {
        q.forAll(lists().of(positiveInt()).ofSizeBetween(0, 0))
         .check(List::isEmpty);
    }

    @Test
    public void defaultDomain() {
        q.forAll(GeneratorsDSL.domains()).check(Objects::nonNull);
    }

    @Test
    public void domainsOfSizeUpTo() {
        q.forAll(positiveInt()).checkAssert(i ->
            q.forAll(GeneratorsDSL.domains().ofSizeUpTo(i))
             .check(x -> x.size() <= i)
        );
    }
    @Test
    public void domainsWithValuesBetween() {
        q.forAll(positiveInt(), positiveInt())
         .assuming((x, y) -> x <= y)
         .checkAssert((x, y) ->
                q.forAll(GeneratorsDSL.domains().withValuesBetween(x, y))
                 .check(d -> d.stream().allMatch(v -> x <= v && v <= y))
        );
    }
    @Test
    public void domainsWithValuesBetweenBoundsInWrongOrder() {
        q.forAll(positiveInt(), positiveInt())
                .assuming((x, y) -> x > y)
                .checkAssert((x, y) ->
                    assertThat(catchThrowable(
                      () -> GeneratorsDSL.domains().withValuesBetween(x, y).build()
                    )).isInstanceOf(IllegalArgumentException.class));
    }


    // ----- PARTIAL ASSIGNMENTS ----------------------------------------------
    @Test
    public void defaultPartialAssingment() {
        q.forAll(GeneratorsDSL.partialAssignments()).check(Objects::nonNull);
    }

    @Test
    public void partialAssignmentWithNVariables() {
        q.forAll(positiveInt())
         .checkAssert(i ->
             q.forAll(GeneratorsDSL.partialAssignments().withVariables(i))
              .check(pa -> pa.size() == i));
    }

    @Test
    public void partialAssignmentWithUpToNVariables() {
        q.forAll(positiveInt())
         .checkAssert(i ->
           q.forAll(GeneratorsDSL.partialAssignments().withUpToVariables(i))
            .check(pa -> pa.size() <= i));
    }

    @Test
    public void partialAssignmentVariablesBetween() {
        q.forAll(positiveInt(), positiveInt())
         .assuming((from, to) -> from <= to)
         .checkAssert((from, to) ->
           q.forAll(
             GeneratorsDSL.partialAssignments().withVariablesBetween(from, to))
            .check(pa -> from <= pa.size() && pa.size() <= to));
    }

    @Test
    public void partialAssignmentWithDomainsOfSizeUpTo() {
        q.forAll(positiveInt())
         .checkAssert(i ->
            q.forAll(GeneratorsDSL.partialAssignments().withDomainsOfSizeUpTo(i))
             .check(pa -> pa.stream().allMatch(d-> d.size() <= i)));
    }

    @Test
    public void partialAssignmentWithValuesRanging() {
        q.forAll(positiveInt(), positiveInt())
         .assuming((from, to) -> from <= to)
         .checkAssert((from, to) ->
           q.forAll(GeneratorsDSL.partialAssignments().withValuesRanging(from, to))
            .assuming(pa -> !pa.isError())
            .check(pa -> pa.stream().allMatch(d-> d.minimum() >= from)
                      && pa.stream().allMatch(d -> d.maximum() <= to)
            ));
    }

    // ----- ASSIGNMENTS ------------------------------------------------------
    @Test
    public void defaultAssignment() {
        q.forAll(GeneratorsDSL.assignments()).check(Objects::nonNull);
    }
    @Test
    public void assignmentWithNVariables() {
        q.forAll(positiveInt())
         .checkAssert(i ->
            q.forAll(GeneratorsDSL.assignments().withVariables(i))
              .check(pa -> pa.size() == i));
    }
    @Test
    public void assignmentWithUpToVariables() {
        q.forAll(positiveInt())
         .checkAssert(i ->
           q.forAll(GeneratorsDSL.assignments().withUpToVariables(i))
            .check(pa -> pa.size() <= i));
    }
    @Test
    public void assignmentVariablesBetween() {
        q.forAll(positiveInt(), positiveInt())
         .assuming((from, to) -> from <= to)
         .checkAssert((from, to) ->
            q.forAll(
               GeneratorsDSL.assignments().withVariablesBetween(from, to))
             .check(ass -> from <= ass.size() && ass.size() <= to));
    }
    @Test
    public void assignmentWithValuesRanging() {
        q.forAll(positiveInt(), positiveInt())
         .assuming((from, to) -> from <= to)
         .checkAssert((from, to) ->
            q.forAll(GeneratorsDSL.assignments().withValuesRanging(from, to))
             .check(ass -> ass.stream().allMatch(v -> from <= v && v <= to)));
    }

    // ----- TABLES -----------------------------------------------------------
    @Test
    public void defaultTable() {
        q.forAll(GeneratorsDSL.tables())
         .check(t -> {
             if (t.isEmpty()) {
                 return true;
             } else {
                 int size = t.stream().findAny().get().size();
                 return t.stream().allMatch(line -> line.size() == size);
             }
         });
    }

    @Test
    public void tablesWithNVariables() {
        q.forAll(positiveInt())
         .checkAssert(i ->
            q.forAll(GeneratorsDSL.tables().withVariables(i))
             .check(table -> table.stream().allMatch(line -> line.size() == i))
         );
    }

    @Test
    public void tablesWithValuesRanging() {
        q.forAll(positiveInt(), positiveInt(), positiveInt())
          .assuming((vars, from, to) -> from <= to)
          .checkAssert((vars, from, to) ->
            q.forAll(
              GeneratorsDSL.tables()
                        .withVariables(vars)
                        .withValuesRanging(from, to))
             .check(table ->
                     table.stream().allMatch(line ->
                             line.size() == vars
                          && line.stream().allMatch(v -> from <= v && v <= to)
                     )
             )
          );
    }

    @Test
    public void tablesWithNLines() {
        q.forAll(positiveInt(), positiveInt())
         .checkAssert((vars, lines) ->
           q.forAll(GeneratorsDSL.tables().withVariables(vars).withLines(lines))
            .check(table -> table.size() == lines)
         );
    }

    @Test
    public void tablesWithUpToLines() {
        q.forAll(positiveInt(), positiveInt())
         .checkAssert((vars, lines) ->
           q.forAll(GeneratorsDSL.tables().withVariables(vars).withUpToLines(lines))
            .check(table -> table.size() <= lines)
         );
    }

    @Test
    public void tablesWithLinesRanging() {
        q.forAll(positiveInt(), positiveInt(), positiveInt())
         .assuming((vars, from, to) -> from <= to)
         .checkAssert((vars, from, to) ->
            q.forAll(GeneratorsDSL.tables().withVariables(vars).withLinesRanging(from, to))
             .check(table -> from <= table.size() && table.size() <= to)
         );
    }

    // ----- UTILITY ----------------------------------------------------------
    private Gen<Integer> positiveInt() {
        return integers().between(0, INTMAX);
    }

}
