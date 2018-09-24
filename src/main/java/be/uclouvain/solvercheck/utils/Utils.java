package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.utils.collections.Zip;
import be.uclouvain.solvercheck.utils.collections.ZipEntry;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.WEAKER;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.INCOMPARABLE;

/**
 * A bunch of general purpose utility methods.
 */
public final class Utils {
    /**
     * Utility class has no constructor.
     */
    private Utils() { }

    /**
     * tests whether the given `index` is a valid position in a a collection
     * of the given `size`.
     *
     * @param index the index whose validity is being checked
     * @param size the size of the collection in which `index` can/or not be
     *             an index.
     *
     * @return true iff index is a valid position inside a collection of size
     * `size`. In other words: this method returns true iff
     * 0 &lt;= index &lt; size.
     */
    public static boolean isValidIndex(final int index, final int size) {
        return 0 <= index && index < size;
    }

    /**
     * Returns true iff the given runnable fails throwing an exception of the
     * given type T.
     *
     * @param clazz the class object capturing the type T of the exception to
     *             be thrown.
     * @param runnable a runnable snippet that may throw a Throwable while
     *                 executing.
     * @param <T> the type of the expected throwable.
     *
     * @return true iff `runnable` has thrown an exception of type T while
     * executing.
     */
    public static  <T extends Throwable> boolean failsThrowing(
            final Class<T> clazz,
            final Runnable runnable) {

        try {
            runnable.run();
            return false;
        } catch (Throwable t) {
            return clazz.isInstance(t);
        }

    }

    /**
     * Compares the two entries given by the specified `ZipEntry` and tells
     * whether or not they are incomparable. The comparison happens in the
     * following direction:
     * ZipEntry.first() compareWith ZipEntry.second()
     *
     * @param entry the zip entry holding a reference to the two domains to
     *              compare.
     * @return true iff entry.first() is incomparable with entry.second()
     */
    public static boolean domainsAreIncomparable(
            final ZipEntry<Domain, Domain> entry) {

        return domainsAre(entry, INCOMPARABLE);
    }

    /**
     * Compares the two entries given by the specified `ZipEntry` and tells
     * whether or not they are equivalent. The comparison happens in the
     * following direction:
     * ZipEntry.first() compareWith ZipEntry.second()
     *
     * @param entry the zip entry holding a reference to the two domains to
     *              compare.
     * @return true iff entry.first() is equivalent to entry.second()
     */
    public static boolean domainsAreEquivalent(
            final ZipEntry<Domain, Domain> entry) {

        return domainsAre(entry, EQUIVALENT);
    }

    /**
     * Compares the two entries given by the specified `ZipEntry` and tells
     * whether the first one is stronger than the second. The comparison
     * happens in the following direction:
     * ZipEntry.first() compareWith ZipEntry.second()
     *
     * @param entry the zip entry holding a reference to the two domains to
     *              compare.
     * @return true iff entry.first() is stronger than entry.second()
     */
    public static boolean domainIsStronger(
            final ZipEntry<Domain, Domain> entry) {

        return domainsAre(entry, STRONGER);
    }

    /**
     * Compares the two entries given by the specified `ZipEntry` and tells
     * whether the first one is weaker than the second. The comparison
     * happens in the following direction:
     * ZipEntry.first() compareWith ZipEntry.second()
     *
     * @param entry the zip entry holding a reference to the two domains to
     *              compare.
     * @return true iff entry.first() is weaker than entry.second()
     */
    public static boolean domainIsWeaker(
            final ZipEntry<Domain, Domain> entry) {

        return domainsAre(entry, WEAKER);
    }

    /**
     * Compares the two entries given by the specified `ZipEntry` and tells
     * whether they relate as specified by `cmp`. The comparison happens in
     * the following direction:
     * ZipEntry.first() compareWith ZipEntry.second()
     *
     * @param entry the zip entry holding a reference to the two domains to
     *              compare.
     * @param cmp the expected partial ordering between first and second.
     * @return true iff entry.first() `cmp` entry.second()
     */
    public static boolean domainsAre(
            final ZipEntry<Domain, Domain> entry,
            final PartialOrdering cmp) {

        return entry.apply(Domain::compareWith) == cmp;
    }

    /**
     * Implements the 'zip' functional programming idiom. It takes two
     * iterable as input and produces a new iterable object which delivers
     * 2-tuples (see ZipEntry) at each iteration.
     *
     * .. Note::
     *    In the event where the two iterables do not have the same size,
     *    the zipped iterable iterable will generate entries with null-padding.
     *
     * @param a the first operand of the zip operator
     * @param b the second operand of the zip operator
     * @param <A> the type of the elements held in `a`
     * @param <B> the type of the elements held in `b`
     *
     * @return a new iterable that iterates over 2-tuples each made of one
     * item from a and one item from b.
     */
    public static <A, B> Zip<A, B> zip(
            final Iterable<A> a,
            final Iterable<B> b) {

        return new Zip<>(a, b);
    }
}
