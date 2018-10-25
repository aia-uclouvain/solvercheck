package be.uclouvain.solvercheck.assertions.util;

import be.uclouvain.solvercheck.core.data.PartialAssignment;
import org.quicktheories.core.Strategy;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class provides a mini framework to setup tests relative to the use of
 * partial assignments in a fluent fashion.
 *
 * <div>
 *     <h1>Note</h1>
 *     In a sense, one could consider that the work accomplished by this
 *     class should be realized by a `generator`. This would indeed feel more
 *     consistent with the rest of the QuickTheories framework. However,
 *     practice has shown that the partial assignments sample produced using
 *     this class is more varied and tackles a wider range of possible cases.
 * </div>
 *
 * @param <T> the concrete type of the implementing subclass.
 */

public interface WithFluentConfig<T extends WithFluentConfig<T>>
       extends Supplier<Strategy> {

    /**
     * Makes the interface of the filter assertion more fluent.
     *
     * @return this.
     */
    T forAnyPartialAssignment();

    /**
     * Configures the seed of the PRNG used to pseudo-randomly generate partial
     * assignments, anchors and values.
     *
     * @param seed the seed to use to initialize the PRNG
     * @return this
     */
    T withFixedSeed(long seed);

    /**
     * Configures the underlying quicktheories layer to try to generate a
     * partial assignment satisfying the assumptions at least `attempts` time
     * before failing on value exhaustion.
     *
     * @param attempts the number of attempts to try before value exhaustion.
     * @return this
     */
    T withGenerateAttempts(int attempts);

    /**
     * Configures the desired number of anchors which are picked to seed a
     * round of partial assignment tests.
     *
     * @param n the number of anchor values to generate.
     * @return this
     */
    T withAnchorSamples(int n);

    /**
     * Configures the number of tests which are generated for each anchor value.
     *
     * @param n the number of example partial assignments produced for each
     *          anchor value.
     * @return this
     */
    T withExamples(int n);

    /**
     * Configures the number of shrink cycles used by the underlying
     * quicktheories layer in order to determine the smallest possible
     * violation instances.
     *
     * @param cycles the number of shrink cycles to use.
     * @return this
     */
    T withShrinkCycles(int cycles);

    /**
     * Configures the range of values which can appear in the partial
     * assignments.
     *
     * <div>
     *     <h1>Note</h1>
     *     The range of values must be expressed with x being smaller or
     *     equal to y. Any other combination will be rejected.
     * </div>
     *
     * @param x the lowest value that can possibly appear in a partial
     *          assignment.
     * @param y the highest value that can possibly appear in a partial
     *          assignment.
     * @return this
     */
    T withValuesBetween(int x, int y);

    /**
     * Configures the maximum spread between any two values appearing in the
     * partial assignment.
     *
     * @param n the maximum allowed spread (must be positive)
     * @return this
     */
    T spreading(int n);

    /**
     * Configures the maximum size of the domains composing the partial
     * assignments.
     *
     * @param n the maximum allowed domain size
     * @return this
     */
    T withDomainsOfSizeUpTo(int n);

    /**
     * Configures the size of the generated partial assignments. All
     * resulting PAs will have that exact size.
     *
     * @param x the exact target size (must be positive).
     * @return this
     */
    T ofSize(int x);

    /**
     * Configures the minimum and maximum size of the generated partial
     * assignments. All resulting PAs will comprise at least x variables and
     * at most y.
     *
     * @param x minimum number of variables in the generated partial
     *          assignments.
     * @param y maximum number of variables in the generated partial
     *          assignments.
     * @return this
     */
    T ofSizeBetween(int x, int y);

    /**
     * Enforces the given predicate as an assumption on the generated partial
     * assignments.
     *
     * @param assumption the assumption which must be satisfied
     * @return this
     */
    T assuming(Predicate<PartialAssignment> assumption);

}
