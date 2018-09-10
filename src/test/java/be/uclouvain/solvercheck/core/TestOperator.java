package be.uclouvain.solvercheck.core;

import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;

import static be.uclouvain.solvercheck.core.Operator.*;

public class TestOperator implements WithQuickTheories {

    @Test
    public void testToString() {
        Assert.assertEquals("==", EQ.toString());
        Assert.assertEquals("!=", NE.toString());
        Assert.assertEquals("<",  LT.toString());
        Assert.assertEquals("<=", LE.toString());
        Assert.assertEquals(">",  GT.toString());
        Assert.assertEquals(">=", GE.toString());
    }

    @Test
    public void testNot() {
        Assert.assertEquals(NE.not(), EQ);
        Assert.assertEquals(EQ.not(), NE);
        Assert.assertEquals(GE.not(), LT);
        Assert.assertEquals(GT.not(), LE);
        Assert.assertEquals(LE.not(), GT);
        Assert.assertEquals(LT.not(), GE);
    }

    @Test
    public void testCheckEQ() {
        qt().forAll(integers().all(), integers().all())
                .check((x, y) -> EQ.check(x,y) == (x.equals(y)));
    }
    @Test
    public void testCheckNE() {
        qt().forAll(integers().all(), integers().all())
                .check((x, y) -> NE.check(x,y) == (!x.equals(y)));
    }
    @Test
    public void testCheckLT() {
        qt().forAll(integers().all(), integers().all())
                .check((x, y) -> LT.check(x,y) == (x.compareTo(y) < 0));
    }
    @Test
    public void testCheckLE() {
        qt().forAll(integers().all(), integers().all())
                .check((x, y) -> LE.check(x,y) == (x.compareTo(y) <= 0));
    }
    @Test
    public void testCheckGT() {
        qt().forAll(integers().all(), integers().all())
                .check((x, y) -> GT.check(x,y) == (x.compareTo(y) > 0));
    }
    @Test
    public void testCheckGE() {
        qt().forAll(integers().all(), integers().all())
                .check((x, y) -> GE.check(x,y) == (x.compareTo(y) >= 0));
    }
}
