package be.uclouvain.solvercheck.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static be.uclouvain.solvercheck.core.StrengthComparison.*;

public final class PartialAssignment implements Iterable<Domain> {
    private final List<Domain> domains;

    public PartialAssignment(final List<Domain> domains) {
        this.domains = domains;
    }

    public int                 size()            { return domains.size();     }
    public Domain              get(final int var){ return domains.get(var);   }
    public PartialAssignment   remove(final int var, final Integer val) {
        if( var < 0 || var >= domains.size()) {
            return this;
        } else {
            Domain original = domains.get(var);
            Domain updated  = original.remove(val);

            if (original.equals(updated)) {
                return this;
            } else {
                return new PartialAssignment(stream()
                            .map(x-> x == original ? updated : x)
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
        if( this.size() != that.size() ) {
            return INCOMPARABLE;
        }

        int weakerCount    = 0;
        int strongerCount  = 0;

        for(int i = 0; i < size(); i++) {
            switch(this.get(i).compareWith(that.get(i))) {
                case INCOMPARABLE:
                    return INCOMPARABLE;
                case WEAKER:
                    if( strongerCount > 0) {
                        return INCOMPARABLE;
                    } else {
                        weakerCount ++ ;
                    }
                    break;
                case STRONGER:
                    if( weakerCount > 0) {
                        return INCOMPARABLE;
                    } else {
                        strongerCount ++;
                    }
                    break;
                default:
                    /* do nothing */
                    break;
            }
        }

        if( weakerCount > 0) {
            return WEAKER;
        } else if( strongerCount > 0) {
            return STRONGER;
        } else {
            return EQUIVALENT;
        }
    }

    public Set<PartialAssignment> cartesianProduct() {
        List<Set<Integer>> raw  = stream().map(Domain::toSet).collect(Collectors.toList());

        return Sets.cartesianProduct(raw)
                .stream()
                .map(this::toUnaryDomains)
                .map(PartialAssignment::new)
                .collect(Collectors.toSet());
    }

    private List<Domain> toUnaryDomains(final List<Integer> values) {
        return values.stream()
                .map(v -> new Domain(ImmutableSet.of(v)))
                .collect(Collectors.toList());
    }

    // TODO: réutiliser l'idée des étudiants selon laquelle on peut générer le cross product de manière plus smart
    //       si la propriété a checker est anti monotonique (alldiff). Ou prefix-antimonotonique (regular)
    //       En effet, ce n'est pas la peine d'étendre une solution partielle si on sait d'avance qu'elle va être
    //       rejetée.
    //public static Set<PartialAssignment> cartesianProduct(final List<Domain> domains, final Predicate antiMonotonicChecker) {
    //    return null;
    //}

    @Override
    public Iterator<Domain>         iterator() { return domains.iterator(); }
    public Stream<Domain>           stream()   { return StreamSupport.stream(spliterator(), false);}
    @Override
    public String                   toString() { return domains.toString(); }
    @Override
    public int                      hashCode() { return domains.hashCode(); }
    @Override @SuppressWarnings("unchecked")
    public boolean                  equals(final Object other) {
        return other instanceof PartialAssignment && domains.equals(((PartialAssignment) other).domains);
    }
}
