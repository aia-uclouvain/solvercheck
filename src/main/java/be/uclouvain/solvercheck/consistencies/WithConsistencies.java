package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.DomainFilter;
import be.uclouvain.solvercheck.core.task.Filter;

import java.util.function.Function;

/**
 * This interface collects all the useful methods that let you seamlessly plug
 * SolverCheck's consistency API into your code.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithConsistencies {
    /**
     * Lets an user build an arc-consistent (GAC) Filter from some
     * given Checker. This means that domains will be filtered to only contain
     * values that actually belong to some support of the constraint.
     *
     * @param checker the checker backing the desired AC Filter
     * @return an AC filter that implements the given `checker` constraint.
     */
    default Filter arcConsistent(final Checker checker) {
        return new ArcConsitency(checker);
    }
    /**
     * lets an user build a Bound(D) consistent Filter from some given
     * Checker. This means that domains will be filtered to only contain values
     * between the bounds that actually belong to some support of the
     * constraint.
     *
     * @param checker the checker backing the desired Bound(D) Filter
     * @return a Bound(D) filter that implements the given `checker` constraint.
     */
    default Filter boundDConsistent(final Checker checker) {
        return new BoundDConsistency(checker);
    }
    /**
     * lets an user build a Bound(Z) consistent Filter from some given
     * Checker. This means that domains will be filtered to only contain values
     * between the bounds that actually belong to some bound support of the
     * constraint.
     *
     * @param checker the checker backing the desired Bound(Z) Filter
     * @return a Bound(Z) filter that implements the given `checker` constraint.
     */
    default Filter boundZConsistent(final Checker checker) {
        return new BoundZConsistency(checker);
    }

    /**
     * Lets an user build a Range consistent Filter from some given
     * Checker. This means that domains will be filtered to only contain values
     * belonging to some **bound support** of the constraint.
     *
     * @param checker the checker backing the desired range consistent Filter
     * @return a range consistent filter that implements the given `checker`
     * constraint.
     */
    default Filter rangeConsistent(final Checker checker) {
        return new RangeConsistency(checker);
    }

    /**
     * An hybrid consistency is one that does not uniformly applies the same
     * DomainFilter to all of the variables. Instead, the hybrid consistency
     * lets you specify what domain filter to apply for each variable.
     *
     * @param checker the checker backing the desired Filter of hybrid
     *                consistency
     * @param domainConsistencies functions producing the desired domain
     *                            consistencies when parameterized with
     *                            thev given `checker`. The domain
     *                            filterings are applied to variables in
     *                            their order of appearance.
     * @return a filter that implements the given `checker` with some user
     * specified hybrid consistency.
     */
    default Filter hybrid(
            final Checker checker,
            final Function<Checker, DomainFilter>... domainConsistencies) {
        return new HybridConsistency(checker, domainConsistencies);
    }

    /**
     * Arc Consistent domain filter: a domain filter that ensures that all the
     * values of the domain of some given variable have an actual support in
     * the domains of other variables.
     *
     * @return the arc consistency domain filter
     */
    default Function<Checker, DomainFilter> acDomain() {
        return ArcConsitency::domainFilter;
    }
    /**
     * Bound(D) Consistent domain filter: a domain filter that ensures ensures
     * that both the lower and upper bound of the filtered domain have a
     * support in the domain of the other variables.
     *
     * @return the Bound(D) consistency domain filter
     */
    default Function<Checker, DomainFilter> bcDDomain() {
        return BoundDConsistency::domainFilter;
    }
    /**
     * Bound(Z) consistent Domain Filter from some given Checker. This means
     * that domains will be filtered to only contain values between the bounds
     * that actually belong to some **bound support** of the constraint.
     *
     * @return the Bound(Z) consistency domain filter
     */
    default Function<Checker, DomainFilter> bcZDomain() {
        return BoundZConsistency::domainFilter;
    }
    /**
     * Range consistent Domain Filter from some given Checker. This means
     * that domains will be filtered to only contain values belonging to some
     * **bound support** of the constraint.
     *
     * @return the range consistency domain filter
     */
    default Function<Checker, DomainFilter> rangeDomain() {
        return BoundDConsistency::domainFilter;
    }
}
