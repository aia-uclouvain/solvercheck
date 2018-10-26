package be.uclouvain.solvercheck.assertions.stateless;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import be.uclouvain.solvercheck.utils.relations.PartialOrdering;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.WEAKER;

/**
 * This class acts as a stateless property factory. It lets you create basic
 * predicates (bearing on partial assignment) applicable for some given
 * `Filter`.
 */
public final class StatelessProperties {

    /**
     * A 'property' is nothing but a predicate on partial assignment.
     */
    @FunctionalInterface
    public interface Property extends Predicate<PartialAssignment> { }

    /** An utility class has no public constructor. */
    private StatelessProperties() { }

    /**
     * Checks that `actual` and `other` have equivalent propagating strengths
     * for the given domains test cases.
     *
     * @param actual the 1st filter operand of the comparison
     * @param other the 2nd filter operand of the comparison
     *
     * @return a property which is true iff the two filters have equivalent
     * propagation strengths.
     */
    public static Property equivalentTo(final Filter actual,
                                        final Filter other) {
        return domains -> {
            PartialOrdering comparison =
               actual.filter(domains).compareWith(other.filter(domains));

            return comparison == EQUIVALENT;
        };
    }

    /**
     * Checks that `actual` is weaker or equivalent to `other` for the given
     * domains test cases.
     *
     * @param actual the 1st filter operand of the comparison
     * @param other the 2nd filter operand of the comparison
     *
     * @return a property which is true iff the actual has a propagation
     * strength weaker or equivalent to that of other.
     */
    public static Property weakerThan(final Filter actual,
                                      final Filter other) {
        return domains -> {
            final PartialOrdering comparison =
               actual.filter(domains).compareWith(other.filter(domains));

            return comparison == WEAKER || comparison == EQUIVALENT;
        };
    }

    /**
     * Checks that `actual` is weaker or equivalent to `other` for the given
     * domains test cases.
     *
     * @param actual the 1st filter operand of the comparison
     * @param other the 2nd filter operand of the comparison
     *
     * @return a property which is true iff the actual has a propagation
     * strength (always) weaker than that of other.
     */
    public static Property strictlyWeakerThan(final Filter actual,
                                              final Filter other) {
        return domains -> {
            final PartialOrdering comparison =
               actual.filter(domains).compareWith(other.filter(domains));

            return comparison == WEAKER;
        };
    }

    /**
     * Checks that `actual` is stronger or equivalent to `other` for the given
     * domains test cases.
     *
     * @param actual the 1st filter operand of the comparison
     * @param other the 2nd filter operand of the comparison
     *
     * @return a property which is true iff the actual has a propagation
     * strength stronger or equivalent to that of other.
     */
    public static Property strongerThan(final Filter actual,
                                        final Filter other) {
        return domains -> {
            final PartialOrdering comparison =
               actual.filter(domains).compareWith(other.filter(domains));

            return comparison == STRONGER || comparison == EQUIVALENT;
        };
    }

    /**
     * Checks that `actual` is stronger than `other` for the given domains
     * test cases.
     *
     * @param actual the 1st filter operand of the comparison
     * @param other the 2nd filter operand of the comparison
     *
     * @return a property which is true iff the actual has a propagation
     * strength (always) stronger than that of other.
     */
    public static Property strictlyStrongerThan(final Filter actual,
                                                final Filter other) {
        return domains -> {
            final PartialOrdering comparison =
               actual.filter(domains).compareWith(other.filter(domains));

            return comparison == STRONGER;
        };
    }

    /**
     * Returns true iff `filter` is a contracting propagator. That is to say,
     * iff
     *  $\forall d \in PartialAssignments : actual(d) \subseteq d$
     *
     * @param filter the tested propagator
     * @return a property which is true iff `filter` is a contracting function.
     */
    public static Property contracting(final Filter filter) {
        return domains -> {
            final PartialOrdering comparison =
               filter.filter(domains).compareWith(domains);
            return comparison == STRONGER || comparison == EQUIVALENT;
        };
    }

    /**
     * Returns true iff `actual` is an idempotent propagator. That is to say,
     * iff
     * $\forall d \in PartialAssignments : actual(actual(d)) = actual(d)$
     *
     * @param actual the tested filter
     * @return a property which is true iff `actual` is idempotent.
     */
    public static Property idempotent(final Filter actual) {
        return domains -> {
            PartialAssignment filteredOnce = actual.filter(domains);

            return actual.filter(filteredOnce).equals(filteredOnce);
        };
    }

    /**
     * Returns true iff `actual` is a weakly monotonic function. That is to
     * say iff
     * $\forall d \in PartialAssignment, \forall a \in d:
     *    actual({a}) \subseteq actual(d)$
     *
     * @param actual the tested filter
     * @return a property which is true iff `actual` is weakly monotonic.
     */
    public static Property weaklyMonotonic(final Filter actual) {
        return domains -> {
            // The actual filtered partial assignment. It serves as a
            // reference for the comparison with the other tuples.
            final PartialAssignment filtered =
               actual.filter(domains);

            // Filtered assignments must be stronger or equivalent to
            // filtered partial assignments.
            final Set<PartialOrdering> subseteq =
               Set.of(STRONGER, EQUIVALENT);

            // Maps a list of integer onto an assignment.
            final Function<List<Integer>, Assignment> asn =
               a -> a.stream().collect(Assignment.collector());

            // Maps an assignment onto a partial assignment (reverse
            // operation of `asAssignment()`/
            final Function<Assignment, PartialAssignment> partial =
               a -> a.stream().map(Domain::from).collect(PartialAssignment.collector());

            // Maps a list of integer into a partial assignment.
            final Function<List<Integer>, PartialAssignment> partialAsn =
               partial.compose(asn);

            // Checks the property for one single assignment.
            final Predicate<PartialAssignment> isWeakerThanActual =
               pa -> subseteq.contains(actual.filter(pa).compareWith(filtered));

            // The above check hold of all assignment. Hence we check all items
            // from the cartesian product of the domains.
            return CartesianProduct.of(domains).parallelStream()
               .map(partialAsn)
               .allMatch(isWeakerThanActual);
        };
    }
}
