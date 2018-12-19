package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.Operator;
import org.junit.Assert;
import org.junit.Test;

public class TestDiveOperation implements WithSolverCheck {

    @Test
    public void testBranchingRepr() {
        for (int var = 0; var < 5; var++) {
            for (Operator op : Operator.values()) {
                for (int val = -10; val < 10; val += 2) {
                    Assert.assertEquals(
                       new Branching(var, op, val).toString(),
                       String.format("Branch on [[ x%s %s %s ]]", var, op, val));
                }
            }
        }
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
