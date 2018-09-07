package be.uclouvain.solvercheck.core;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static be.uclouvain.solvercheck.core.StrengthComparison.*;

/**
 * This class implements the core idea of what a cp domain should be: a set of values for some given type.
 *
 * From a technical point-of-view, this type should be considered an application of the 'new type' pattern.
 * It gives a semantically richer meaning to sets of T's while adding some benefits at the same time
 * (ie: cartesian product, partial order comparison).
 */
public final class Domain implements Iterable<Integer> {
    private final Set<Integer> values;

    public Domain(final Set<Integer> values) {
        this.values = values;
    }

    public Domain remove(final Integer val) {
        if( !contains(val) ) {
            return this;
        } else {
            return new Domain(values.stream()
                    .filter (x -> !x.equals(val))
                    .collect(Collectors.toSet()));
        }
    }

    /*
     * Note:
     * I am hereby explicitly avoiding to implement the Comparable as the latter imposes a total ordering,
     * which does not apply do domains.
     */
    public StrengthComparison compareWith(final Domain that) {
        if ( this.size() < that.size() ) {
            return that.values.containsAll(this.values) ? STRONGER   : INCOMPARABLE;
        }
        else if ( this.size() > that.size() ) {
            return this.values.containsAll(that.values) ? WEAKER     : INCOMPARABLE;
        }
        else {
            return this.values.equals(that.values)      ? EQUIVALENT : INCOMPARABLE;
        }
    }

    public boolean           contains(int x) { return values.contains(x);                                }
    public int               size()          { return values.size();                                     }
    public boolean           isEmpty()       { return size() == 0;                                       }
    public boolean           isFixed()       { return size() == 1;                                       }
    @Override
    public Iterator<Integer> iterator()      { return values.iterator();                                 }
    public Stream<Integer>   stream()        { return StreamSupport.stream(spliterator(), false);}
    @Override
    public String            toString()      { return values.toString();                                 }
    @Override
    public int               hashCode()      { return values.hashCode();                                 }
    @Override
    @SuppressWarnings("unchecked")
    public boolean           equals(final Object other) {
        // note: an instanceof check is sufficient given that Domain is final
        return (other instanceof Domain ) && this.values.equals(((Domain)other).values);
    }

    // this should not be made public. it is only meant to be used in the core framework
    // to compute cartesian products
    /* package */ Set<Integer> toSet() {
        return values;
    }
}
