package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.DomainFilter;
import be.uclouvain.solvercheck.core.task.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * An hybrid consistency is one that does not uniformly applies the same
 * DomainFilter to all of the variables. Instead, the HybridConsistency class
 * lets you specify what domain filter to apply for each variable.
 *
 * Just like any other consistency algorithm, this class lets you build a Filter
 * from some given checker and computes the filtered partial assignment as
 * the least fixpoint of the domains filtered by the DomainFilters.
 */
public final class HybridConsistency implements Filter {
    /** The domain filters used for all the variables. */
    private final DomainFilter[] domainFilter;

    /**
     * Creates a new hybrid consistency from some given checker and a list of
     * function producing domain filters.
     *
     * .. Note::
     *    The given list of function producing the domain filters **must** have
     *    the same length as the arity of the constraint being checked. Any
     *    failure to comply with this rule will result in the hybrid
     *    consistency filter throwing an exception.
     *
     * @param checker the checker embodying the constraint being verified
     * @param domainFilter a vararg list of function that can be used to produce
     *                    domain filters from the given checker.
     */
    @SafeVarargs
    public HybridConsistency(
            final Checker checker,
            final Function<Checker, DomainFilter>...domainFilter) {

        this.domainFilter = Arrays.stream(domainFilter)
                .map(x -> x.apply(checker))
                .toArray(DomainFilter[]::new);
    }

    /** {@inheritDoc} */
    @Override
    public PartialAssignment filter(
            final PartialAssignment partialAssignment) {

        if (domainFilter.length != partialAssignment.size()) {
            throw new IllegalArgumentException(String.format(
                    "The given partial assignment has an arity of %d, "
                  + "which differs from the expected %d",
                    partialAssignment.size(),
                    domainFilter.length));
        }

        if (partialAssignment.isError()) {
            return noSolution(partialAssignment);
        }

        // Make a temporary, modifiable version of the partial assignment
        List<Domain> domains = new ArrayList<>(partialAssignment);

        boolean fixpoint = false;

        while (!fixpoint) {
            fixpoint = true;

            for (int i = 0; i < domains.size(); i++) {
                Domain reduced = domainFilter[i].filter(i, domains);

                if (reduced.isEmpty()) {
                    return noSolution(partialAssignment);
                }
                if (!reduced.equals(domains.get(i))) {
                    domains.set(i, reduced);
                    fixpoint = false;
                }
            }
        }

        return PartialAssignment.from(domains);
    }

    /**
     * @param original the original partial assignment that was shown to hold
     *                 no acceptable solution according to this level of
     *                 consistency.
     * @return a partial assignment having all domains cleared.
     *      This method can be used to signify that the given partial assignment
     *      is a dead end and, any of its extensions should be rejected.
     */
    private PartialAssignment noSolution(final PartialAssignment original) {
        return original.stream()
                .map(x -> Domain.emptyDomain())
                .collect(PartialAssignment.collector());
    }
}
