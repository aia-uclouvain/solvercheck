package be.uclouvain.solvercheck;

import be.uclouvain.solvercheck.assertions.WithAssertions;
import be.uclouvain.solvercheck.checkers.WithCheckers;
import be.uclouvain.solvercheck.generators.WithCpGenerators;
import be.uclouvain.solvercheck.generators.WithJavaGenerators;

/**
 * This interface collects all the useful methods that have been defined in
 * the project fluent interfaces. The easiest way to get yourself started with
 * SolverCheck is to "implement" this interface (it is really just about
 * putting the keyword: your class does not have to provide any code in
 * order to implement the interface). As a result, your implementing class
 * will have an immediate access to all the methods required to write
 * property-based tests for your constraints.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithSolverCheck
    extends
        WithAssertions,
        WithCpGenerators, WithJavaGenerators,
        WithCheckers {
}
