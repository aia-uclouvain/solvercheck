package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.DomainFilter;
import be.uclouvain.solvercheck.core.task.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class can serve as a base class for all the filters willing to implement an 'uniform'
 * consistency. That is to say, for all the classes willing to implement a consistency filter;
 * filtering the domains of **all** variables according to one same variable filtering policy.
 *
 * Notable examples extending this class: {@see BoundDConsistency}, {@see BoundZConsistency}
 * and {@see RangeConsistency}. For efficiency reasons, the `ArcConsistency` was **not**
 * implemented as a subclass of `AbstractUniformConsistency`.
 */
public abstract class AbstractUniformConsistency implements Filter {
    /** The single variable filtering policy that must apply to all the variables */
    private final DomainFilter domainFilter;

    /** Creates a new instance */
    public AbstractUniformConsistency(final DomainFilter domainFilter) {
        this.domainFilter = domainFilter;
    }

    /**
     * {@inheritDoc}
     *
     * Filters the domains of the variables of the given partial assignment until the
     * least fixpoint has been reached (one additional application of the domain filtering
     * will not prune any additional value).
     */
    @Override
    public PartialAssignment filter(PartialAssignment partialAssignment) {
        // Make a temporary, modifiable version of the partial assignment
        List<Domain> domains = new ArrayList<>(partialAssignment);

        boolean fixpoint = false;

        while (!fixpoint) {
            fixpoint = true;

            for (int i = 0; i < domains.size(); i++) {
                Domain reduced = domainFilter.filter(i, domains);

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
     * @returns a partial assignment having all domains cleared.
     *      This method can be used to signify that the given partial assignment is a dead end and, any
     *      of its extensions should be rejected.
     */
    private PartialAssignment noSolution(final PartialAssignment original){
        return original.stream()
                .map(x-> Domain.emptyDomain())
                .collect(PartialAssignment.collector());
    }
}
