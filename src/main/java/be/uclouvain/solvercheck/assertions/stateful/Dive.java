package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.generators.Generators;
import org.quicktheories.core.Strategy;
import org.quicktheories.generators.SourceDSL;
import org.quicktheories.impl.Distribution;
import org.quicktheories.impl.Distributions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The purpose of this class is to actually run a 'dive' exploration of the
 * search tree rooted by some given partial assignment. It will explore nbDives
 * branches of a partial assignment until a leaf of the search tree is reached
 * or an error is encountered.
 */
/* package */ final class Dive implements Runnable {
    /** The assertion being verified during this 'dive' check. */
    private final DiveAssertion assertion;

    /**
     * This is the supplier used to randomly pick a variable from the
     * variables distribution. In some way, this is the variable decision
     * heuristic used in the dive tests.
     */
    private final Supplier<Integer>  variables;
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
    private final Supplier<Operator> operators;
    /**
     * This is the supplier used to randomly generate the booleans that are
     * used in determining the backtrack depth.
     */
    private final Supplier<Boolean>  backtracks;

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
     * @param assertion the assertion being verified during this 'dive' check.
     * @param strategy the ongoing strategy used by the underlying
     *                 QuickTheories layer. It is used to generate the
     *                 appropriate distributions from generators seeded with
     *                 the same seed as QuickTheories.
     * @param root the initial value of all the variables domains used as a
     *             basis for the search tree explored by this dive check.
     */
    /* package */ Dive(
            final DiveAssertion assertion,
            final Strategy strategy,
            final PartialAssignment  root) {

        this.assertion  = assertion;
        this.root       = root;

        this.variables  = variablesSupplier(strategy, root);
        this.values     = valuesSupplier(strategy, root);
        this.operators  = operatorSupplier(strategy);
        this.backtracks = backtrackSupplier(strategy);

        this.history    = new ArrayList<>();
        this.decisions  = new Stack<>();
    }

    /** {@inheritDoc} */
    public void run() {
        assertion.setup(root);

        for (int i = 0; i < assertion.getNbDives(); i++) {
            exploreOneBranch();
            doBacktrack();
        }
    }

    /**
     * Generate nodes along one branch of the search tree, testing the
     * whether the `check` condition holds at each node.
     */
    private void exploreOneBranch() {
        while (!assertion.isCurrentStateLeaf()) {
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
        assertion.pushState();
    }

    /**
     * Generates a branching decision, applies it to both stateful filters
     * and records it in the history.
     */
    private void doDecide() {
        int variable = variables.get();
        int value    = values.apply(variable);
        Operator op  = operators.get();

        Branching branch = new Branching(variable, op, value);
        history.add(branch);
        decisions.push(branch);

        assertion.branchOn(variable, op, value);
    }

    /**
     * Evaluates whether the `check` condition holds at the current node.
     * Whenever the `check` condition is falsified, an AssertionError is
     * raised describing the scenario that lead to this violation.
     */
    private void doCheck() {
        if (!assertion.checkTest()) {
            throw new AssertionError(scenario());
        }
    }

    /**
     * Backtrancks a (pseudo)-random number of nodes along one branch to try
     * and expand an other one.
     */
    private void doBacktrack() {
        while (!decisions.isEmpty() && backtracks.get()) {
            history.add(Pop.getInstance());
            assertion.popState();
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
        builder.append(assertion.actualState()).append("\n");
        builder.append("########################### \n");
        builder.append("EXPECTED STATE : \n");
        builder.append(assertion.otherState()).append("\n");

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
     * @param strategy the configuration of the underlying QuickTheories
     *                 layer. It is used to configure the data distributions
     *                 from which values are chosen.
     * @param forDomains the value of the variables domains at the root of the
     *                  search tree explored by the dive.
     * @return a supplier to pick variable identifiers from a boundary skewed
     * distribution.
     */
    private Supplier<Integer> variablesSupplier(
            final Strategy strategy,
            final PartialAssignment forDomains) {

        final Distribution<Integer> distrib =
                Distributions.boundarySkewed(
                        strategy,
                        SourceDSL.integers().between(0, forDomains.size() - 1));

        return () -> Distributions.nextValue(distrib);
    }
    /**
     * Instanciates a function to pick variable value from a
     * random distribution, given the identifier of some variable.
     *
     * @param strategy the configuration of the underlying QuickTheories
     *                 layer. It is used to configure the data distributions
     *                 from which values are chosen.
     * @param forDomains the value of the variables domains at the root of the
     *                  search tree explored by the dive.
     * @return a supplier to pick variable identifiers from a random
     * distribution.
     */
    private Function<Integer, Integer> valuesSupplier(
            final Strategy strategy,
            final PartialAssignment forDomains) {

        return xi -> Distributions.nextValue(
                valuesDistrib(strategy, forDomains, xi));
    }

    /**
     * Creates the value distribution relative to some identified vaiable `xi`.
     *
     * @param strategy the configuration of the underlying QuickTheories
     *                 layer. It is used to configure the data distributions
     *                 from which values are chosen.
     * @param inDomains the value of the variables domains at the root of the
     *                  search tree explored by the dive.
     * @param xi the identifier of the variable for wchich to create a value
     *           distribution.
     * @return a function that maps a variable identifier with a random
     * distribution of the values in the domain of that variable.
     */
    private Distribution<Integer> valuesDistrib(
            final Strategy strategy,
            final PartialAssignment inDomains,
            final int xi) {

        Domain dxi = inDomains.get(xi);
        return Distributions.random(
                strategy,
                SourceDSL.integers().between(dxi.minimum(), dxi.maximum()));
    }
    /**
     * Instanciates a supplier to pick an operator from a random distribution.
     *
     * @param strategy the configuration of the underlying QuickTheories
     *                 layer. It is used to configure the data distributions
     *                 from which values are chosen.
     * @return a supplier to pick an operator from a random distribution.
     */
    private Supplier<Operator> operatorSupplier(final Strategy strategy) {
        final Distribution<Operator> distrib =
                Distributions.random(strategy, Generators.operators());

        return () -> Distributions.nextValue(distrib);
    }
    /**
     * Instanciates a supplier that generates random booleans. These booleans
     * are used to determine the level up to which some dive should backtrack.
     *
     * @param strategy the configuration of the underlying QuickTheories
     *                 layer. It is used to configure the data distributions
     *                 from which values are chosen.
     * @return a supplier to pick a random boolean.
     */
    private Supplier<Boolean> backtrackSupplier(final Strategy strategy) {
        final Distribution<Boolean> distrib =
                Distributions.random(strategy, SourceDSL.booleans().all());

        return () -> Distributions.nextValue(distrib);
    }
}
