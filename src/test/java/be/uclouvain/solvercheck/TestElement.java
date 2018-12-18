package be.uclouvain.solvercheck;

import be.uclouvain.solvercheck.generators.GeneratorsDSL;
import org.junit.Test;

public class TestElement implements WithSolverCheck {

    @Test
    public void testElement() {
        //given().randomSeed(1L).
        assertThat(
           forAll(listOf("Xs", smallDomain()).ofSizeBetween(1, 5)).assertThat(xs ->
           forAll(domain("Y")).assuming(d -> true).assertThat(y ->
           forAll(domain("Z")).assertThat(z ->
              an(arcConsistent(element())).isEquivalentTo(boundZConsistent(element()))
              .forAnyPartialAssignment().with(xs).then(y).then(z)
           )))
        );
    }

    @Test
    public void testAllDiff() {
        assertThat(
           forAll(listOf("Xs", domain())).assertThat(xs ->
               an(arcConsistent(allDiff())).isEquivalentTo(boundDConsistent(allDiff()))
               .forAnyPartialAssignment().with(xs)
           )
        );
    }

    private GeneratorsDSL.GenDomainBuilder smallDomain() {
        return domain().ofSizeBetween(1, 3);
    }
}
