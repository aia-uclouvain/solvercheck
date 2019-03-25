package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.utils.collections.Range;
import org.junit.Assert;
import org.junit.Test;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;

public class TestRange implements WithSolverCheck {

    @Test
    public void oneCannotCreateANegativeRange(){
        assertThat(
            forAll(integer(), integer())
            .assuming((from, to) -> from > to)
            .itIsTrueThat((from, to) ->
                failsThrowing(IllegalArgumentException.class,
                   () -> Range.between(from, to)))
            );
    }

    @Test
    public void testRangeSize(){
        assertThat(
        forAll(integer().between(0, 1000), integer().between(0, 1000))
        .assuming((from, to) -> from <= to)
        .itIsTrueThat((from, to) ->
            Range.between(from, to).size() == (int) Range.between(from, to).stream().count()
        ));
    }

    @Test
    public void testRangeIterator() {
        assertThat(
            forAll(integer().between(0, 1000), integer().between(0, 1000))
            .assuming((from, to) -> from <= to)
            .itIsTrueThat((from, to) -> {
                Range tested = Range.between(from, to);
                for(int i = from; i < to; i++) {
                    Assert.assertTrue(tested.contains(i));
                }
                return true;
            }));
    }

    @Test
    public void testRangeOne() {
        Range r = Range.between(2147483647, 2147483647);
        int count = 0;
        for (Integer i : r) {
            count++;
        }
        Assert.assertEquals(count, 0);
    }
}
