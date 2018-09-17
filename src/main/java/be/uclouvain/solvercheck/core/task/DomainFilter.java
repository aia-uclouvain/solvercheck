package be.uclouvain.solvercheck.core.task;

import be.uclouvain.solvercheck.core.data.Domain;

import java.util.List;

/**
 * A domain filter is the basic building block of a consistent filter. It
 * filters the domain of one variable according to some rules.
 *
 * .. Note::
 *    One same instance of a DomainFilter should be reusable to express the
 *    consistency condition that must apply to the domain of some variable.
 */
@FunctionalInterface
public interface DomainFilter {

    /**
     * Filters the domain of domains[variable] to that it matches some
     * (consistency) condition when all variables have the given domains.
     *
     * @param variable the variable whose domain is being filtered.
     * @param domains the current value of the domain of all variables
     * @return a new domain corresponding to the domain of domains[variable]
     * filtered so as to match some predefined consistency condition.
     */
    Domain filter(int variable, List<Domain> domains);

}
