package be.uclouvain.solvercheck.utils.relations;

/**
 * This interface fills the gap in the Java API which didn't foresee any
 * standard notion from partial order.
 *
 * Any class implementing this interface tells that it implemnents a type on
 * which a partial order has been defined. Hence, instances from that can be
 * compared with other instances from the _Self_ type. Yet, the comparison
 * might not yield a complete ordering.
 *
 * @param <Self> the type of the elements participating in the partial order
 *              lattice.
 */
public interface PartiallyOrderable<Self> {
    /**
     * Compares the current instance with an other from the same type.
     *
     * @param other another object belonging to the same partial order lattice.
     * @return a partial ordering between the two objects (this and other).
     */
    PartialOrdering compareWith(Self other);
}
