package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.DomainFilter;

import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.shrinkBounds;
import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.boundSupport;

/**
 * This class lets an user build a Bound(Z) consistent Filter from some
 * given Checker. This means that domains will be filtered to only contain
 * values between the bounds that actually belong to some **bound support** of the constraint.
 */
public class BoundZConsistency extends AbstractUniformConsistency {
    /** creates a new instance based on the given checker */
    public BoundZConsistency(final Checker checker) {
        super(domainFilter(checker));
    }

    /**
     * This is a convenience method which can be used to build hybrid consistencies.
     * It returns a domain filter that ensures that both the lower and upper bound of the
     * filtered domain have a **bound support** in the domain of the other variables.
     *
     * @param checker the checker testing the satisfaction of the constraint
     * @return a DomainFilter that ensures the Bound(Z) consistency of the given checker for some
     *         variable domain.
     */
    public static DomainFilter domainFilter(final Checker checker) {
        return (var, domains) -> shrinkBounds(var, domains.get(var), checker, boundSupport(domains));
    }
}
