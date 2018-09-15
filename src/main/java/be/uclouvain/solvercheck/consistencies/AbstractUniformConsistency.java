package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Filter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUniformConsistency implements Filter {
    private final DomainFilter domainFilter;

    public AbstractUniformConsistency(final DomainFilter domainFilter) {
        this.domainFilter = domainFilter;
    }

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
                    return PartialAssignment.from(domains);
                }
                if (!reduced.equals(domains.get(i))) {
                    domains.set(i, reduced);
                    fixpoint = false;
                }
            }
        }

        return PartialAssignment.from(domains);
    }
}
