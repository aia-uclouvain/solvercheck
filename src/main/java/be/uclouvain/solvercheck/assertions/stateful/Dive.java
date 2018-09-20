package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.core.data.Operator;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.StatefulFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;

public class Dive {
    /** The number of branches to explore until a leaf is reached */
    private final int nbDives;

    /** The actual StatefulFilter being tested. */
    private final StatefulFilter actual;

    /** The reference StatefulFilter with which to compare the resuts. */
    private final StatefulFilter other;

    /**
     * The condition effectively being checked when testing some partial
     * assignment.
     */
    private final Supplier<Boolean> check;

    private final Supplier<Integer>  variables;
    private final Function<Integer, Integer> values;
    private final Supplier<Operator> operators;
    private final Supplier<Boolean>  backtracks;

    private final List<DiveOperation> history;
    private final Stack<Branching> decisions;

    private final PartialAssignment root;


    public Dive(
            final int nbDives,
            final StatefulFilter actual,
            final StatefulFilter other,
            final Supplier<Boolean> check,
            final Supplier<Integer>  variables,
            final Function<Integer, Integer>  values,
            final Supplier<Operator> operators,
            final Supplier<Boolean>  backtracks,
            final PartialAssignment  root) {

        this.nbDives    = nbDives;
        this.actual     = actual;
        this.other      = other;
        this.check      = check;
        this.variables  = variables;
        this.values     = values;
        this.operators  = operators;
        this.backtracks = backtracks;
        this.root       = root;
        this.history    = new ArrayList<>();
        this.decisions  = new Stack<>();
    }

    public void run() {
        actual.setup(root);
        other .setup(root);

        for (int i = 0; i < nbDives; i++) {
            exploreOneBranch();
            doBacktrack();
        }
    }

    private void exploreOneBranch() {
        while (!actual.currentState().isLeaf()
            && !other.currentState().isLeaf()) {

            doPush();
            doDecide();
            doCheck();
        }
    }

    private void doPush() {
        history.add(Push.getInstance());
        actual.pushState();
        other.pushState();
    }
    private void doDecide() {
        int variable = variables.get();
        int value    = values.apply(variable);
        Operator op  = operators.get();

        Branching branch = new Branching(variable, op, value);
        history.add(branch);
        decisions.push(branch);

        actual.branchOn(variable, op, value);
        other .branchOn(variable, op, value);
    }
    private void doCheck() {
        final PartialAssignment actualPa = actual.currentState();
        final PartialAssignment otherPa  = other.currentState();

        if (!this.check.get()) {
            throw new AssertionError(scenario(actualPa, otherPa));
        }
    }
    private void doBacktrack() {
        while (!decisions.isEmpty() && backtracks.get()) {
            actual.popState();
            other .popState();

            decisions.pop();
        }
    }


    private String scenario(
            final PartialAssignment actualPa,
            final PartialAssignment otherPa) {

        final StringBuilder builder = new StringBuilder("\n");

        builder.append("########################### \n");
        builder.append("PROPERTY FALSIFIED \n");
        builder.append("########################### \n");
        builder.append("STARTING FROM DOMAINS: \n").append(root).append("\n");

        builder.append("########################### \n");
        builder.append("ACTUAL STATE : \n").append(actualPa).append("\n");
        builder.append("########################### \n");
        builder.append("EXPECTED STATE : \n").append(otherPa).append("\n");

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

}
