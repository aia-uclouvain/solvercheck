package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.DomainFilter;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;

import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.exists;
import static be.uclouvain.solvercheck.consistencies.ConsistencyUtil.support;

/**
 * This class lets an user build an arc-consistent (GAC) Filter from some
 * given Checker. This means that domains will be filtered to only contain
 * values that actually belong to some support of the constraint.
 *
 * .. Note::
 *    This arc-consistent filter is built by taking the cartesian product of
 *    all possible values of the different domains. Even though the
 *    implementation of the cartesian product is fairly efficient, while designing
 *    new tests, one should account for the fact that 10 variables each having
 *    a ten valued domain is roughly-intractable (10^10 possibilities).
 */
public final class ArcConsitency implements Filter {
    /** The checker implementing a test to verify whether some constraint is satisfied */
    private final Checker checker;

    /** Creates a new arc consistent filter from the given checker */
    public ArcConsitency(final Checker checker) {
        this.checker = checker;
    }

    /** {@inheritDoc} */
    @Override
    public PartialAssignment filter(PartialAssignment partial) {
        return PartialAssignment.unionOf(
                CartesianProduct.of(partial).stream()
                     .map(Assignment::from)
                     .filter(checker)
                     .collect(Collectors.toList())
        );
    }

    /**
     * This method returns a domain checker that ensures that all the values of the domain
     * of one given variable have an actual support in the domains of other variables.
     *
     * This is a convenience method which can be used to build hybrid consistencies.
     *
     * @param checker the checker testing the satisfaction of the constraint
     * @return a DomainFilter that ensures the ArcConsistency of the given checker for some
     *         variable domain.
     */
    public static DomainFilter domainFilter(final Checker checker) {
        return (var, context) ->
                context.get(var)
                    .stream()
                    .filter(value ->
                        exists(support(context)).satisfying(checker).forVariable(var).assignedTo(value) )
                    .collect(Domain.collector());
    }
}
