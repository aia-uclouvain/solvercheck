package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.task.Checker;

import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.boundSupport;
import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.exists;

public class RangeConsistency extends AbstractUniformConsistency {

    public RangeConsistency(final Checker checker) {
        super(domainFilter(checker));
    }

    public static DomainFilter domainFilter(final Checker checker) {
        return (var, domains) ->
                domains.get(var)
                    .stream()
                    .filter(value ->
                        exists(boundSupport(domains)).satisfying(checker).forVariable(var).assignedTo(value) )
                    .collect(Domain.collector());
    }

}
