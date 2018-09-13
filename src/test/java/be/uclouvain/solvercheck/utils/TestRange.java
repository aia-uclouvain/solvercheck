package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.utils.collections.Range;
import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestRange implements WithQuickTheories {

    @Test
    public void oneCannotCreateAnOpenRange(){
        qt().withGenerateAttempts(10000)
            .forAll(integers().all(), integers().all(), integers().all())
            .assuming((from, to, step) -> step == 0 || step > 0 && from > to || step < 0 && from < to)
            .checkAssert((from, to, step) ->
                assertThat(catchThrowable(()-> Range.between(from, to, step)))
                    .isInstanceOf(IllegalArgumentException.class)
            );
    }

    @Test
    public void testRangeSize(){
        qt().withGenerateAttempts(10000)
            .forAll(integers().all(), integers().all(), integers().all())
            .assuming((from, to, step) -> (from <= to && step > 0) || (from > to && step < 0))
            .check((from, to, step) ->
                    Range.between(from, to, step).size() ==
                            (int) Range.between(from, to, step).stream().count()
            );
    }

    @Test
    public void testRangeIterator() {
        qt().withGenerateAttempts(10000)
            .forAll(integers().between(-100, 100), integers().between(-100, 100), integers().between(-100, 100))
            .assuming((from, to, step) -> (from <= to && step > 0) || (from > to && step < 0))
            .checkAssert((from, to, step) -> {
                Range tested = Range.between(from, to, step);
                for(int i = from; i <= to; i+= step) {
                    Assert.assertTrue(tested.contains(i));
                }
            });
    }
}
