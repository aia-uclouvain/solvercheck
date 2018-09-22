package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class TestFilterAssertions implements WithSolverCheck {

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

    @Test
    public void checkEquivalent() {
        Filter alpha = mockFilter(STRONG);
        Filter beta  = mockFilter(STRONG);

        assertThat( an(alpha).isEquivalentTo(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }

    @Test
    public void checkStronger() {
        Filter alpha = mockFilter(STRONG);
        Filter beta  = mockFilter(WEAK);

        assertThat( an(alpha).isStrongerThan(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }
    @Test
    public void checkEquivalentMayBeStronger() {
        Filter alpha = mockFilter(STRONG);
        Filter beta  = mockFilter(STRONG);

        assertThat( an(alpha).isStrongerThan(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }

    @Test(expected = AssertionError.class)
    public void checkWeakerMayNotBeStronger() {
        Filter alpha = mockFilter(WEAK);
        Filter beta  = mockFilter(STRONG);

        assertThat( an(alpha).isStrongerThan(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }

    @Test
    public void checkStriclyStronger() {
        Filter alpha = mockFilter(STRONG);
        Filter beta  = mockFilter(WEAK);

        assertThat( an(alpha).isStrictlyStrongerThan(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }
    @Test(expected = AssertionError.class)
    public void checkEquivalentIsNotStrictlyStronger() {
        Filter alpha = mockFilter(STRONG);
        Filter beta  = mockFilter(STRONG);

        assertThat( an(alpha).isStrictlyStrongerThan(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }

    @Test
    public void checkWeaker() {
        Filter alpha = mockFilter(WEAK);
        Filter beta  = mockFilter(STRONG);

        assertThat( an(alpha).isWeakerThan(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }
    @Test
    public void checkEquivalentCanBeWeaker() {
        Filter alpha = mockFilter(WEAK);
        Filter beta  = mockFilter(WEAK);

        assertThat( an(alpha).isWeakerThan(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }
    @Test(expected = AssertionError.class)
    public void checkStrongerCannotBeWeaker() {
        Filter alpha = mockFilter(STRONG);
        Filter beta  = mockFilter(WEAK);

        assertThat( an(alpha).isWeakerThan(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }
    @Test
    public void checkStriclyWeaker() {
        Filter alpha = mockFilter(WEAK);
        Filter beta  = mockFilter(STRONG);

        assertThat( an(alpha).isWeakerThan(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }
    @Test(expected = AssertionError.class)
    public void checkEquivalentIsNotStriclyWeaker() {
        Filter alpha = mockFilter(WEAK);
        Filter beta  = mockFilter(WEAK);

        assertThat( an(alpha).isStrictlyWeakerThan(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }

    @Test
    public void testIsEquivalent1() {
        Filter alpha = mockFilter(WEAK);
        Filter beta  = mockFilter(WEAK);

        assertThat( an(alpha).isEquivalentTo(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }
    @Test
    public void testIsEquivalent2() {
        Filter alpha = mockFilter(STRONG);
        Filter beta  = mockFilter(STRONG);

        assertThat( an(alpha).isEquivalentTo(beta) );

        verify(alpha, atLeastOnce()).filter(any());
        verify(beta , atLeastOnce()).filter(any());
    }

    @Test
    public void testForall() {
        Filter alpha = mockFilter(STRONG);
        Filter beta  = mockFilter(STRONG);

        assertThat(
           an(alpha).isEquivalentTo(beta).forAll(
             partialAssignments().withUpToVariables(1)
           )
        );

        ArgumentCaptor<PartialAssignment> captorA =
                ArgumentCaptor.forClass(PartialAssignment.class);
        ArgumentCaptor<PartialAssignment> captorB =
                ArgumentCaptor.forClass(PartialAssignment.class);

        verify(alpha, atLeastOnce()).filter(captorA.capture());
        verify(beta , atLeastOnce()).filter(captorB.capture());

        assertTrue(
            captorA.getAllValues().stream().allMatch(pa -> pa.size() <= 1));
        assertTrue(
            captorB.getAllValues().stream().allMatch(pa -> pa.size() <= 1));
    }

    @Test
    public void testAssuming() {
        Filter alpha = mockFilter(STRONG);
        Filter beta  = mockFilter(STRONG);

        assertThat(
                an(alpha).isEquivalentTo(beta).assuming(pa -> pa.size() <= 1)
        );

        ArgumentCaptor<PartialAssignment> captorA =
                ArgumentCaptor.forClass(PartialAssignment.class);
        ArgumentCaptor<PartialAssignment> captorB =
                ArgumentCaptor.forClass(PartialAssignment.class);

        verify(alpha, atLeastOnce()).filter(captorA.capture());
        verify(beta , atLeastOnce()).filter(captorB.capture());

        assertTrue(
                captorA.getAllValues().stream().allMatch(pa -> pa.size() <= 1));
        assertTrue(
                captorB.getAllValues().stream().allMatch(pa -> pa.size() <= 1));
    }

    private static Filter mockFilter(final PartialAssignment pa) {
        Filter mockF = mock(Filter.class);
        when(mockF.filter(any(PartialAssignment.class))).thenReturn(pa);
        return mockF;
    }

}
