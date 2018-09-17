package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.DomainFilter;

import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.boundSupport;
import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.exists;

/**
 * This class lets an user build a Range consistent Filter from some given
 * Checker. This means that domains will be filtered to only contain values
 * belonging to some **bound support** of the constraint.
 */
public final class RangeConsistency extends AbstractUniformConsistency {

    /**
     * Creates a new instance based on the given checker.
     *
     * @param checker the predicate testing whether an assignment satisfies some
     *                given constraint.
     */
    public RangeConsistency(final Checker checker) {
        super(domainFilter(checker));
    }

    /**
     * This is a convenience method which can be used to build hybrid
     * consistencies. It returns a domain filter that ensures that all values
     * of the filtered domain have a **bound support** in the domain of the
     * other variables.
     *
     * @param checker the checker testing the satisfaction of the constraint
     * @return a DomainFilter that ensures the Range consitency of the given
     * checker for some variable domain.
     */
    public static DomainFilter domainFilter(final Checker checker) {
        return (var, domains) ->
                domains.get(var)
                    .stream()
                    .filter(value ->
                        exists(boundSupport(domains))
                            .satisfying(checker)
                            .forVariable(var)
                            .assignedTo(value))
                    .collect(Domain.collector());
    }

}
