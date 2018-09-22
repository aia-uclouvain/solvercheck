package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.WithSolverCheck;
import org.junit.Assert;
import org.junit.Test;

public class TestForallAssertions implements WithSolverCheck {
    // ------ FORALL 1 --------------------------------------------------------
    @Test
    public void testForall1() {
        final Counter cnt = new Counter();

        assertThat(
            forAll(integers().between(0, 10))
                .itIsTrueThat(i -> () -> {
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
                forAll(integers().between(0, 10))
                        .assuming(i -> i <= 5)
                        .itIsTrueThat(i -> () -> {
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
                forAll(integers().between(0, 10))
                        .assuming(i -> i <= 5)
                        .assuming(i -> i > 3)
                        .itIsTrueThat(i -> () -> {
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
                forAll(integers().between(0, 10), integers().between(11, 20))
                        .itIsTrueThat((x, y) -> () -> {
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
                forAll(integers().between(0, 10), integers().between(11, 20))
                        .assuming((x, y) -> x <= 5)
                        .itIsTrueThat((x, y) -> () -> {
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
                forAll(integers().between(0, 10), integers().between(11, 20))
                        .assuming((x, y) -> x <= 5)
                        .assuming((x, y) -> y > 15)
                        .itIsTrueThat((x, y) -> () -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= x && x <= 5);
                            Assert.assertTrue(15 < y && y <= 20);
                        })
        );


        Assert.assertTrue(cnt.get() > 0);
    }
    // ------ FORALL 3 --------------------------------------------------------
    @Test
    public void testForall3() {
        final Counter cnt = new Counter();

        assertThat(
                forAll(
                        integers().between(0, 10),
                        integers().between(11, 20),
                        integers().between(21, 30))
                        .itIsTrueThat((x, y, z) -> () -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= x && x <= 10);
                            Assert.assertTrue(11 <= y && y <= 20);
                            Assert.assertTrue(21 <= z && z <= 30);
                        })
        );

        Assert.assertTrue(cnt.get() > 0);
    }

    @Test
    public void testForall3SimpleAssuming() {
        final Counter cnt = new Counter();


        assertThat(
                forAll(
                        integers().between(0, 10),
                        integers().between(11, 20),
                        integers().between(21, 30))
                        .assuming((x, y, z) -> x <= 5)
                        .itIsTrueThat((x, y, z) -> () -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= x && x <= 5);
                            Assert.assertTrue(11 <= y && y <= 20);
                            Assert.assertTrue(21 <= z && z <= 30);
                        })
        );

        Assert.assertTrue(cnt.get() > 0);
    }
    @Test
    public void testForall3MultipleAssumptions() {
        final Counter cnt = new Counter();

        assertThat(
                forAll(
                        integers().between(0, 10),
                        integers().between(11, 20),
                        integers().between(21, 30))
                        .assuming((x, y, z) -> x <= 5)
                        .assuming((x, y, z) -> y <= 15)
                        .itIsTrueThat((x, y, z) -> () -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= x && x <= 5);
                            Assert.assertTrue(11 <= y && y <= 15);
                            Assert.assertTrue(21 <= z && z <= 30);
                        })
        );

        Assert.assertTrue(cnt.get() > 0);
    }
    // ------ FORALL 4 --------------------------------------------------------
    @Test
    public void testForall4() {
        final Counter cnt = new Counter();

        assertThat(
                forAll(
                        integers().between(0, 10),
                        integers().between(11, 20),
                        integers().between(21, 30),
                        integers().between(31, 40))
                        .itIsTrueThat((w, x, y, z) -> () -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= w && w <= 10);
                            Assert.assertTrue(11 <= x && x <= 20);
                            Assert.assertTrue(21 <= y && y <= 30);
                            Assert.assertTrue(31 <= z && z <= 40);
                        })
        );

        Assert.assertTrue(cnt.get() > 0);
    }

    @Test
    public void testForall4SimpleAssuming() {
        final Counter cnt = new Counter();


        assertThat(
                forAll(
                        integers().between(0, 10),
                        integers().between(11, 20),
                        integers().between(21, 30),
                        integers().between(31, 40))
                        .assuming((w, x, y, z) -> w <= 5)
                        .itIsTrueThat((w, x, y, z) -> () -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= w && w <= 5);
                            Assert.assertTrue(11 <= x && x <= 20);
                            Assert.assertTrue(21 <= y && y <= 30);
                            Assert.assertTrue(31 <= z && z <= 40);
                        })
        );

        Assert.assertTrue(cnt.get() > 0);
    }
    @Test
    public void testForall4MultipleAssumptions() {
        final Counter cnt = new Counter();

        assertThat(
                forAll(
                        integers().between(0, 10),
                        integers().between(11, 20),
                        integers().between(21, 30),
                        integers().between(31, 40))
                        .assuming((w, x, y, z) -> w <= 5)
                        .assuming((w, x, y, z) -> z <= 35)
                        .itIsTrueThat((w, x, y, z) -> () -> {
                            cnt.inc();
                            Assert.assertTrue(0 <= w && w <= 5);
                            Assert.assertTrue(11 <= x && x <= 20);
                            Assert.assertTrue(21 <= y && y <= 30);
                            Assert.assertTrue(31 <= z && z <= 35);
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
