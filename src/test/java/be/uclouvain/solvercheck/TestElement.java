package be.uclouvain.solvercheck;

import be.uclouvain.solvercheck.generators.GeneratorsDSL;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestElement implements WithSolverCheck {

    @Test
    public void testElement() {
        given(2, TimeUnit.SECONDS).randomSeed(1L).
        assertThat(
           forAll(listOf("Xs", smallDomain()).ofSizeBetween(1, 5)).assertThat(xs ->
           forAll(domain("Y")).assertThat(y ->
           forAll(domain("Z")).assertThat(z ->
              an(arcConsistent(element())).isEquivalentTo(arcConsistent(element()))
              .forAnyPartialAssignment().with(xs).then(y).then(z)
           )))
        );
    }

    @Test
    public void testAllDiff() {
        assertThat(
           forAll(listOf("Xs", domain())).assertThat(xs ->
               an(arcConsistent(allDiff())).isEquivalentTo(arcConsistent(allDiff()))
               .forAnyPartialAssignment().with(xs)
           )
        );
    }

    private GeneratorsDSL.GenDomainBuilder smallDomain() {
        return domain().ofSizeUpTo(3);
    }
}
