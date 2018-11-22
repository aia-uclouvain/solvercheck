package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.fuzzing.Generators;
import be.uclouvain.solvercheck.fuzzing.Randomness;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

/**
 * The purpose of this class is to actually run a 'dive' exploration of the
 * search tree rooted by some given partial assignment. It will explore nbDives
 * branches of a partial assignment until a leaf of the search tree is reached
 * or an error is encountered.
 */
/* package */ final class Dive implements Runnable {
    /** The source of randomness used to pick random values during search. */
    private final Randomness randomness;

    /** The property being verified during this 'dive' check. */
    private final StatefulProperties.Property property;

    /** The number of 'dives' to perform (the number of branches to explore). */
    private final int nbDives;

    /**
     * This is the supplier used to randomly pick a variable from the
     * variables distribution. In some way, this is the variable decision
     * heuristic used in the dive tests.
     */
    private final Iterator<Integer> variables;
    /**
     * This is the supplier used to randomly pick a value from the domain of a
     * variable. In some way, this is the variable decision heuristic used in
     * the dive tests.
     */
    private final Function<Integer, Integer> values;
    /**
     * This is the supplier used to randomly chose an operator to apply to
     * restrict variable domains.
     */
    private final Iterator<Operator> operators;
    /**
     * This is the supplier used to randomly generate the booleans that are
     * used in determining the backtrack depth.
     */
    private final Iterator<Boolean>  backtracks;

    /**
     * This is a complete history (all operations are logged) of the
     * operations that have been performed during this dive test.
     */
    private final List<DiveOperation> history;
    /**
     * This is the fraction of the history showing only the decisions made on
     * the last (current) explored branch. In case the full history is too
     * cumbersome for an user to understand, this might provide a hint to
     * start investigation of the reported violation.
     */
    private final Stack<Branching> decisions;

    /**
     * This is the initial value of all the variables domains used as a basis
     * for the search tree explored by this dive check.
     */
    private final PartialAssignment root;

    /**
     * Configures and instanciates a new runnable dive.
     *
     * @param randomness the source of randomness used for the fuzzing.
     * @param property the property being verified during this 'dive' check.
     * @param nbDives  the number of branches to explore until a leaf is
     *                 reached.
     * @param root     the initial value of all the variables domains used as a
     *                 basis for the search tree explored by this dive check.
     */
    /* package */ Dive(
       final Randomness randomness,
       final StatefulProperties.Property property,
       final int nbDives,
       final PartialAssignment  root) {
        this.randomness = randomness;
        this.property   = property;
        this.nbDives    = nbDives;
        this.root       = root;

        this.variables  = variables(root);
        this.values     = values(root);
        this.operators  = Generators.operators(randomness).iterator();
        this.backtracks = Generators.booleans(randomness).iterator();

        this.history    = new ArrayList<>();
        this.decisions  = new Stack<>();
    }

    /** {@inheritDoc} */
    public void run() {
        try {
            property.setup(root);
            doCheck();

            for (int i = 0; i < nbDives; i++) {
                exploreOneBranch();
                doBacktrack();
            }
        } catch (Throwable problem) {
            throw new AssertionError(
               problem.getMessage() + "\n" + scenario(),
               problem);
        }
    }

    /**
     * Generate nodes along one branch of the search tree, testing the
     * whether the `check` condition holds at each node.
     */
    private void exploreOneBranch() {
        while (!property.isCurrentStateLeaf()) {
            doPush();
            doDecide();
            doCheck();
        }
    }

    /**
     * Pushes the state of both stateful filters and records the operation in
     * the history.
     */
    private void doPush() {
        history.add(Push.getInstance());
        property.pushState();
    }

    /**
     * Generates a branching decision, applies it to both stateful filters
     * and records it in the history.
     */
    private void doDecide() {
        int variable = variables.next();
        int value    = values.apply(variable);
        Operator op  = operators.next();

        Branching branch = new Branching(variable, op, value);
        history.add(branch);
        decisions.push(branch);

        property.branchOn(variable, op, value);
    }

    /**
     * Evaluates whether the `check` condition holds at the current node.
     * Whenever the `check` condition is falsified, an AssertionError is
     * raised describing the scenario that lead to this violation.
     */
    private void doCheck() {
        if (!property.checkTest()) {
            throw new AssertionError("Found a counterexample of the property");
        }
    }

    /**
     * Backtrancks a (pseudo)-random number of nodes along one branch to try
     * and expand an other one.
     */
    private void doBacktrack() {
        while (!decisions.isEmpty() && backtracks.next()) {
            history.add(Pop.getInstance());
            property.popState();
            decisions.pop();
        }
    }

    /**
     * Generates a string describing the scenario that led to a violation of
     * the `check` property.
     *
     * @return a string describing the scenario that led to a violation of
     * the `check` property.
     */
    private String scenario() {

        final StringBuilder builder = new StringBuilder("\n");

        builder.append("########################### \n");
        builder.append("PROPERTY FALSIFIED \n");
        builder.append("########################### \n");
        builder.append("STARTING FROM DOMAINS: \n").append(root).append("\n");

        builder.append("########################### \n");
        builder.append("ACTUAL STATE : \n");
        builder.append(property.actualState()).append("\n");
        builder.append("########################### \n");
        builder.append("EXPECTED STATE : \n");
        builder.append(property.expectedState()).append("\n");

        builder.append("########################### \n");
        builder.append("BRANCH THAT LED TO FAILURE: \n");
        for (Branching decision : decisions) {
            builder.append("    ").append(decision).append("\n");
        }

        builder.append("########################### \n");
        builder.append("COMPLETE PATH TO FAILURE: \n");

        for (DiveOperation op : history) {
            builder.append("    ").append(op).append("\n");
        }
        builder.append("########################### \n");

        return builder.toString();
    }

    /**
     * Instanciates a supplier to pick variable identifiers from a boundary
     * skewed distribution.
     *
     * @param forDomains the value of the variables domains at the root of the
     *                  search tree explored by the dive.
     * @return a supplier to pick variable identifiers from a boundary skewed
     * distribution.
     */
    private Iterator<Integer> variables(final PartialAssignment forDomains) {
        return randomness
           .intsBetween(0, forDomains.size() - 1)
           .iterator();
    }

    /**
     * Instanciates a function to pick variable value from a
     * random distribution, given the identifier of some variable.
     *
     * @param forDomains the value of the variables domains at the root of the
     *                  search tree explored by the dive.
     * @return a supplier to pick variable identifiers from a random
     * distribution.
     */
    private Function<Integer, Integer> values(final PartialAssignment forDomains) {
        return xi -> {
            final Domain dxi = forDomains.get(xi);
            return randomness.randomInt(dxi.minimum(), dxi.maximum());
        };
    }
}
