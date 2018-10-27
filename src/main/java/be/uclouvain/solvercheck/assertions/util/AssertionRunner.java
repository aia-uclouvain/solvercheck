package be.uclouvain.solvercheck.assertions.util;

import be.uclouvain.solvercheck.assertions.Assertion;

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

    public AssertionRunner() {
        this(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    public AssertionRunner(final long duration, final TimeUnit unit) {
        this.duration      = duration;
        this.unit          = unit;
        this.failOnTimeout = false;
    }

    public AssertionRunner failingOnTimeout() {
        failOnTimeout = true;
        return this;
    }

    public void assertThat(final Assertion assertion) {
        Future<?> future = EXECUTOR.submit(assertion::check);

        try {
            future.get(duration, unit);
        } catch (ExecutionException exec) {
            throw new AssertionError(exec);
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
