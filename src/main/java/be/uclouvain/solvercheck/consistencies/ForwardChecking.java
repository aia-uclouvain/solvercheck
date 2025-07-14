package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;

import java.util.stream.Collectors;

/**
 * This class implements the consistency level reached by an algorithm
 * implementing `forward checking`. It turns any checker into a Filter with
 * FC consistency.
 *
 * <div>
 *     <h2>Note:</h2>
 *     The definition of forward checking as a consistency is given in the
 *     handbook of constraint programming at pages 63 -- 64.
 *
 *     Concretely, forward checking only filters partial assignment when all
 *     variables but one are assigned.
 * </div>
 */
public final class ForwardChecking implements Filter {
    /**
     * The checker implementing a test to verify whether some constraint
     * is satisfied.
     */
    private final Checker checker;

    /**
     * Creates a new arc consistent filter from the given checker.
     * @param checker the checker implementing a test to verify whether or not
     *                some constraint is satisfied
     */
    public ForwardChecking(final Checker checker) {
        this.checker = checker;
    }

    /** {@inheritDoc} */
    @Override
    public PartialAssignment filter(final PartialAssignment partial) {
        long nbUnassigned = partial.stream().filter(d -> d.size() > 1).count();

        if (nbUnassigned > 1) {
            // do nothing when there is more than one unassigned variable.
            return partial;
        } else {
            // else return all possible extensions of the fixed variables that
            // satisfy the constraint
            return PartialAssignment.unionOf(
               partial.size(),
               CartesianProduct.of(partial).stream()
                  .map(values -> Assignment.from(values))
                  .filter(checker)
                  .collect(Collectors.toList()));
        }
    }
}
