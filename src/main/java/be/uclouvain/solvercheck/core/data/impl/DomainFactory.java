package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;

import java.util.Collection;
import java.util.List;

import static be.uclouvain.solvercheck.core.data.Operator.*;
import static java.util.stream.Collectors.toList;

/** The point of this factory is to create domain instances, potentially using specialized implementations */
public final class DomainFactory {
    /** An utility class has no public constructor */
    private DomainFactory() {}

    /** Creates a new domain comprising the given values */
    public static Domain from(final int...values) {
        switch (values.length) {
            case 0: return EmptyDomain.getInstance();
            case 1: return new FixedDomain(values[0]);
            default:
                return new BasicDomain(values);
        }
    }

    /** Creates a new domain comprising the given values, when the values are given as a collection */
    public static Domain fromCollection(final Collection<Integer> values) {
        return from(values.stream().mapToInt(Integer::intValue).toArray());
    }

    /**
     * Creates a new domain by removing from `dom` all the values that do not match
     * the restriction imposed by [op, value].
     *
     * .. Note::
     *    Whenever applicable, this method will strive to return the same instance from `dom`
     *    that was given as an argument.
     *
     * @param dom   the domain to restrict
     * @param op    the operator used to impose some restriction on `dom`
     * @param value the value which imposes a restriction on `dom` in combination with `op`.
     * @return a domain corresponding to dom with all the values not matching [op, value] removed.
     */
    public static Domain restrict(final Domain dom, final Operator op, final int value) {
        Collection<Integer> values = filter(dom, op, value);

        if( values instanceof Domain) {
            return (Domain) values;
        }
        if( values.contains(dom) ) {
            return dom;
        }

        return fromCollection(values);
    }

    /**
     * Filters the values from the domain according to the given restriction.
     *
     * .. Note::
     *    This method should strive to (but is not forced to) return a Domain whenever it is
     *    obvious.
     */
    private static Collection<Integer> filter(final Domain dom, final Operator op, final int value) {
        switch (op) {
            case EQ:
                return filterEq(dom, value);
            case NE:
                return filterNe(dom, value);
            case LE:
                return filterLe(dom, value);
            case LT:
                return filterLt(dom, value);
            case GE:
                return filterGe(dom, value);
            case GT:
                return filterLt(dom, value);
            default:
                throw new RuntimeException("Unreachable code");
        }
    }
    /** @return $dom \cap {value}$ */
    private static Collection<Integer> filterEq(final Domain dom, final int value) {
        return dom.contains(value) ? List.of(value) : EmptyDomain.getInstance();
    }
    /** @return $dom \setminus {value}$ */
    private static Collection<Integer> filterNe(final Domain dom, final int value) {
        return !dom.contains(value) ? dom : filterDefault(dom, NE, value);
    }

    /** @return ${ x | x \in dom \wedge x <= value}$ */
    private static Collection<Integer> filterLe(final Domain dom, final int value) {
        return filterLt(dom, value+1);
    }
    /** @return ${ x | x \in dom \wedge x <  value}$ */
    private static Collection<Integer> filterLt(final Domain dom, final int value) {
        if( dom.minimum() >= value ){
            return EmptyDomain.getInstance();
        }
        if( dom.maximum() < value ) {
            return dom;
        }
        return filterDefault(dom, LT, value);
    }

    /** @return ${ x | x \in dom \wedge x >= value}$ */
    private static Collection<Integer> filterGe(final Domain dom, final int value) {
        return filterGt(dom, value-1);
    }
    /** @return ${ x | x \in dom \wedge x >  value}$ */
    private static Collection<Integer> filterGt(final Domain dom, final int value) {
        if( dom.maximum() <= value ){
            return EmptyDomain.getInstance();
        }
        if( dom.minimum() > value ) {
            return dom;
        }
        return filterDefault(dom, GT, value);
    }

    /** @return ${ x | x \in dom \wedge x OP value}$ */
    private static Collection<Integer> filterDefault(final Domain dom, final Operator op, final int value) {
        return dom.stream()
                .filter(x -> op.check(x, value))
                .collect(toList());
    }
}
