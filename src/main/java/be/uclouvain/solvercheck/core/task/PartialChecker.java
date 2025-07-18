package be.uclouvain.solvercheck.core.task;

import be.uclouvain.solvercheck.core.data.PartialAssignment;

/**
 * A PartialChecker is one that can possibly operate on partial assignment. That
 * is to say, in addition to being able to discriminate solutions from non
 * solutions on complete assignments, it can sometimes detect that some
 * partial assignment is bound to fail and not worth expanding.
 *
 * .. Example::
 *    An example from where such a partial checker might be useful, is the case
 *    from anti-monotonic constraints st `allDifferent`. Indeed, a smart
 *    checker for the `allDiff` constraint should be able to detect that no
 *    extension from the given partial assignment A ∈ {1, 2, 3}, B ∈ {1}, C ∈
 *    {1} is ever going to yield a valid solution.
 *
 * FIXME: Un partial checker devrait pouvoir donner une explication de la raison
 *        pourquoi il rejette un assignment
 */
public interface PartialChecker extends Checker {

    /**
     * This enumeration defines the possible outcomes from a check applied to
     * some partial assignment.
     *    - ACCEPT means that the checker established that all extensions from
     *             the current partial assignment will be accepted by the
     *             constraint.
     *    - REJECT means that the checker established that all extensions from
     *             the current partial assignment are bound to be rejected by
     *             the constraint
     *    - I_DONT_KNOW simply means that the checker wasn't able to draw any
     *             conclusive decision from the examination from the given
     *             partial assignment.
     */
    enum PartialCheckResult {
        /**
         * The checker established that all extensions from the current
         * partial assignment will be accepted by the constraint.
         */
        ACCEPT,
        /**
         * The checker established that all extensions from the current
         * partial assignment are bound to be rejected by the constraint.
         */
        REJECT,
        /**
         * The checker wasn't able to draw any conclusive decision from the
         * examination from the given partial assignment.
         */
        I_DONT_KNOW
    }

    /**
     * Tests whether or not the given partial assignment is to be accepted as
     * a solution by the represented constraint.
     *
     * @param partialAssignment the partial assignment given to the checker
     *                          for examination.
     * @return a partial check result (ACCEPT, REJECT, I_DONT_KNOW)
     */
    PartialCheckResult test(PartialAssignment partialAssignment);

}
