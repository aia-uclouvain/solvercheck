package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;

import java.util.List;

/**
 * The point of this factory is to create assignments. Note, this class is mostly present to ensure
 * the style-consistency with other classes {@see DomainFactory, PartialAssignmentFactory}.
 */
public final class AssignmentFactory {

    /** An utility class has no public constructor */
    private AssignmentFactory(){}

    /** Creates a new Assignement from the given list of domains */
    public static Assignment from(final List<Integer> domains) {
        return new BasicAssignment(domains);
    }

    /** Creates a new Assignement from the given fixed partial assignment */
    public static Assignment from(final PartialAssignment partial) {
        return partial.asAssignment();
    }

}
