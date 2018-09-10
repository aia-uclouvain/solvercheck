package be.uclouvain.solvercheck.core;

import be.uclouvain.solvercheck.utils.Utils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static be.uclouvain.solvercheck.core.StrengthComparison.*;
import static be.uclouvain.solvercheck.utils.Utils.zip;

public final class PartialAssignment implements Iterable<Domain> {
    private final List<Domain> domains;

    public PartialAssignment(final List<Domain> domains) {
        this.domains = ImmutableList.copyOf(domains);
    }

    public int size() {
        return domains.size();
    }

    public Domain get(int var) {
        return domains.get(var >= 0 ? var : domains.size()+var);
    }

    public PartialAssignment remove(final int var, final Integer val) {
        if (var < 0 || var >= domains.size()) {
            return this;
        } else {
            Domain original = domains.get(var);
            Domain updated = original.remove(val);

            if (original.equals(updated)) {
                return this;
            } else {
                return new PartialAssignment(stream()
                        .map(x -> x == original ? updated : x)
                        .collect(Collectors.toList()));
            }
        }
    }

    public boolean isComplete() {
        return stream().allMatch(Domain::isFixed);
    }

    public boolean isLeaf() {
        return stream().anyMatch(Domain::isEmpty) || isComplete();
    }

    public StrengthComparison compareWith(final PartialAssignment that) {
        if (this.size() != that.size()) {
            return INCOMPARABLE;
        }
        if (zip(this, that).stream().anyMatch(Utils::domainsAreIncomparable)) {
            return INCOMPARABLE;
        }

        boolean hasStronger = zip(this, that).stream().anyMatch(Utils::domainIsStronger);
        boolean hasWeaker = zip(this, that).stream().anyMatch(Utils::domainIsWeaker);
        if (hasStronger && hasWeaker) {
            return INCOMPARABLE;
        }

        if (hasStronger) {
            return STRONGER;
        }
        if (hasWeaker) {
            return WEAKER;
        }
        return EQUIVALENT;
    }

    @SuppressWarnings("unchecked")
    public static PartialAssignment unionOf(Set<Assignment> assignments) {
        if( assignments.isEmpty())
            return new PartialAssignment(ImmutableList.of());

        int nbVars = assignments.stream().findAny().get().size();

        // initialize the container
        Set<Integer>[] domains = (Set<Integer>[]) new Set[nbVars];
        for(int i = 0; i < domains.length; i++) domains[i] = new HashSet<>();

        // actually compute the unions
        for(Assignment a : assignments) {
            if( a.size() > nbVars)
                throw new IllegalArgumentException("Not all assignments have the same number of variables");

            for(int i = 0; i < a.size(); i++) {
                domains[i].add(a.get(i));
            }
        }

        // and then turn that all into a partial assignment
        return new PartialAssignment(
                Arrays.stream(domains).map(Domain::new).collect(Collectors.toList())
        );
    }

    private static boolean isUnitable(Collection<Assignment> assignments) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for( Assignment a : assignments) {
            if( a.size() > max ) max = a.size();
            if( a.size() < min ) min = a.size();
        }
        return assignments.isEmpty() || min == max;
    }

    public Set<Assignment> cartesianProduct() {
        List<Set<Integer>> raw = stream().map(Domain::asSet).collect(Collectors.toList());

        return Sets.cartesianProduct(raw)
                .stream()
                .map(Assignment::new)
                .collect(Collectors.toSet());
    }

    // TODO: réutiliser l'idée des étudiants selon laquelle on peut générer le cross product de manière plus smart
    //       si la propriété a checker est anti monotonique (alldiff). Ou prefix-antimonotonique (regular)
    //       En effet, ce n'est pas la peine d'étendre une solution partielle si on sait d'avance qu'elle va être
    //       rejetée.
    //public static Set<PartialAssignment> cartesianProduct(final List<Domain> domains, final Predicate antiMonotonicChecker) {
    //    return null;
    //}

    @Override
    public Iterator<Domain> iterator() {
        return domains.iterator();
    }

    public Stream<Domain> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public String toString() {
        return domains.toString();
    }

    @Override
    public int hashCode() {
        return domains.hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object other) {
        return other instanceof PartialAssignment && domains.equals(((PartialAssignment) other).domains);
    }

    public List<Domain> asList() {
        return domains;
    }

    public Domain[] asArray() {
        return domains.toArray(new Domain[]{});
    }
}
