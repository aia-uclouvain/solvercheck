package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collector;

import static be.uclouvain.solvercheck.core.data.Operator.GT;
import static be.uclouvain.solvercheck.core.data.Operator.NE;
import static be.uclouvain.solvercheck.core.data.Operator.LT;

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
    public static Domain from(final Collection<Integer> values) {
        if( values instanceof Domain) {
            return (Domain) values;
        }

        switch (values.size()) {
            case 0: return EmptyDomain.getInstance();
            case 1: return new FixedDomain(values.stream().findFirst().get());
            default:
                return new BasicDomain(values);
        }
    }

    /**
     * Creates a new domain by removing from `dom` all the values that do not match
     * the restriction imposed by [op, value].
     *
     * @param dom   the domain to restrict
     * @param op    the operator used to impose some restriction on `dom`
     * @param value the value which imposes a restriction on `dom` in combination with `op`.
     * @return a domain corresponding to dom with all the values not matching [op, value] removed.
     */
    public static Domain restrict(final Domain dom, final Operator op, final int value) {
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

    /**
     * @return a collector that combines any given stream of integer into a Domain
     */
    public static Collector<Integer, ?, Domain> collector() {
        return Collector.of(
                ()          -> new HashSet<Integer>(),        // supplier aka "mutable container"
                (set, item) -> set.add(item),                 // accumulator function
                (s1, s2)    -> { s1.addAll(s2); return s1; }, // combiner (to handle parallel processing)
                (set)       -> from(set),                     // finisher (performs the final conversion)
                Collector.Characteristics.UNORDERED
        );
    }

    /** @return $dom \cap {value}$ */
    private static Domain filterEq(final Domain dom, final int value) {
        return dom.contains(value) ? from(value) : EmptyDomain.getInstance();
    }
    /** @return $dom \setminus {value}$ */
    private static Domain filterNe(final Domain dom, final int value) {
        return !dom.contains(value) ? dom : filterDefault(dom, NE, value);
    }

    /** @return ${ x | x \in dom \wedge x <= value}$ */
    private static Domain filterLe(final Domain dom, final int value) {
        return filterLt(dom, value+1);
    }
    /** @return ${ x | x \in dom \wedge x <  value}$ */
    private static Domain filterLt(final Domain dom, final int value) {
        if( dom.minimum() >= value ){
            return EmptyDomain.getInstance();
        }
        if( dom.maximum() < value ) {
            return dom;
        }
        return filterDefault(dom, LT, value);
    }

    /** @return ${ x | x \in dom \wedge x >= value}$ */
    private static Domain filterGe(final Domain dom, final int value) {
        return filterGt(dom, value-1);
    }
    /** @return ${ x | x \in dom \wedge x >  value}$ */
    private static Domain filterGt(final Domain dom, final int value) {
        if( dom.maximum() <= value ){
            return EmptyDomain.getInstance();
        }
        if( dom.minimum() > value ) {
            return dom;
        }
        return filterDefault(dom, GT, value);
    }

    /** @return ${ x | x \in dom \wedge x OP value}$ */
    private static Domain filterDefault(final Domain dom, final Operator op, final int value) {
        return dom.stream()
                .filter(x -> op.check(x, value))
                .collect(collector());
    }
}
