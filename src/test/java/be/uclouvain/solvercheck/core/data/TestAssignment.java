package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.WithSolverCheck;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAssignment implements WithSolverCheck {

    @Test
    public void testSize() {
        assertThat(
           forAll(listOf(integer())).assertThat(a -> rnd ->
            assertEquals(a.size(), Assignment.from(a).size())
           )
        );
    }

    @Test
    public void getReturnsTheIthElementIffItsAValidIndex(){
        assertThat(
           forAll(listOf(integer()), integer().between(0, 10))
           .assuming((a, i) -> isValidIndex(i, a.size()))
           .assertThat((a, i) -> rnd ->
              assertEquals(a.get(i), Assignment.from(a).get(i))
           )
        );
    }

    @Test
    public void getFailsWithExceptionWhenItsNotAValidIndex() {
        assertThat(
           forAll(assignment(), integer().between(0, 10))
              .assuming((a, i) -> !isValidIndex(i, a.size()))
              .assertThat((a, i) -> rnd ->
                 assertTrue(
                    failsThrowing(
                       IndexOutOfBoundsException.class,
                       () -> a.get(i) )
                 )
              )
        );
    }

    @Test
    public void testEqualsAllValuesMatch() {
        assertThat(
           forAll(listOf(integer()), listOf(integer()))
              .assertThat((a, b) -> rnd ->
                 assertEquals(
                    a.equals(b),
                    Assignment.from(a).equals(Assignment.from(b)))
              )
        );
    }

    @Test
    public void testHashCode() {
       assertThat(
          forAll(assignment(), assignment())
          .assertThat((a, b) -> rnd ->
             assertTrue(!a.equals(b) || (a.hashCode() == (b.hashCode())))
          )
        );
    }

    @Test
    public void testToString() {
        assertEquals(
            Assignment.from(List.of(1, 2, 3, 4)).toString(),
            "x0=1, x1=2, x2=3, x3=4"
        );
    }

    @Test
    public void testFromList() {
        assertThat(
          forAll(listOf(integer()))
          .assertThat(lst -> rnd ->
             assertEquals(lst, Assignment.from(lst))
          )
        );
    }

    @Test
    public void testFromArray() {
        assertThat(
           forAll(arrayOf(Integer.class, integer()))
              .assertThat(lst -> rnd ->
                 assertEquals(
                    Arrays.stream(lst).collect(toList()),
                    Assignment.from(Arrays.stream(lst).mapToInt(Integer::intValue).toArray()))
              )
        );
    }

    @Test
    public void testFromPartialAssignmentEqualsAsAssignmentWhenCompete() {
        assertThat(
           forAll(partialAssignment())
           .assuming(pa -> pa.isComplete())
           .assertThat(partialAssignment -> rnd ->
            assertEquals(
               partialAssignment.asAssignment(),
               Assignment.from(partialAssignment))
           )
        );
    }

    @Test
    public void testFromPartialAssignmentFailsWhenNotCompete() {
        assertThat(
           forAll(partialAssignment())
              .assuming(pa -> !pa.isComplete())
              .assertThat(partialAssignment -> rnd ->
                 assertTrue(
                    failsThrowing(
                        IllegalStateException.class,
                        ()  -> Assignment.from(partialAssignment))
                 )
              )
        );
    }

    @Test
    public void testCollector() {
        assertThat(
           forAll(listOf(integer())).assertThat(lst -> rnd ->
             assertEquals(lst, lst.stream().collect(Assignment.collector()))
           )
        );
    }

}
