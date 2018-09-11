package be.uclouvain.solvercheck.core.task;

import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.function.Predicate;

/**
 * A PartialChecker is one that can possibly operate on partial assignment. That is
 * to say, in addition to being able to discriminate solutions from non solutions on
 * complete assignments, it can sometimes detect that some partial assignment is bound
 * to fail and not worth expanding.
 *
 * .. Example::
 *    An example of where such a partial checker might be useful, is the case of
 *    anti-monotonic constraints st `allDifferent`. Indeed, a smart checker for the
 *    `allDiff` constraint should be able to detect that no extension of the given
 *    partial assignment A ∈ {1, 2, 3}, B ∈ {1}, C ∈ {1} is ever going to yield a
 *    valid solution.
 */
public interface PartialChecker extends Checker {

    /**
     * This enumeration defines the possible outcomes of a check applied to some partial
     * assignment.
     *    - ACCEPT means that the checker established that all extensions of the current
     *             partial assignment will be accepted by the constraint.
     *    - REJECT means that the checker established that all extensions of the current
     *             partial assignment are bound to be rejected by the constraint
     *    - I_DONT_KNOW simply means that the checker wasn't able to draw any conclusive
     *             decision from the examination of the given partial assignment.
     */
    static enum PartialCheckResult {
        ACCEPT, REJECT, I_DONT_KNOW
    }

    /**
     * Tests whether or not the given partial assignment is to be accepted as a solution
     * by the represented constraint.
     */
    PartialCheckResult test(final PartialAssignment partialAssignment);

}
