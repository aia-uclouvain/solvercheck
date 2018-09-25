package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.utils.collections.Range;
import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestRange implements WithQuickTheories {

    @Test
    public void oneCannotCreateANegativeRange(){
        qt().withGenerateAttempts(100000)
            .forAll(integers().all(), integers().all())
            .assuming((from, to) -> from > to)
            .checkAssert((from, to) ->
                assertThat(catchThrowable(()-> Range.between(from, to)))
                    .isInstanceOf(IllegalArgumentException.class)
            );
    }

    @Test
    public void testRangeSize(){
        qt().withGenerateAttempts(100000)
            .forAll(integers().between(0, 1000), integers().between(0, 1000))
            .assuming((from, to) -> from <= to)
            .check((from, to) ->
                Range.between(from, to).size() == (int) Range.between(from, to).stream().count()
            );
    }

    @Test
    public void testRangeIterator() {
        qt().withGenerateAttempts(100000)
            .forAll(integers().between(0, 1000), integers().between(0, 1000))
            .assuming((from, to) -> from <= to)
            .checkAssert((from, to) -> {
                Range tested = Range.between(from, to);
                for(int i = from; i < to; i++) {
                    Assert.assertTrue(tested.contains(i));
                }
            });
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
