package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Domain;

import java.util.List;

@FunctionalInterface
public interface DomainFilter {

    Domain filter(final int variable, final List<Domain> domains);

}
