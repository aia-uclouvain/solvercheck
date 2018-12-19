package be.uclouvain.solvercheck.assertions.util;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.randomness.Randomness;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An assertion runner is an object in charge of effectively running the tests
 * for a property that has been declared. It corresponds to the behavior of
 * an `assertThat` statement of the DSL.
 */
public final class AssertionRunner {
    /**
     * In order to implement an efficient (and clean) handling of the propety
     * tests time boxing, we rely on Java's futures API. Therefore, we need an
     * executor service in charge of effectively running the futures. This is
     * what the EXECUTOR field provides.
     */
    private static final ExecutorService EXECUTOR =
       Executors.newCachedThreadPool();

    /** The maximal duration in `utnit` for any given set of tests. */
    private long  duration;
    /** The time unit used to interpret the value of `duration`. */
    private TimeUnit unit;
    /**
     * A flag indicating whether the system should interpret a timeout as a
     * failure, or as a cutoff.
     */
    private boolean failOnTimeout;
    /**
     * The random seed used to initalize the PRNG of the randomness source.
     */
    private long seed;

    /**
     * Creates a new runner configured with all default values (virtually
     * unbounded execution time).
     */
    public AssertionRunner() {
        this(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    /**
     * Creates a new runner with a maximal duration.
     *
     * @param duration the maximal duration in `unit` for any given set of
     *                 tests
     * @param unit the time unit used to interpret the value of `duration`.
     */
    public AssertionRunner(final long duration, final TimeUnit unit) {
        this.duration      = duration;
        this.unit          = unit;
        this.failOnTimeout = false;
        this.seed          = System.currentTimeMillis();
    }

    /**
     * Configures the random seed to use to initialize the fuzzing PRNG.
     *
     * @param seed the random seed.
     * @return this
     */
    @SuppressWarnings("checkstyle:hiddenfield")
    public AssertionRunner randomSeed(final long seed) {
        this.seed = seed;
        return this;
    }

    /**
     * Configures this runner to let it consider that a timeout should be
     * interpreted as a violation of the property.
     *
     * @return this
     */
    public AssertionRunner failingOnTimeout() {
        failOnTimeout = true;
        return this;
    }

    /**
     * Effectively run the given assertion.
     *
     * @param assertion the assertion to check.
     */
    public void assertThat(final Assertion assertion) {
        Future<?> future = EXECUTOR.submit(() -> {
            assertion.check(new Randomness(seed));
        });

        try {
            future.get(duration, unit);
        } catch (ExecutionException exec) {
            Throwable cause = exec.getCause();
            if (cause instanceof AssertionError) {
                throw (AssertionError) cause;
            } else {
                throw new RuntimeException(cause);
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
