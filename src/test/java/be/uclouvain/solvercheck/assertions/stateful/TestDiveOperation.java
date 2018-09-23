package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.WithSolverCheck;
import org.junit.Assert;
import org.junit.Test;

public class TestDiveOperation implements WithSolverCheck {

    @Test
    public void testBranchingRepr() {
        assertThat(
            forAll(integers().allPositive(), operators(), integers().all())
            .itIsTrueThat((var, op, val) -> () ->
                Assert.assertEquals(
                    new Branching(var, op, val).toString(),
                    String.format("Branch on [[ x%s %s %s ]]", var, op, val))
            )
        );
    }

    @Test
    public void testPushRepr() {
        Assert.assertEquals(Push.getInstance().toString(), "Push");
    }

    @Test
    public void testPopRepr() {
        Assert.assertEquals(Pop.getInstance().toString(), "Pop");
    }
}
