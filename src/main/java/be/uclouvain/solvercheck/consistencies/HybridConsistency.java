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

public class HybridConsistency implements Filter {
    private final DomainFilter[] domainFilter;

    public HybridConsistency(final Checker checker, final Function<Checker, DomainFilter>...domainFilter) {
        this.domainFilter = Arrays.stream(domainFilter)
                .map(x->x.apply(checker))
                .toArray(DomainFilter[]::new);
    }

    @Override
    public PartialAssignment filter(PartialAssignment partialAssignment) {
        if(domainFilter.length != partialAssignment.size()) {
            throw new IllegalArgumentException(String.format(
                    "The given partial assignment has an arity of %d, which differs from the expected %d",
                    partialAssignment.size(), domainFilter.length));
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
