package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.core.Domain;
import be.uclouvain.solvercheck.core.PartialAssignment;
import be.uclouvain.solvercheck.core.StrengthComparison;

import static be.uclouvain.solvercheck.core.StrengthComparison.*;

public final class Utils {
    /**
     * utility class has no constructor
     */
    private Utils() {
    }

    public static boolean isValidIndex(int index, int size) {
        return 0 <= index && index < size;
    }

    public static  <T extends Throwable> boolean failsThrowing(Class<T> clazz, Runnable r) {
        try { r.run(); return false; } catch (Exception e) { return clazz.isInstance(e); }
    }

    public static boolean domainsAreIncomparable(ZipEntry<Domain, Domain> entry) {
        return domainsAre(entry, INCOMPARABLE);
    }

    public static boolean domainsAreEquivalent(ZipEntry<Domain, Domain> entry) {
        return domainsAre(entry, EQUIVALENT);
    }

    public static boolean domainIsStronger(ZipEntry<Domain, Domain> entry) {
        return domainsAre(entry, STRONGER);
    }

    public static boolean domainIsWeaker(ZipEntry<Domain, Domain> entry) {
        return domainsAre(entry, WEAKER);
    }

    public static boolean domainsAre(ZipEntry<Domain, Domain> entry, StrengthComparison cmp) {
        return entry.apply(Domain::compareWith) == cmp;
    }

    public static <A, B> Zip<A, B> zip(final Iterable<A> a, final Iterable<B> b) {
        return new Zip<>(a, b);
    }
}
