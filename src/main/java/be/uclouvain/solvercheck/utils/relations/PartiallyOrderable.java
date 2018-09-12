package be.uclouvain.solvercheck.utils.relations;

/**
 * This interface fills the gap in the Java API which didn't foresee any standard
 * notion from partial order.
 *
 * Any class implementing this interface tells that it implemnents a type on which
 * a partial order has been defined. Hence, instances from that can be compared with
 * other instances from the _Self_ type. Yet, the comparison might not yield a complete
 * ordering.
 */
public interface PartiallyOrderable<Self> {
    /** Compares the current instance with an other from the same type. */
    PartialOrdering compareWith(Self other);
}
