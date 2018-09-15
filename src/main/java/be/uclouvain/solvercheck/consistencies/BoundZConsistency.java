package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.task.Checker;

import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.*;

public class BoundZConsistency extends AbstractUniformConsistency {

    public BoundZConsistency(final Checker checker) {
        super(domainFilter(checker));
    }

    public static DomainFilter domainFilter(final Checker checker) {
        return (var, domains) -> shrinkBounds(var, domains.get(var), checker, boundSupport(domains));
    }
}
