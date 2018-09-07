package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.core.Domain;
import be.uclouvain.solvercheck.core.StrengthComparison;

import static be.uclouvain.solvercheck.core.StrengthComparison.*;

public final class Utils {
    /** utility class has no constructor */
    private Utils(){}

    public static <A, B> Zip<A, B> zip(final Iterable<A> a, final Iterable<B> b) {
        return new Zip<>(a, b);
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
}
