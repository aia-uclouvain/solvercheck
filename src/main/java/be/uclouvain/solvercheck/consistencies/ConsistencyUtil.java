package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import be.uclouvain.solvercheck.utils.collections.Range;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConsistencyUtil {

    public static Set<List<Integer>> support(final List<Domain> domains) {
        return CartesianProduct.of(domains);
    }

    public static Set<List<Integer>> boundSupport(final List<Domain> domains) {
        return CartesianProduct.of(
                domains.stream()
                        .map(domain -> Range.between(domain.minimum(), domain.maximum()))
                        .collect(Collectors.toList())
        );
    }

    public static Domain shrinkBounds(final int var, final Domain dom, final Checker checker, final Collection<List<Integer>> context) {
        // crop falsifying lower bounds
        Domain withCorrectLB = dom.increasingStream()
                .dropWhile( value ->
                        !exists(context).satisfying(checker).forVariable(var).assignedTo(value)
                ).collect(Domain.collector());

        // crop falsifying upper bounds
        Domain withCorrectUB = withCorrectLB.decreasingStream()
                .dropWhile( value ->
                        !exists(context).satisfying(checker).forVariable(var).assignedTo(value)
                ).collect(Domain.collector());

        return withCorrectUB;
    }

    public static ExistsSupport exists(final Collection<List<Integer>> supportType) {
        return new ExistsSupport(supportType);
    }

    public static class ExistsSupport {
        private Collection<List<Integer>> candidates;
        private Checker checker;
        private int variable;

        public ExistsSupport(final Collection<List<Integer>> candidates) {
            this.candidates = candidates;
            this.checker    = null;
        }
        public ExistsSupport satisfying(final Checker checker) {
            this.checker = checker;
            return this;
        }
        public ExistsSupport forVariable(final int variable) {
            this.variable = variable;
            return this;
        }
        public boolean assignedTo(final int value) {
            return candidates.stream().anyMatch(support ->
                        support.get(variable).equals(value) && checker.test(Assignment.from(support))
            );
        }
    }
}
