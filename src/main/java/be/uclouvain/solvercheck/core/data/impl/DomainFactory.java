package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collector;

import static be.uclouvain.solvercheck.core.data.Operator.GT;
import static be.uclouvain.solvercheck.core.data.Operator.LT;
import static be.uclouvain.solvercheck.core.data.Operator.NE;

/**
 * The point of this factory is to create domain instances, potentially using
 * specialized implementations.
 */
public final class DomainFactory {
    /** An utility class has no public constructor. */
    private DomainFactory() { }

    /**
     * Creates a new domain comprising the given values.
     *
     * @param values the values composing the domain
     * @return a new domain composed of exactly the set of values given as input
     * parameters
     */
    public static Domain from(final int...values) {
        switch (values.length) {
            case 0: return EmptyDomain.getInstance();
            case 1: return new FixedDomain(values[0]);
            default:
                return new BasicDomain(values);
        }
    }

    /**
     * Creates a new domain comprising the given values, when the values are
     * given as a collection.
     *
     * @param values the values composing the domain
     * @return a new domain composed of exactly the set of values given as input
     * parameters
     */
    public static Domain from(final Collection<Integer> values) {
        if (values instanceof Domain) {
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
     * Creates a new domain by removing from `dom` all the values that do not
     * match the restriction imposed by [op, value].
     *
     * @param dom   the domain to restrict
     * @param op    the operator used to impose some restriction on `dom`
     * @param value the value which imposes a restriction on `dom` in
     *              combination with `op`.
     * @return a domain corresponding to dom with all the values not matching
     * [op, value] removed.
     */
    public static Domain restrict(
            final Domain dom,
            final Operator op,
            final int value) {

        if (dom.isEmpty()) {
            return dom;
        }

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
                return filterGt(dom, value);
            default:
                throw new RuntimeException("Unreachable code");
        }
    }

    /**
     * @return a collector that combines any given stream of integer into a
     * Domain
     */
    @SuppressWarnings("checkstyle:leftcurly")
    public static Collector<Integer, ?, Domain> collector() {
        return Collector.of(
                ()          -> new HashSet<Integer>(),
                (set, item) -> set.add(item),
                (s1, s2)    -> { s1.addAll(s2); return s1; },
                (set)       -> from(set),
                Collector.Characteristics.UNORDERED
        );
    }

    /**
     * @param dom the domain that needs to be filtered to contain only `value`
     * @param value the only value we want to keep in the filtered domain
     *              (iff it was present in `dom`)
     * @return $dom \cap {value}$
     */
    private static Domain filterEq(final Domain dom, final int value) {
        if (dom.contains(value)) {
            return from(value);
        } else {
            return EmptyDomain.getInstance();
        }
    }
    /**
     * @param dom the domain that needs to be filtered.
     * @param value the only value we want to remove from `dom` in the filtered
     *              domain
     * @return $dom \setminus {value}$ */
    private static Domain filterNe(final Domain dom, final int value) {
        if (!dom.contains(value)) {
            return dom;
        } else {
             return filterDefault(dom, NE, value);
        }
    }

    /**
     * Implements a specialized filtering which keeps only values of `dom`
     * having a value lesser or equal to `value`.
     *
     * @param dom the domain that needs to be filtered.
     * @param value the value we want to use a new upper bound in the
     *              filtered domain.
     * @return ${ x | x \in dom \wedge x <= value}$
     */
    private static Domain filterLe(final Domain dom, final int value) {
        if (value == Integer.MAX_VALUE) {
            return dom;
        } else {
            return filterLt(dom, value + 1);
        }
    }
    /**
     * Implements a specialized filtering which keeps only values of `dom`
     * having a value stricly lower than `value`.
     *
     * @param dom the domain that needs to be filtered.
     * @param value the value we want to use a new (excluded) upper bound in the
     *              filtered domain.
     * @return ${ x | x \in dom \wedge x <  value}$
     */
    private static Domain filterLt(final Domain dom, final int value) {
        if (dom.minimum() >= value) {
            return EmptyDomain.getInstance();
        }
        if (dom.maximum() < value) {
            return dom;
        }
        return filterDefault(dom, LT, value);
    }

    /**
     * Implements a specialized filtering which keeps only values of `dom`
     * having a value greater or equal to `value`.
     *
     * @param dom the domain that needs to be filtered.
     * @param value the value we want to use a new lower bound in the
     *              filtered domain.
     * @return ${ x | x \in dom \wedge x >= value}$
     */
    private static Domain filterGe(final Domain dom, final int value) {
        if (value == Integer.MIN_VALUE) {
            return dom;
        } else {
            return filterGt(dom, value - 1);
        }
    }
    /**
     * Implements a specialized filtering which keeps only values of `dom`
     * having a value stricly greater than `value`.
     *
     * @param dom the domain that needs to be filtered.
     * @param value the value we want to use a new (excluded) lower bound in the
     *              filtered domain.
     * @return ${ x | x \in dom \wedge x >  value}$
     */
    private static Domain filterGt(final Domain dom, final int value) {
        if (dom.maximum() <= value) {
            return EmptyDomain.getInstance();
        }
        if (dom.minimum() > value) {
            return dom;
        }
        return filterDefault(dom, GT, value);
    }

    /**
     * Implements a default domain filtering.
     *
     * @param dom the domain to filter according to `op` and value
     * @param op the operator to use when filtering the domain
     * @param value the value which combined with op imposes a restriction on
     *             the values that can remain in the filtered domain.
     * @return ${ x | x \in dom \wedge x OP value}$
     */
    private static Domain filterDefault(
            final Domain dom,
            final Operator op,
            final int value) {

        return dom.stream()
                .filter(x -> op.check(x, value))
                .collect(collector());
    }
}
