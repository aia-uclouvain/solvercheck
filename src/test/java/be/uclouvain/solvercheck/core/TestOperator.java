package be.uclouvain.solvercheck.core;

import be.uclouvain.solvercheck.WithSolverCheck;
import org.junit.Test;

import static be.uclouvain.solvercheck.core.data.Operator.EQ;
import static be.uclouvain.solvercheck.core.data.Operator.GE;
import static be.uclouvain.solvercheck.core.data.Operator.GT;
import static be.uclouvain.solvercheck.core.data.Operator.LE;
import static be.uclouvain.solvercheck.core.data.Operator.LT;
import static be.uclouvain.solvercheck.core.data.Operator.NE;
import static org.junit.Assert.assertEquals;

public class TestOperator implements WithSolverCheck {

    @Test
    public void testToString() {
        assertEquals("==", EQ.toString());
        assertEquals("!=", NE.toString());
        assertEquals("<",  LT.toString());
        assertEquals("<=", LE.toString());
        assertEquals(">",  GT.toString());
        assertEquals(">=", GE.toString());
    }

    @Test
    public void testNot() {
        assertEquals(NE.not(), EQ);
        assertEquals(EQ.not(), NE);
        assertEquals(GE.not(), LT);
        assertEquals(GT.not(), LE);
        assertEquals(LE.not(), GT);
        assertEquals(LT.not(), GE);
    }

    @Test
    public void testCheckEQ() {
        assertThat(
           forAll(integer("X"), integer("Y"))
           .assertThat((x, y) -> rnd ->
              assertEquals((x.equals(y)), EQ.check(x, y))
           )
        );
    }
    @Test
    public void testCheckNE() {
        assertThat(
           forAll(integer("X"), integer("Y"))
              .assertThat((x, y) -> rnd ->
                 assertEquals((!x.equals(y)), NE.check(x,y))
              )
        );
    }
    @Test
    public void testCheckLT() {
        assertThat(
           forAll(integer("X"), integer("Y"))
              .assertThat((x, y) -> rnd ->
                 assertEquals((x.compareTo(y) < 0), LT.check(x,y))
              )
        );
    }
    @Test
    public void testCheckLE() {
        assertThat(
           forAll(integer("X"), integer("Y"))
              .assertThat((x, y) -> rnd ->
                 assertEquals((x.compareTo(y) <= 0), LE.check(x,y))
              )
        );
    }
    @Test
    public void testCheckGT() {
        assertThat(
           forAll(integer("X"), integer("Y"))
              .assertThat((x, y) -> rnd ->
                 assertEquals((x.compareTo(y) > 0), GT.check(x,y))
              )
        );
    }
    @Test
    public void testCheckGE() {
        assertThat(
           forAll(integer("X"), integer("Y"))
              .assertThat((x, y) -> rnd ->
                 assertEquals((x.compareTo(y) >= 0), GE.check(x,y))
              )
        );
    }
}