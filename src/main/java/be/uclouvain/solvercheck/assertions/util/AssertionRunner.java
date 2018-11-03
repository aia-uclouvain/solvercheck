package be.uclouvain.solvercheck.assertions.util;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.pbt.Randomness;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class AssertionRunner {

    private static final ExecutorService EXECUTOR =
       Executors.newCachedThreadPool();

    private long     duration;
    private TimeUnit unit;
    private boolean  failOnTimeout;
    private long     seed;

    public AssertionRunner() {
        this(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    public AssertionRunner(final long duration, final TimeUnit unit) {
        this.duration      = duration;
        this.unit          = unit;
        this.failOnTimeout = false;
        this.seed          = System.currentTimeMillis();
    }

    public AssertionRunner randomSeed(final long seed) {
        this.seed = seed;
        return this;
    }

    public AssertionRunner failingOnTimeout() {
        failOnTimeout = true;
        return this;
    }

    public void assertThat(final Assertion assertion) {
        Future<?> future = EXECUTOR.submit(() -> {
            assertion.check(new Randomness(seed));
        });

        try {
            future.get(duration, unit);
        } catch (ExecutionException exec) {
            if (exec.getCause() instanceof AssertionError) {
                throw (AssertionError) exec.getCause();
            } else {
                throw new RuntimeException(exec.getCause());
            }
        } catch (TimeoutException timeout) {
            if (failOnTimeout) {
                throw new AssertionError("Assertion timed out");
            }
            // else silently swallow
        } catch (InterruptedException interrupted) {
            throw new AssertionError("Test thread was interrupted", interrupted);
        } finally {
            // Don't let it run away after timeout elapsed!
            future.cancel(true);
        }
    }

}
