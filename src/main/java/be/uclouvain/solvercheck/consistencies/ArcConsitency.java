package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;

import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.exists;
import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.support;

public final class ArcConsitency implements Filter {
    private final Checker checker;

    public ArcConsitency(final Checker checker) {
        this.checker = checker;
    }

    @Override
    public PartialAssignment filter(PartialAssignment partial) {
        return PartialAssignment.unionOf(
                CartesianProduct.of(partial).stream()
                     .map(Assignment::from)
                     .filter(checker)
                     .collect(Collectors.toList())
        );
    }

    public static DomainFilter domainFilter(final Checker checker) {
        return (var, context) ->
                context.get(var)
                    .stream()
                    .filter(value ->
                        exists(support(context)).satisfying(checker).forVariable(var).assignedTo(value) )
                    .collect(Domain.collector());
    }
}
