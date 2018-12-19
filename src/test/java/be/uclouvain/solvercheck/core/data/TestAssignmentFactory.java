package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.impl.AssignmentFactory;
import org.junit.Test;

import java.util.Arrays;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAssignmentFactory implements WithSolverCheck {

    @Test
    public void testFromList() {
        assertThat(
           forAll(listOf(integer())).assertThat(lst -> rnd ->
             assertEquals(lst, AssignmentFactory.from(lst))
           )
        );
    }

    @Test
    public void testFromArray() {
        assertThat(
           forAll(arrayOf(Integer.class, integer())).assertThat(a -> rnd ->
              assertEquals(
                 Arrays.stream(a).collect(toList()),
                 AssignmentFactory.from(Arrays.stream(a).mapToInt(Integer::intValue).toArray()))
           )
        );
    }

    @Test
    public void testFromPartialAssignmentEqualsAsAssignmentWhenCompete() {
        assertThat(
           forAll(partialAssignment().withDomainsOfSizeUpTo(1))
           .assuming(PartialAssignment::isComplete)
           .assertThat(partialAssignment -> rnd -> {
               assertEquals(
                  partialAssignment.asAssignment(),
                  AssignmentFactory.from(partialAssignment));
           })
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
                  () -> AssignmentFactory.from(partialAssignment))
            )
           )
        );
    }

    @Test
    public void testCollector() {
        assertThat(
          forAll(listOf(integer())).assertThat(lst -> rnd ->
           assertEquals(lst, lst.stream().collect(AssignmentFactory.collector()))
          )
        );
    }
}
