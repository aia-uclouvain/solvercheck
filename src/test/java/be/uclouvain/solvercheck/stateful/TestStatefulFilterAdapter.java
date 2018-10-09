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
        forAnyPartialAssignment()
        .assuming(pa -> !pa.isError())
        .checkAssert(pa -> {
            tested.setup(pa);

            verify(filter, times(1)).filter(pa);
            reset(filter);
        });
    }


    @Test
    public void pushPopRestoresTheState() {
        forAnyPartialAssignment()
           .withValuesBetween(10, 100)
           .ofSizeBetween(1, 5)
           .assuming(pa -> !pa.isError())
           .checkAssert(pa -> {
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
           });
    }

    @Test
    public void popWillNotGoOverTheRoot() {
        forAnyPartialAssignment()
                //.withFixedSeed(39822486463804555L)
                .withValuesBetween(10, 100)
                .ofSizeBetween(1, 5)
                .assuming(pa -> !pa.isError())
                .checkAssert(pa -> {
                    when(filter.filter(pa))
                            .thenReturn(PartialAssignment.unionOf(pa.size(), List.of()));

                    tested.setup(pa);

                    PartialAssignment pa1 = tested.currentState();
                    for (int i = 0; i < 10; i++) {
                        tested.popState();
                        assertEquals(pa1, tested.currentState());
                    }

                    reset(filter);
                });
    }

    @Test
    public void whenCurrentStateIsErrorItReturnsAnErrorPa() {
        forAnyPartialAssignment()
           .withValuesBetween(10, 100)
           .ofSizeBetween(1, 5)
           .assuming(PartialAssignment::isError)
           .checkAssert(pa -> {
               when(filter.filter(pa))
                  .thenReturn(PartialAssignment.unionOf(pa.size(), List.of()));
               
               tested.setup(pa);

               Assert.assertEquals(PartialAssignment.error(pa.size()), tested.currentState());
           });
    }

}
