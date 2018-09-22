package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.core.Gen;

import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;

public class TestTestConfiguration implements WithSolverCheck {
    private static final PartialAssignment WEAK =
            PartialAssignment.from(List.of(
                    Domain.from(0, 1, 2, 3),
                    Domain.from(0, 1, 2, 3)
            ));

    private static final PartialAssignment STRONG =
            PartialAssignment.from(List.of(
                    Domain.from(0),
                    Domain.from(0)
            ));
    @Test(expected = AssertionError.class)
    public void testAttempts() {
        Filter alpha = mockFilter(WEAK);
        Filter beta  = mockFilter(STRONG);

        Gen<PartialAssignment> gen = mock(Gen.class);
        when(gen.generate(any())).thenReturn(STRONG);

        try {
           assertThat(
               given()
               .attempts(10)
                   .an(alpha).isWeakerThan(beta)
               .assuming(pa -> false)
           );
        } finally {
            verify(alpha, never()).filter(any());
            verify(beta , never()).filter(any());
            verify(gen, times(10)).generate(any());
        }
    }
    @Test
    public void testExamples() {
        Filter alpha = mockFilter(WEAK);
        Filter beta  = mockFilter(STRONG);

        assertThat(given().examples(1).an(alpha).isWeakerThan(beta));

        verify(alpha, times(1)).filter(any());
        verify(beta , times(1)).filter(any());
    }

    @Test
    public void testGet() {
        Assert.assertTrue(Objects.nonNull(given().get()));
    }

    @Test
    public void testDives() {
        Assert.assertEquals(given().dives(5).getNbDives(), 5);
    }

    @Test(expected = AssertionError.class)
    public void testShrinkCycles() {
        Filter alpha = mockFilter(WEAK);
        Filter beta  = mockFilter(STRONG);

        try {
            assertThat(given().shrinkCycles(5).an(alpha).isWeakerThan(beta));
        } finally {
            verify(alpha, times(6)).filter(any());
        }

    }

    private static Filter mockFilter(final PartialAssignment pa) {
        Filter mockF = mock(Filter.class);
        when(mockF.filter(any(PartialAssignment.class))).thenReturn(pa);
        return mockF;
    }
}
