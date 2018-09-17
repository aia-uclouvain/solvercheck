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

/**
 * This class provides some utility method that helps the writing of consistency
 * filters. Among other, it provides implementation of what a support/bound
 * support can be given an existing list of domains.
 *
 * Additionally, it provides features to test the existence of a form of support
 * for some combination of variable-values when testing the consistency of
 * some checker.
 */
public final class ConsistencyUtil {

    /** An utility class should have no public constructor. */
    private ConsistencyUtil() { }

    /**
     * @param domains the domains for which the lists all possible supports need
     *                to be computed
     * @return the set of all possible supports that can be generated
     * (brute-forced!) from the given list of domains.
     */
    public static Set<List<Integer>> support(final List<Domain> domains) {
        return CartesianProduct.of(domains);
    }

    /**
     * @param domains the domains for which the lists all possible
     *                **bound** supports need to be computed
     * @return the set of all possible bound-supports that can be generated
     * (brute-forced!) from the given list of domains
     */
    public static Set<List<Integer>> boundSupport(final List<Domain> domains) {
        return CartesianProduct.of(
                domains.stream()
                    .map(dom -> Range.between(dom.minimum(), dom.maximum()))
                    .collect(Collectors.toList())
        );
    }

    /**
     * This method produces a new domain corresponding to the domain `dom`
     * filtered to only contain values between bounds having some support in
     * the given context.
     *
     * @param var the variable whose bounds must have a support in the given
     *            context
     * @param dom the domain to filter
     * @param checker the checker testing the acceptability of some assignment
     * @param context the candidate support (typically: a plain support or bound
     *                support)
     * @return a new domain corresponding to the domain `dom` filtered to only
     * contain values between bounds having some support in the given context.
     */
    public static Domain shrinkBounds(
            final int var,
            final Domain dom,
            final Checker checker,
            final Collection<List<Integer>> context) {

        // crop falsifying lower bounds
        Domain withCorrectLB = dom.increasingStream()
                .dropWhile(value ->
                        !exists(context)
                            .satisfying(checker)
                            .forVariable(var)
                            .assignedTo(value)
                ).collect(Domain.collector());

        // crop falsifying upper bounds
        Domain withCorrectUB = withCorrectLB.decreasingStream()
                .dropWhile(value ->
                        !exists(context)
                            .satisfying(checker)
                            .forVariable(var)
                            .assignedTo(value)
                ).collect(Domain.collector());

        return withCorrectUB;
    }

    /**
     * This method provides an entry point into the micro DSL meant to test the
     * existence of some support for a comination of variable-value in some
     * context and for some given checker.
     *
     * @param context the kind of support (bound, plain) whose existence is
     *                to be proven for some var-value combination.
     * @return a builder that lets us cleanly build the existence predicate
     */
    public static ExistsSupport exists(final Collection<List<Integer>> context) {
        return new ExistsSupport(context);
    }

    /**
     * The builder class backing the 'exists' micro DSL.
     */
    public static final class ExistsSupport {
        /** The 'set' of all possible candidate 'support'. */
        private final Collection<List<Integer>> candidates;
        /**
         * The checker used to discriminate between valid solutions and
         * invalid ones.
         */
        private Checker checker;

        /** The variable whose values are to be tested. */
        private int variable;

        /**
         * Entry point into the DSL.
         *
         * @param candidates the candidates support that need to be searched
         *                   through
         */
        ExistsSupport(final Collection<List<Integer>> candidates) {
            this.candidates = candidates;
            this.checker    = null;
        }

        /**
         * Tells the constraint that must be satisfied by the support.
         *
         * @param checker the checker used to test the possible supports
         * @return this
         */
        @SuppressWarnings("checkstyle:hiddenfield")
        public ExistsSupport satisfying(final Checker checker) {
            this.checker = checker;
            return this;
        }
        /**
         * Tells the variable that must be have a support for some value.
         *
         * @param variable the variable for which a support is to be found
         * @return this
         */
        @SuppressWarnings("checkstyle:hiddenfield")
        public ExistsSupport forVariable(final int variable) {
            this.variable = variable;
            return this;
        }
        /**
         * @param value the value from the domain of `variable` for which a
         *              support is to be found.
         * @return true iff there exists at least one support $\tau$ in the set
         * of candidate for which the $\tau[var] = value$ and
         * $checker(\tau) = \top$
         */
        public boolean assignedTo(final int value) {
            return candidates.stream()
                    .anyMatch(support ->
                        support.get(variable).equals(value)
                    &&  checker.test(Assignment.from(support))
            );
        }
    }
}
