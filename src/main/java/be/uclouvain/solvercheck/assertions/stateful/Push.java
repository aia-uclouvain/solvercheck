package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.core.task.StatefulFilter;

final class Push implements DiveOperation {
    private static final Push INSTANCE = new Push();

    private Push() { }

    @Override
    public String toString() {
        return "Push";
    }

    public static Push getInstance() {
        return INSTANCE;
    }
}
