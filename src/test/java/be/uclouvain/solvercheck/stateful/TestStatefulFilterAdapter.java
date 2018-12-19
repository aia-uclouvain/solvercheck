package be.uclouvain.solvercheck.stateful;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.core.task.StatefulFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestStatefulFilterAdapter implements WithSolverCheck {

    private Filter filter;
    private StatefulFilter tested;

    @Before
    public void setUp() {
        filter = mock(Filter.class);
        tested = stateful(filter);
    }


    @Test
    public void setupPerformsAFirstFiltering() {
        assertThat(
        forAll(partialAssignment())
        .assuming(pa -> !pa.isError())
        .itIsTrueThat(pa -> {
            tested.setup(pa);

            verify(filter, times(1)).filter(pa);
            reset(filter);
            return true; // fails before in case of problem
        }));
    }


    @Test
    public void pushPopRestoresTheState() {
        assertThat(
           forAll(partialAssignment().withValuesRanging(10, 100).withVariablesBetween(1, 5))
           .assuming(pa -> !pa.isError())
           .assuming(pa -> pa.size() >= 1)
           .itIsTrueThat(pa -> {
               PartialAssignment restricted =
                       PartialAssignment.restrict(pa, 0, Operator.EQ, 4);

               when(filter.filter(pa)).thenReturn(pa);
               when(filter.filter(restricted)).thenReturn(restricted);

               tested.setup(pa);

               PartialAssignment pa1 = tested.currentState();
               tested.pushState();
               tested.branchOn(0, Operator.EQ, 4);
               PartialAssignment pa2 = tested.currentState();

               assertTrue(pa2.isError());

               tested.popState();

               assertSame(pa1, tested.currentState());

               reset(filter);
               return true;
           }));
    }

    @Test
    public void popWillNotGoOverTheRoot() {
        assertThat(
           forAll(partialAssignment().withValuesRanging(10, 100).withVariablesBetween(1, 5))
                .assuming(pa -> !pa.isError())
                .itIsTrueThat(pa -> {
                    when(filter.filter(pa))
                            .thenReturn(PartialAssignment.unionOf(pa.size(), List.of()));

                    tested.setup(pa);

                    PartialAssignment pa1 = tested.currentState();
                    for (int i = 0; i < 10; i++) {
                        tested.popState();
                        assertEquals(pa1, tested.currentState());
                    }

                    reset(filter);
                    return true;
                }));
    }

    @Test
    public void whenCurrentStateIsErrorItReturnsAnErrorPa() {
        assertThat(
           forAll(partialAssignment().withValuesRanging(10, 100).withVariablesBetween(1, 5))
           .assuming(PartialAssignment::isError)
           .itIsTrueThat(pa -> {
               when(filter.filter(pa))
                  .thenReturn(PartialAssignment.unionOf(pa.size(), List.of()));
               
               tested.setup(pa);

               Assert.assertEquals(PartialAssignment.error(pa.size()), tested.currentState());
               return true;
           }));
    }

}
