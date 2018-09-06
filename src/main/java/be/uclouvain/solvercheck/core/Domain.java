package be.uclouvain.solvercheck.core;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.core.StrengthComparison.*;

/**
 * This class implements the core idea of what a cp domain should be: a set of values for some given type.
 *
 * From a technical point-of-view, this type should be considered an application of the 'newtype' pattern.
 * It gives a semantically richer meaning to sets of T's while adding some benefits at the same time
 * (ie: cartesian product, partial order comparison).
 *
 * @param <T> the type of the values of the domain.
 */
public final class Domain<T> implements Iterable<T> {
    private final Set<T> values;

    public Domain(final Set<T> values) {
        this.values = values;
    }

    /*
     * Note:
     * I am hereby explicitly avoiding to implement the Comparable as the latter imposes a total ordering,
     * which does not apply do domains.
     */
    public StrengthComparison compareWith(final Domain<T> that) {
        if ( this.size() < that.size() ) {
            return that.values.contains(this.values) ? STRONGER   : INCOMPARABLE;
        }
        else if ( this.size() > that.size() ) {
            return this.values.contains(that.values) ? WEAKER     : INCOMPARABLE;
        }
        else {
            return this.values.equals(that.values)   ? EQUIVALENT : INCOMPARABLE;
        }
    }


    public int         size()     { return values.size();      }
    public boolean     isEmpty()  { return size() == 0;        }
    public boolean     isFalse()  { return isEmpty();          }
    public boolean     isFixed()  { return size() == 1;        }
    public Domain<T>   remove(final T val) {
        if( !values.contains(val) ) {
            return this;
        } else {
            return new Domain<T>(values.stream()
                                        .filter (x -> !x.equals(val))
                                        .collect(Collectors.toSet()));
        }
    }

    @Override
    public Iterator<T> iterator() { return values.iterator();  }
    @Override
    public String      toString() { return values.toString();  }
    @Override
    public int         hashCode() { return values.hashCode();  }
    @Override
    @SuppressWarnings("unchecked")
    public boolean     equals(final Object o) {
        // note: an instanceof check is sufficient given that Domain is final
        return (o instanceof Domain ) && this.values.equals(((Domain<T>)o).values);
    }

    // this should not be made public. it is only meant to be used in the core framework
    // to compute cartesian products
    Set<T> toSet() {
        return values;
    }
}
