package be.uclouvain.solvercheck.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.core.StrengthComparison.*;

public final class PartialAssignment implements Iterable<Domain<Object>> {
    private final List<Domain<Object>> domains;

    public PartialAssignment(final List<Domain<Object>> domains) {
        this.domains = domains;
    }

    public int                 size()      { return domains.size();     }
    public Domain<Object>      get(int var){ return domains.get(var);   }
    public PartialAssignment   remove(int var, Object val) {
        if( var < 0 || var >= domains.size()) {
            return this;
        } else {
            Domain<Object> original = domains.get(var);
            Domain<Object> updated  = original.remove(val);

            if (original == updated) {
                return this;
            } else {
                return new PartialAssignment(domains.stream()
                                                    .map(x-> x == original ? updated : x)
                                                    .collect(Collectors.toList()));
            }
        }
    }

    public boolean isComplete() {
        return domains.stream().allMatch(Domain::isFixed);
    }

    public StrengthComparison compareWith(PartialAssignment that) {
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

        return EQUIVALENT;
    }

    public Set<PartialAssignment> cartesianProduct() {
        List<Set<Object>> raw  = domains.stream().map(Domain::toSet).collect(Collectors.toList());

        return Sets.cartesianProduct(raw)
                .stream()
                .map(this::toUnaryDomains)
                .map(PartialAssignment::new)
                .collect(Collectors.toSet());
    }

    private List<Domain<Object>> toUnaryDomains(final List<Object> xs) {
        return xs.stream()
                .map(v -> new Domain<Object>(ImmutableSet.of(v)))
                .collect(Collectors.toList());
    }

    // TODO: réutiliser l'idée des étudiants selon laquelle on peut générer le cross product de manière plus smart
    //       si la propriété a checker est anti monotonique (alldiff). Ou prefix-antimonotonique (regular)
    //       En effet, ce n'est pas la peine d'étendre une solution partielle si on sait d'avance qu'elle va être
    //       rejetée.
    public static Set<PartialAssignment> cartesianProduct(final List<Domain<?>> domains, final Predicate antiMonotonicChecker) {
        return null;
    }

    @Override
    public Iterator<Domain<Object>> iterator() { return domains.iterator(); }
    @Override
    public String                   toString() { return domains.toString(); }
    @Override
    public int                      hashCode() { return domains.hashCode(); }
    @Override @SuppressWarnings("unchecked")
    public boolean                  equals(Object o) {
        return (o instanceof PartialAssignment) && domains.equals(((PartialAssignment) o).domains);
    }
}
