package be.uclouvain.solvercheck.assertions.util;

import be.uclouvain.solvercheck.WithSolverCheck;
import org.junit.Assert;
import org.junit.Test;

public class TestForallAssertions implements WithSolverCheck {

    // ------ FORALL 1 --------------------------------------------------------
    @Test
    public void testForall1() {
        final Counter cnt = new Counter();

        assertThat(
            forAll(integer().between(0, 10))
                .assertThat(i -> rand -> {
                    cnt.inc();
                    Assert.assertTrue(0 <= i && i <= 10);
                })
        );

        Assert.assertTrue(cnt.get() > 0);
    }

    @Test
    public void testForall1SimpleAssuming() {
        final Counter cnt = new Counter();

        assertThat(
                forAll(integer().between(0, 10))
                        .assuming(i -> i <= 5)
                        .assertThat(i -> rand -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= i && i <= 5);
                        })
        );

        Assert.assertTrue(cnt.get() > 0);
    }
    @Test
    public void testForall1MultipleAssumptions() {
        final Counter cnt = new Counter();

        assertThat(
                forAll(integer().between(0, 10))
                        .assuming(i -> i <= 5)
                        .assuming(i -> i > 3)
                        .withExamples(10000)
                        .assertThat(i -> rand -> {
                            cnt.inc();
                            Assert.assertTrue(3 < i && i <= 5);
                        })
        );

        Assert.assertTrue(cnt.get() > 0);
    }

    // ------ FORALL 2 --------------------------------------------------------
    @Test
    public void testForall2() {
        final Counter cnt = new Counter();

        assertThat(
                forAll(integer().between(0, 10), integer().between(11, 20))
                        .assertThat((x, y) -> rand -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= x && x <= 10);
                            Assert.assertTrue(11 <= y && y <= 20);
                        })
        );

        Assert.assertTrue(cnt.get() > 0);
    }

    @Test
    public void testForall2SimpleAssuming() {
        final Counter cnt = new Counter();

        assertThat(
                forAll(integer().between(0, 10), integer().between(11, 20))
                        .assuming((x, y) -> x <= 5)
                        .assertThat((x, y) -> rand -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= x && x <= 5);
                            Assert.assertTrue(11 <= y && y <= 20);
                        })
        );

        Assert.assertTrue(cnt.get() > 0);
    }
    @Test
    public void testForall2MultipleAssumptions() {
        final Counter cnt = new Counter();

        assertThat(
                forAll(integer().between(0, 10), integer().between(11, 20))
                        .assuming((x, y) -> x <= 5)
                        .assuming((x, y) -> y > 15)
                        .assertThat((x, y) -> rand -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= x && x <= 5);
                            Assert.assertTrue(15 < y && y <= 20);
                        })
        );


        Assert.assertTrue(cnt.get() > 0);
    }

    private class Counter {
        private int i = 0;

        public int get() {
            return i;
        }

        public void inc() {
            i ++;
        }
    }
}
